package com.example.bunnix.model
import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: String? = null,
    val vendor_id: String,
    val customer_id: String,
    val service_name: String,
    val booking_date: String,
    val status: String = "pending",
    val price: Double = 0.0
)