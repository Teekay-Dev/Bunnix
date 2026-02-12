package com.example.bunnix.domain.chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.order.OrderRepository
import java.time.Instant
import java.util.UUID

class GetOrCreateOrderConversationUseCase(
    private val authManager: AuthManager,
    private val orderRepository: OrderRepository,
    private val conversationRepository: ConversationRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(orderId: String): Result<Conversation> {

        val order = orderRepository.getById(orderId)
            ?: return Result.failure(Exception("Order not found"))

        val uid = authManager.currentUserId()

        if (uid != order.customerId && uid != order.vendorId)
            return Result.failure(SecurityException("Unauthorized"))

        val existing = conversationRepository.getByReference(
            ConversationType.ORDER,
            orderId
        )

        if (existing != null) return Result.success(existing)

        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            type = ConversationType.ORDER,
            referenceId = orderId,
            customerId = order.customerId,
            vendorId = order.vendorId,
            createdAt = Instant.now()
        )

        return Result.success(conversationRepository.create(conversation))
    }
}
