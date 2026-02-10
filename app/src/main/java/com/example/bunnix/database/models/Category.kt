package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val categoryId: String = "",
    val name: String = "",
    val icon: String = "",
    val imageUrl: String = "",
    val type: String = "",
    val subCategories: List<String> = emptyList(),
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null
)