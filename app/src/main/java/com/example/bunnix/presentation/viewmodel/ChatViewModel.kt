package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.firebase.FirebaseManager
import com.example.bunnix.database.firebase.collections.ChatCollection
import com.example.bunnix.database.firebase.collections.VendorProfileCollection
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.Message
import com.example.bunnix.database.models.ParticipantInfo
import com.example.bunnix.database.models.VendorProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    // ===== FIREBASE & USER =====
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseManager.getCurrentUserId()

    // ===== CHAT LIST STATE =====
    private val _userChats = MutableStateFlow<List<Chat>>(emptyList())
    val userChats: StateFlow<List<Chat>> = _userChats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ===== CHAT DETAIL STATE =====
    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    val chatMessages: StateFlow<List<Message>> = _chatMessages.asStateFlow()

    private val _isLoadingMessages = MutableStateFlow(false)
    val isLoadingMessages: StateFlow<Boolean> = _isLoadingMessages.asStateFlow()

    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage: StateFlow<Boolean> = _isSendingMessage.asStateFlow()

    private val _messageSent = MutableStateFlow(false)
    val messageSent: StateFlow<Boolean> = _messageSent.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(userId)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _vendorProfile = MutableStateFlow<VendorProfile?>(null)
    val vendorProfile: StateFlow<VendorProfile?> = _vendorProfile.asStateFlow()

    // ===== UNREAD COUNT STATE (for bottom nav badge) =====
    private val _totalUnreadCount = MutableStateFlow(0)
    val totalUnreadCount: StateFlow<Int> = _totalUnreadCount.asStateFlow()

    init {
        loadCurrentUserChats()
    }

    // ===== CHAT LIST FUNCTIONS =====

    fun loadCurrentUserChats() {
        viewModelScope.launch {
            if (userId == null) {
                _error.value = "User not logged in"
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            try {
                ChatCollection.getUserChats(userId).collectLatest { chats ->
                    _userChats.value = chats

                    // Calculate total unread count
                    val unread = chats.sumOf { it.unreadCount[userId] ?: 0 }
                    _totalUnreadCount.value = unread

                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load chats"
                _isLoading.value = false
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            if (userId == null) return@launch

            try {
                ChatCollection.getUserChats(userId).collectLatest { chats ->
                    val unread = chats.sumOf { it.unreadCount[userId] ?: 0 }
                    _totalUnreadCount.value = unread
                }
            } catch (e: Exception) {
                _totalUnreadCount.value = 0
            }
        }
    }

    // ===== CHAT DETAIL FUNCTIONS =====

    fun observeChatMessages(chatId: String) {
        viewModelScope.launch {
            _isLoadingMessages.value = true
            ChatCollection.getMessages(chatId).collectLatest { messages ->
                _chatMessages.value = messages
                _isLoadingMessages.value = false
            }
        }
    }

    fun getOrCreateChat(
        currentUserId: String,
        vendorId: String,
        vendorName: String,
        vendorImage: String,
        onResult: (String) -> Unit
    ) {
        val chatId = if (currentUserId < vendorId)
            "$currentUserId-$vendorId"
        else
            "$vendorId-$currentUserId"

        val chatRef = firestore.collection("chats").document(chatId)

        chatRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                onResult(chatId)
            } else {
                val newChat = Chat(
                    chatId = chatId,
                    participants = listOf(currentUserId, vendorId),
                    participantDetails = mapOf(
                        currentUserId to ParticipantInfo("You", "", false),
                        vendorId to ParticipantInfo(vendorName, vendorImage, true)
                    ),
                    lastMessage = "",
                    unreadCount = mapOf(
                        currentUserId to 0,
                        vendorId to 0
                    )
                )

                chatRef.set(newChat)
                    .addOnSuccessListener { onResult(chatId) }
            }
        }
    }

    fun loadVendorProfile(vendorId: String) {
        viewModelScope.launch {
            _vendorProfile.value = VendorProfileCollection.getVendorProfile(vendorId).getOrNull()
        }
    }

    fun sendTextMessage(chatId: String, senderId: String, senderName: String, text: String) {
        viewModelScope.launch {
            _isSendingMessage.value = true
            val result = ChatCollection.sendMessage(
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = text,
                messageType = "text"
            )
            if (result.isSuccess) {
                _messageSent.value = true
            }
            _isSendingMessage.value = false
        }
    }

    fun sendImageMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            _isSendingMessage.value = true
            ChatCollection.sendMessage(
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = "📷 Image",
                imageUrl = imageUrl,
                messageType = "image"
            )
            _isSendingMessage.value = false
        }
    }

    fun sendVoiceMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        audioUrl: String
    ) {
        viewModelScope.launch {
            _isSendingMessage.value = true
            ChatCollection.sendMessage(
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = "🎤 Voice Note",
                imageUrl = audioUrl,
                messageType = "voice"
            )
            _isSendingMessage.value = false
        }
    }

    fun markMessagesAsRead(chatId: String, userId: String) {
        viewModelScope.launch {
            ChatCollection.markMessagesAsRead(chatId, userId)
        }
    }

    fun resetMessageSent() {
        _messageSent.value = false
    }
}