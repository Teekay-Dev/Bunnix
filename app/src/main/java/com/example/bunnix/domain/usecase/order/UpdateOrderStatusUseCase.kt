package com.example.bunnix.domain.usecase.order

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Order
import com.example.bunnix.domain.repository.OrderRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * CORRECTED - Payment on Delivery Flow
 * Processing → Shipped → Delivered → Awaiting Payment → Payment Confirmed
 */
class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        orderId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Order> {
        // CORRECTED - Valid statuses for Payment on Delivery
        val validStatuses = listOf(
            "Processing",
            "Shipped",
            "Delivered",
            "Awaiting Payment",     // After delivery
            "Payment Confirmed",
            "Cancelled"
        )

        if (newStatus !in validStatuses) {
            return AuthResult.Error("Invalid order status")
        }

        val result = orderRepository.updateOrderStatus(orderId, newStatus, vendorId)

        // Notify customer of status change
        if (result is AuthResult.Success) {
            notificationRepository.notifyOrderStatusChanged(
                customerId = result.data.customerId,
                orderId = orderId,
                orderNumber = result.data.orderNumber,
                newStatus = newStatus
            )
        }

        return result
    }
}
