package com.example.bunnix.database.models

data class CartItem(
    val id: String = "", // This will be the Product ID
    val productId: String = "",
    val name: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val variant: String? = null
)