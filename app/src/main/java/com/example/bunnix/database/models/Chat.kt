package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Chat(
    @DocumentId
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val participantDetails: Map<String, ParticipantInfo> = emptyMap(),
    val lastMessage: String = "",

    // ✅ CRITICAL FIX: Accept Any? (Long or Timestamp) to prevent crash
    // We use a different internal name to avoid conflicts if we wanted a custom setter,
    // but here we just expose the raw value and use a helper for the UI.
    @get:PropertyName("lastMessageTime")
    val lastMessageTimeRaw: Any? = null,

    val lastMessageSender: String = "",
    val unreadCount: Map<String, Int> = emptyMap(),
    val relatedOrderId: String = "",
    val relatedBookingId: String = "",
    val createdAt: Timestamp? = null
) {
    // ✅ Helper function: Safely convert Long (milliseconds) or Timestamp to Timestamp
    fun getLastMessageTime(): Timestamp? {
        return when (lastMessageTimeRaw) {
            is Long -> {
                // Convert milliseconds to Timestamp (seconds, nanoseconds)
                Timestamp(lastMessageTimeRaw / 1000, ((lastMessageTimeRaw % 1000) * 1000000).toInt())
            }
            is Timestamp -> lastMessageTimeRaw
            else -> null
        }
    }

    // Helper to get a Date object for sorting
    fun getLastMessageTimeDate(): java.util.Date? {
        return getLastMessageTime()?.toDate()
    }
}

data class ParticipantInfo(
    val name: String = "",
    val profilePic: String = "",
    val isVendor: Boolean = false
)

data class Message(
    @DocumentId
    val messageId: String = " ",
    val senderId: String = " ",
    val senderName: String = " ",
    val text: String = " ",
    val imageUrl: String? = " ",
    val messageType: String = "text",
    val orderPreview: Map<String, Any> = emptyMap(),
    val isRead: Boolean = false,
    val timestamp: Timestamp? = null,
    val content: String = "", // Added default
    val chatId: String = "",  // Added default
    val type: String = ""     // Added default
)