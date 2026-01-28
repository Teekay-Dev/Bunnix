package com.example.bunnix.model


data class Order(
    val id: String,
    val customerName: String,
    val items: List<String>,
    val price: String,
    val vendor_id: String,
    val status: String // "pending", "processing", "shipped", "delivered"
)

data class Booking(
    val id: String,
    val customerName: String,
    val serviceType: String,
    val dateTime: String,
    val price: String,
    val vendor_id: String,
    val status: String // "confirmed", "pending"
)

enum class OrderStatus {
    ORDERED, PACKED, SHIPPED, DELIVERED
}