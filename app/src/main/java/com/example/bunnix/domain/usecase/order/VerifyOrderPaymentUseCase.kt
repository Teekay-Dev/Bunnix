package com.example.bunnix.domain.usecase.order

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.domain.repository.OrderRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

class VerifyOrderPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        orderId: String,
        vendorId: String
    ): AuthResult<Unit> {
        val result = orderRepository.verifyPayment(orderId, vendorId)
        
        // Notify customer that payment is verified
        if (result is AuthResult.Success) {
            val orderResult = orderRepository.getOrder(orderId)
            if (orderResult is AuthResult.Success) {
                notificationRepository.notifyPaymentVerified(
                    customerId = orderResult.data.customerId,
                    orderId = orderId,
                    orderNumber = orderResult.data.orderNumber
                )
            }
        }
        
        return result
    }
}
