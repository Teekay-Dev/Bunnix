package com.example.bunnix.model.domain.chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.model.data.auth.AuthManager
import java.time.Instant
import java.util.UUID

class SendMessageUseCase(
    private val authManager: AuthManager,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: ChatMessageRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun send(
        conversation: Conversation,
        text: String
    ): Result<ChatMessage> {

        val senderId = authManager.currentUserUid()

        if (senderId != conversation.customerId &&
            senderId != conversation.vendorId
        ) {
            return Result.failure(SecurityException("Unauthorized sender"))
        }

        if (text.isBlank())
            return Result.failure(IllegalArgumentException("Empty message"))

        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversation.id,
            senderId = senderId,
            message = text.trim(),
            sentAt = Instant.now()
        )

        return Result.success(messageRepository.send(message))
    }
}
