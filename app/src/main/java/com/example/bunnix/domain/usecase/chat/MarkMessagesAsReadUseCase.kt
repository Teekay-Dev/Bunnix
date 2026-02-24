package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.data.auth.getErrorMessage
import com.example.bunnix.domain.repository.ChatRepository
import javax.inject.Inject

class MarkMessagesAsReadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String,
        userId: String
    ): Result<Unit> {
        return try {
            val authResult = chatRepository.markMessagesAsRead(chatId, userId)
            when {
                authResult.isSuccess() -> Result.success(Unit)
                else -> Result.failure(Exception(authResult.getErrorMessage() ?: "Failed to mark messages as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}