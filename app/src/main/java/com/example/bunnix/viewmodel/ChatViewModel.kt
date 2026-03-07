package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.messages.ChatMessage
import com.example.bunnix.vendorUI.screens.vendor.messages.Conversation
import com.example.bunnix.vendorUI.screens.vendor.messages.CustomerInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                // Get all chats where vendor is a participant
                val chatsSnapshot = firestore.collection("chats")
                    .whereArrayContains("participants", vendorId)
                    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val conversations = mutableListOf<Conversation>()

                for (doc in chatsSnapshot.documents) {
                    try {
                        val participants = doc.get("participants") as? List<String> ?: continue
                        val customerId = participants.firstOrNull { it != vendorId } ?: continue

                        // Get customer info
                        val userDoc = firestore.collection("users")
                            .document(customerId)
                            .get()
                            .await()

                        val customerName = userDoc.getString("name") ?: "Unknown User"
                        val customerImageUrl = userDoc.getString("profilePicUrl") ?: ""

                        // Get last message
                        val lastMessage = doc.getString("lastMessage") ?: "No messages yet"
                        val lastMessageTime = doc.getTimestamp("lastMessageTime")
                        val timeAgo = lastMessageTime?.let { calculateTimeAgo(it.toDate()) } ?: "Just now"

                        // Check unread count for vendor
                        val unreadCount = doc.getLong("vendorUnreadCount")?.toInt() ?: 0

                        conversations.add(
                            Conversation(
                                chatId = doc.id,
                                customerName = customerName,
                                customerImageUrl = customerImageUrl,
                                lastMessage = lastMessage,
                                timeAgo = timeAgo,
                                hasUnread = unreadCount > 0,
                                unreadCount = unreadCount,
                                isOnline = false // TODO: Implement online status
                            )
                        )
                    } catch (e: Exception) {
                        continue
                    }
                }

                _conversations.value = conversations.filter {
                    if (_searchQuery.value.isBlank()) {
                        true
                    } else {
                        it.customerName.contains(_searchQuery.value, ignoreCase = true) ||
                                it.lastMessage.contains(_searchQuery.value, ignoreCase = true)
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load conversations"
                _conversations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                // Listen for real-time updates
                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _error.value = error.message
                            return@addSnapshotListener
                        }

                        val messages = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val senderId = doc.getString("senderId") ?: return@mapNotNull null
                                val text = doc.getString("text") ?: return@mapNotNull null
                                val timestamp = doc.getTimestamp("timestamp")

                                val formattedTime = timestamp?.let {
                                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it.toDate())
                                } ?: "Now"

                                ChatMessage(
                                    messageId = doc.id,
                                    text = text,
                                    timestamp = formattedTime,
                                    isVendor = senderId == vendorId
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()

                        _messages.value = messages
                    }

                // Mark messages as read
                firestore.collection("chats")
                    .document(chatId)
                    .update("vendorUnreadCount", 0)
                    .await()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load messages"
                _messages.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCustomerInfo(chatId: String) {
        viewModelScope.launch {
            try {
                val vendorId = auth.currentUser?.uid ?: return@launch

                val chatDoc = firestore.collection("chats")
                    .document(chatId)
                    .get()
                    .await()

                val participants = chatDoc.get("participants") as? List<String> ?: return@launch
                val customerId = participants.firstOrNull { it != vendorId } ?: return@launch

                val userDoc = firestore.collection("users")
                    .document(customerId)
                    .get()
                    .await()

                _customerInfo.value = CustomerInfo(
                    name = userDoc.getString("name") ?: "Unknown User",
                    imageUrl = userDoc.getString("profilePicUrl") ?: "",
                    isOnline = false // TODO: Implement online status
                )

            } catch (e: Exception) {
                _error.value = "Failed to load customer info"
            }
        }
    }

    fun sendMessage(chatId: String) {
        viewModelScope.launch {
            try {
                val text = _messageText.value.trim()
                if (text.isEmpty()) return@launch

                val vendorId = auth.currentUser?.uid ?: return@launch

                // Create message document
                val messageData = hashMapOf(
                    "senderId" to vendorId,
                    "text" to text,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "isRead" to false
                )

                // Add message to subcollection
                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .add(messageData)
                    .await()

                // Update chat metadata
                firestore.collection("chats")
                    .document(chatId)
                    .update(
                        mapOf(
                            "lastMessage" to text,
                            "lastMessageTime" to FieldValue.serverTimestamp(),
                            "lastMessageSenderId" to vendorId,
                            "customerUnreadCount" to FieldValue.increment(1)
                        )
                    )
                    .await()

                // Clear input
                _messageText.value = ""

            } catch (e: Exception) {
                _error.value = "Failed to send message"
            }
        }
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadConversations() // Reload with filter
    }

    private fun calculateTimeAgo(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time

        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 7 -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            else -> "Just now"
        }
    }

    fun refresh() {
        loadConversations()
    }
}