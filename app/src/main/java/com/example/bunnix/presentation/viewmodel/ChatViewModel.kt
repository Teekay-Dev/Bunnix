package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.Message
import com.example.bunnix.domain.repository.AuthRepository
import com.example.bunnix.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

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

    // Current User ID (for detail screen logic)
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        loadCurrentUserChats()
    }

    // ===== LOGIC =====

    fun loadCurrentUserChats() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.getCurrentUser()) {
                is AuthResult.Success -> {
                    val userId = result.data?.userId
                    if (userId != null) {
                        _currentUserId.value = userId
                        observeChatsInternal(userId)
                    } else {
                        _error.value = "User not found"
                        _isLoading.value = false
                    }
                }
                is AuthResult.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
                else -> _isLoading.value = false
            }
        }
    }

    private fun observeChatsInternal(userId: String) {
        viewModelScope.launch {
            chatRepository.observeUserChats(userId)
                .onStart { _isLoading.value = true }
                .catch { e -> _error.value = e.message; _isLoading.value = false }
                .collect { chats ->
                    _userChats.value = chats
                    _isLoading.value = false
                }
        }
    }

    // --- Detail Screen Methods ---

    fun observeChatMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.observeChatMessages(chatId)
                .onStart { _isLoadingMessages.value = true }
                .catch { _isLoadingMessages.value = false }
                .collect { messages ->
                    _chatMessages.value = messages
                    _isLoadingMessages.value = false
                }
        }
    }

    fun sendTextMessage(chatId: String, senderId: String, senderName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isSendingMessage.value = true
            chatRepository.sendMessage(chatId, senderId, senderName, text)
            _messageSent.value = true
            _isSendingMessage.value = false
        }
    }

    fun markMessagesAsRead(chatId: String, userId: String) {
        viewModelScope.launch {
            chatRepository.markMessagesAsRead(chatId, userId)
        }
    }

    fun resetMessageSent() {
        _messageSent.value = false
    }
}