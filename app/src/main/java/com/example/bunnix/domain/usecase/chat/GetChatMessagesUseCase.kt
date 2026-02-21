package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.data.auth.getOrNull
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.database.models.Message
import com.example.bunnix.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): Result<List<Message>> {
        return try {
            val authResult = chatRepository.getChatMessages(chatId)
            when {
                authResult.isSuccess() -> Result.success(authResult.getOrNull() ?: emptyList())
                else -> Result.failure(Exception("Failed to get messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}