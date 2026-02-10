package com.example.bunnix.model

// ✅ Mixed Search Result Model
data class SearchResultItem(
    val id: Int,
    val name: String,
    val category: String,
    val type: String // "Product" or "Service"
)
