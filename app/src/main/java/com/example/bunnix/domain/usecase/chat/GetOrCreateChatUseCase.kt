package com.example.bunnix.domain.usecase.chat

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Chat
import com.example.bunnix.domain.repository.ChatRepository
import javax.inject.Inject

class GetOrCreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        userId1: String,
        userId2: String,
        user1Name: String,
        user2Name: String,
        user1IsVendor: Boolean,
        user2IsVendor: Boolean,
        relatedOrderId: String = "",
        relatedBookingId: String = ""
    ): AuthResult<Chat> {
        return chatRepository.getOrCreateChat(
            userId1 = userId1,
            userId2 = userId2,
            user1Name = user1Name,
            user2Name = user2Name,
            user1IsVendor = user1IsVendor,
            user2IsVendor = user2IsVendor,
            relatedOrderId = relatedOrderId,
            relatedBookingId = relatedBookingId
        )
    }
}
