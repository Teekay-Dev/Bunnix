package com.example.bunnix.domain.order

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager
import com.example.bunnix.model.data.auth.AuthManager

class ConfirmOrderPaymentUseCase(
    private val authManager: AuthManager,
    private val userModeManager: UserModeManager,
    private val orderRepository: OrderRepository,
    private val stateMachine: OrderStateMachine
) {

    suspend fun confirm(orderId: String): Result<Order> {

        if (userModeManager.getMode() != UserMode.VENDOR)
            return Result.failure(SecurityException("Only vendors can confirm payments"))

        val order = orderRepository.getById(orderId)
            ?: return Result.failure(Exception("Order not found"))

        if (order.vendorId != authManager.currentUserUid())
            return Result.failure(SecurityException("Unauthorized"))

        if (!stateMachine.canTransition(
                order.status,
                OrderStatus.PAYMENT_CONFIRMED,
                UserMode.VENDOR
            )
        ) {
            return Result.failure(IllegalStateException("Invalid payment confirmation"))
        }

        val updated = order.copy(status = OrderStatus.PAYMENT_CONFIRMED)
        orderRepository.update(updated)

        return Result.success(updated)
    }
}
