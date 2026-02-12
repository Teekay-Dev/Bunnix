package com.example.bunnix.domain.payment

import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.domain.order.Order
import com.example.bunnix.domain.order.OrderRepository
import com.example.bunnix.domain.order.OrderStatus
import com.example.bunnix.model.data.auth.AuthManager

class VerifyPaymentUseCase(
    private val auth: AuthManager,
    private val paymentRepo: PaymentRepository,
    private val orderRepo: OrderRepository,
    private val eventBus: EventBus
) {
    suspend fun execute(orderId: String): Result<Order> {
        val vendorId = auth.currentUserUid()

        val success = paymentRepo.verifyPayment(orderId, vendorId)
        if (!success) return Result.failure(Exception("Verification failed"))

        // Fetch the order
        val order = orderRepo.getById(orderId) ?: return Result.failure(Exception("Order not found"))

        // Update order status
        val updatedOrder = order.copy(status = OrderStatus.PAYMENT_CONFIRMED)
        val savedOrder = orderRepo.update(updatedOrder)

        // Emit event
        eventBus.emit(DomainEvent.PaymentConfirmed(orderId, vendorId))

        return Result.success(savedOrder)
    }
}

