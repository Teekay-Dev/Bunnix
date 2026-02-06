package com.example.bunnix.model.domain.order

enum class OrderStatus {
    PLACED,
    PAYMENT_AWAITING_CONFIRMATION,
    PAYMENT_CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED
}
