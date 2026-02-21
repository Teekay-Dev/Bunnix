package com.example.bunnix.domain.usecase.order

import com.example.bunnix.data.auth.getOrNull
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.database.models.Order
import com.example.bunnix.domain.repository.OrderRepository
import javax.inject.Inject

class GetVendorOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        status: String? = null
    ): Result<List<Order>> {
        return try {
            val authResult = orderRepository.getVendorOrders(vendorId)
            when {
                authResult.isSuccess() -> {
                    val orders = authResult.getOrNull() ?: emptyList()
                    val filtered = if (status != null) {
                        orders.filter { it.status.equals(status, ignoreCase = true) }
                    } else orders
                    Result.success(filtered)
                }
                else -> Result.failure(Exception("Failed to get orders"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}