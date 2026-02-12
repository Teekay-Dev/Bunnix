package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val participantDetails: Map<String, ParticipantInfo> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTime: Timestamp? = null,
    val lastMessageSender: String = "",
    val unreadCount: Map<String, Int> = emptyMap(),
    val relatedOrderId: String = "",
    val relatedBookingId: String = "",
    val createdAt: Timestamp? = null
)

data class ParticipantInfo(
    val name: String = "",
    val profilePic: String = "",
    val isVendor: Boolean = false
)

data class Message(
    @DocumentId
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val messageType: String = "text", // "text" | "image" | "order_link"
    val orderPreview: Map<String, Any> = emptyMap(),
    val isRead: Boolean = false,
    val timestamp: Timestamp? = null
)