package com.example.bunnix.domain.usecase.order

import com.example.bunnix.database.models.Order
import com.example.bunnix.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String): Result<Order> {
        return try {
            orderRepository.getOrderById(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

