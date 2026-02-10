package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val productId: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val discountPrice: Double? = null,
    val category: String = "",
    val imageUrls: List<String> = emptyList(),
    val variants: List<Map<String, Any>> = emptyList(),
    val totalStock: Int = 0,
    val inStock: Boolean = true,
    val tags: List<String> = emptyList(),
    val views: Int = 0,
    val sold: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)