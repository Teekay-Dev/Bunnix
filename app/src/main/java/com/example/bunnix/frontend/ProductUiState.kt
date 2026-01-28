package com.example.bunnix.frontend

data class ProductUiState(
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val quantity: Int = 1
)