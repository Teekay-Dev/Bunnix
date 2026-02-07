package com.example.bunnix.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String? = null,
    val vendor_id: String,    // Who is selling
    val customer_id: String,  // Who is buying
    val product_id: Int,      // Link to your Product ID
    val quantity: Int,
    val total_price: Double,
    val status: String = "pending",
    val created_at: String? = null
)