package com.example.bunnix.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String? = null,
    val user_id: String,
    val title: String,
    val content: String,
    val type: String, // 'order', 'booking', etc.
    val is_read: Boolean = false,
    val created_at: String? = null
)