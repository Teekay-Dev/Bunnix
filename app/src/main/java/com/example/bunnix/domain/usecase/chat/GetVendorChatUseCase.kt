package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.database.models.Chat
import com.example.bunnix.domain.repository.ChatRepository
import javax.inject.Inject

class GetVendorChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(vendorId: String): Result<List<Chat>> {
        return try {
            chatRepository.getVendorChats(vendorId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
