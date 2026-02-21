package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.usecase.chat.*
import com.example.bunnix.vendorUI.screens.vendor.messages.ChatMessage
import com.google.ai.edge.litertlm.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = false,
    val chats: List<Conversation> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getVendorChatsUseCase: GetVendorChatsUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadChats(vendorId: String) {
        viewModelScope.launch {
            getVendorChatsUseCase(vendorId)
                .onSuccess { chats ->
                    // Map to UI model
                }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            getChatMessagesUseCase(chatId)
                .onSuccess { messages ->
                    // Map to UI model
                }
        }
    }

    fun sendMessage(chatId: String, content: String) {
        viewModelScope.launch {
            // Create message object and send
        }
    }

    // ✅ FIXED: added userId parameter
    fun markAsRead(chatId: String, userId: String) {
        viewModelScope.launch {
            markMessagesAsReadUseCase(chatId, userId)
        }
    }
}