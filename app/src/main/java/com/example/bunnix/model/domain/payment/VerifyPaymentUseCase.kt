package com.example.bunnix.model.domain.payment

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.model.data.auth.AuthManager
import com.example.bunnix.model.domain.events.DomainEvent
import com.example.bunnix.model.domain.events.EventBus
import com.example.bunnix.model.domain.order.Order
import com.example.bunnix.model.domain.order.OrderRepository
import com.example.bunnix.model.domain.order.OrderStateMachine
import com.example.bunnix.model.domain.order.OrderStatus
import com.example.bunnix.model.domain.user.UserMode
import com.example.bunnix.model.domain.user.UserModeManager
import java.time.Instant

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

