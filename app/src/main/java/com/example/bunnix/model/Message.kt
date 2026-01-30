package com.example.bunnix.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String? = null,
    val sender_id: String,
    val receiver_id: String,
    val content: String,
    val created_at: String? = null
)

data class ChatSummary(
    val customerId: String,
    val customerName: String,
    val lastMessage: String,
    val timestamp: String
)