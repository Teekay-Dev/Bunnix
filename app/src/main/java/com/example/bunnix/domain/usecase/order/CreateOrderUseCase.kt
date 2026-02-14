package com.example.bunnix.domain.usecase.order

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Order
import com.example.bunnix.domain.repository.OrderRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        customerId: String,
        customerName: String,
        vendorId: String,
        vendorName: String,
        items: List<Map<String, Any>>,
        totalAmount: Double,
        deliveryAddress: String,
        paymentMethod: String
    ): AuthResult<Order> {
        // Validation
        if (items.isEmpty()) {
            return AuthResult.Error("Order must contain at least one item")
        }
        
        if (totalAmount <= 0) {
            return AuthResult.Error("Invalid order amount")
        }
        
        if (deliveryAddress.isBlank()) {
            return AuthResult.Error("Delivery address is required")
        }
        
        if (paymentMethod.isBlank()) {
            return AuthResult.Error("Payment method is required")
        }
        
        val result = orderRepository.createOrder(
            customerId = customerId,
            customerName = customerName,
            vendorId = vendorId,
            vendorName = vendorName,
            items = items,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod
        )
        
        // Notify vendor of new order
        if (result is AuthResult.Success) {
            notificationRepository.notifyVendorNewOrder(
                vendorId = vendorId,
                orderId = result.data.orderId,
                orderNumber = result.data.orderNumber,
                customerName = customerName
            )
        }
        
        return result
    }
}
