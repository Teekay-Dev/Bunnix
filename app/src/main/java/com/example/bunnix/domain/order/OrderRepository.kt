package com.example.bunnix.domain.order

interface OrderRepository {

    suspend fun create(order: Order): Order

    suspend fun getById(orderId: String): Order?

    suspend fun update(order: Order): Order
}
