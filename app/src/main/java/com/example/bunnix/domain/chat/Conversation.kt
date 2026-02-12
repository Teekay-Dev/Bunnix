package com.example.bunnix.domain.chat

import java.time.Instant

data class Conversation(
    val id: String,
    val type: ConversationType,
    val referenceId: String, // orderId or bookingId
    val customerId: String,
    val vendorId: String,
    val createdAt: Instant
)
