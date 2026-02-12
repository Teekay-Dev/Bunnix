package com.example.bunnix.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: String,
    val vendor_id: Int,
    val quantity: Int,
    val location: String,
    val image_url: Int
)
