package com.example.bunnix.model.domain.chat

import java.time.Instant

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val message: String,
    val sentAt: Instant
)
