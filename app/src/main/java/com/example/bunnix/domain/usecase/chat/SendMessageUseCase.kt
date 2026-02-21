package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.database.models.Message
import com.example.bunnix.domain.repository.ChatRepository
import com.example.bunnix.domain.repository.NotificationRepository
import com.google.firebase.Timestamp
import java.util.UUID
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
        content: String,
        imageUrl: String? = null
    ): Result<Message> {
        if (content.isBlank()) {
            return Result.failure(Exception("Message cannot be empty"))
        }

        return try {
            val message = Message(
                messageId = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                content = content,
                imageUrl = imageUrl,
                type = if (imageUrl != null) "image" else "text",
                isRead = false,
                timestamp = Timestamp.now()
            )

            val authResult = chatRepository.sendMessage(
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = content
            )

            if (authResult.isSuccess()) {
                notificationRepository.notifyNewMessage(
                    userId = receiverId,
                    senderName = senderName,
                    messagePreview = content,
                    chatId = chatId
                )
                Result.success(message)
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}