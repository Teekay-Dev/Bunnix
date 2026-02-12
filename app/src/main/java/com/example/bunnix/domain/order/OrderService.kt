package com.example.bunnix.domain.order

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager

class OrderService(
    private val authManager: AuthManager,
    private val userModeManager: UserModeManager,
    private val repository: OrderRepository,
    private val stateMachine: OrderStateMachine
) {

    suspend fun updateStatus(orderId: String, newStatus: OrderStatus): Result<Order> {

        val order = repository.getById(orderId)
            ?: return Result.failure(Exception("Order not found"))

        val actorUid = authManager.currentUserUid()
        val actorMode = userModeManager.getMode()

        // Ownership check
        if (actorMode == UserMode.CUSTOMER && order.customerId != actorUid)
            return Result.failure(SecurityException("Unauthorized"))

        if (actorMode == UserMode.VENDOR && order.vendorId != actorUid)
            return Result.failure(SecurityException("Unauthorized"))

        // State validation
        if (!stateMachine.canTransition(order.status, newStatus, actorMode))
            return Result.failure(IllegalStateException("Invalid state transition"))

        val updatedOrder = order.copy(status = newStatus)
        repository.update(updatedOrder)

        return Result.success(updatedOrder)
    }
}
