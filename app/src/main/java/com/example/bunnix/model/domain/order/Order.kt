package com.example.bunnix.model.domain.order

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Order(
    val id: String,
    val customerId: String,
    val vendorId: String,
    val items: List<String>, // product IDs
    val status: OrderStatus = OrderStatus.PLACED,
    val createdAt: Instant = Clock.System.now()
)



