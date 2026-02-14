package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Message
import com.example.bunnix.domain.repository.ChatRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        chatId: String,
        senderId: String,
        senderName: String,
        receiverId: String,
        text: String
    ): AuthResult<Message> {
        if (text.isBlank()) {
            return AuthResult.Error("Message cannot be empty")
        }
        
        val result = chatRepository.sendMessage(
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            text = text
        )
        
        // Notify receiver
        if (result is AuthResult.Success) {
            notificationRepository.notifyNewMessage(
                userId = receiverId,
                senderName = senderName,
                messagePreview = text,
                chatId = chatId
            )
        }
        
        return result
    }
}
