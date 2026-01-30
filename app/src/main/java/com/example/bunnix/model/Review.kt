package com.example.bunnix.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Int? = null,
    val vendor_id: String,
    val customer_id: String,
    val rating: Int, // 1 to 5
    val comment: String?,
    val created_at: String? = null
)