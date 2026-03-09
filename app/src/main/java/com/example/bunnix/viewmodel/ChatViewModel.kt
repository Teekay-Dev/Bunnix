package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class Conversation(
    val chatId: String,
    val customerName: String,
    val customerAvatar: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val isOnline: Boolean = false
)

data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _totalUnreadCount = MutableStateFlow(0)
    val totalUnreadCount: StateFlow<Int> = _totalUnreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var messagesListener: ListenerRegistration? = null
    private var unreadCountListener: ListenerRegistration? = null

    fun loadConversations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                firestore.collection("chats")
                    .whereArrayContains("participants", vendorId)
                    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _isLoading.value = false
                            return@addSnapshotListener
                        }

                        viewModelScope.launch {
                            val conversationsList = mutableListOf<Conversation>()

                            snapshot?.documents?.forEach { doc ->
                                try {
                                    val participants = doc.get("participants") as? List<*>
                                    val customerId = participants?.firstOrNull { it != vendorId } as? String
                                        ?: return@forEach

                                    val customerDoc = firestore.collection("users")
                                        .document(customerId)
                                        .get()
                                        .await()

                                    conversationsList.add(
                                        Conversation(
                                            chatId = doc.id,
                                            customerName = customerDoc.getString("name") ?: "Unknown",
                                            customerAvatar = customerDoc.getString("profilePicUrl") ?: "",
                                            lastMessage = doc.getString("lastMessage") ?: "",
                                            lastMessageTime = doc.getLong("lastMessageTime") ?: 0L,
                                            unreadCount = doc.getLong("vendorUnreadCount")?.toInt() ?: 0
                                        )
                                    )
                                } catch (e: Exception) {
                                    // Skip this conversation
                                }
                            }

                            _conversations.value = conversationsList
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun loadMessages(chatId: String) {
        messagesListener?.remove()

        messagesListener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messagesList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ChatMessage(
                            messageId = doc.id,
                            senderId = doc.getString("senderId") ?: "",
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                _messages.value = messagesList

                // Mark messages as read
                markMessagesAsRead(chatId)
            }
    }

    fun sendMessage(chatId: String, text: String) {
        viewModelScope.launch {
            try {
                val vendorId = auth.currentUser?.uid ?: return@launch
                val timestamp = System.currentTimeMillis()

                // Add message to subcollection
                val messageData = hashMapOf(
                    "senderId" to vendorId,
                    "text" to text,
                    "timestamp" to timestamp,
                    "isRead" to false
                )

                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .add(messageData)
                    .await()

                // Update chat metadata
                val chatUpdate = hashMapOf(
                    "lastMessage" to text,
                    "lastMessageTime" to timestamp,
                    "lastMessageSenderId" to vendorId,
                    "customerUnreadCount" to FieldValue.increment(1)
                )

                firestore.collection("chats")
                    .document(chatId)
                    .update(chatUpdate as Map<String, Any>)
                    .await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun markMessagesAsRead(chatId: String) {
        viewModelScope.launch {
            try {
                val vendorId = auth.currentUser?.uid ?: return@launch

                // Reset vendor unread count
                firestore.collection("chats")
                    .document(chatId)
                    .update("vendorUnreadCount", 0)
                    .await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                val vendorId = auth.currentUser?.uid ?: return@launch

                unreadCountListener?.remove()

                unreadCountListener = firestore.collection("chats")
                    .whereArrayContains("participants", vendorId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener

                        var totalUnread = 0
                        snapshot?.documents?.forEach { doc ->
                            totalUnread += doc.getLong("vendorUnreadCount")?.toInt() ?: 0
                        }

                        _totalUnreadCount.value = totalUnread
                    }

            } catch (e: Exception) {
                _totalUnreadCount.value = 0
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredConversations(): List<Conversation> {
        val query = _searchQuery.value.lowercase()
        if (query.isEmpty()) return _conversations.value

        return _conversations.value.filter {
            it.customerName.lowercase().contains(query) ||
                    it.lastMessage.lowercase().contains(query)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesListener?.remove()
        unreadCountListener?.remove()
    }
}