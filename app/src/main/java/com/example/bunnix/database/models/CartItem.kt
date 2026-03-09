package com.example.bunnix.database.models

data class CartItem(
    val id: String,
    val name: String,
    val vendorName: String,
    val price: Double,
    val originalPrice: Double?,
    val quantity: Int,
    val imageUrl: String?,
    val variant: String?
)