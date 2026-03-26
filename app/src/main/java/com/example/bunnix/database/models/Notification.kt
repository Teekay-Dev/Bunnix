package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId
    val notificationId: String = "",
    val userId: String = "",       // Customer ID
    val vendorId: String = "",     // Vendor ID
    val title: String = "",
    val message: String = "",
    val type: String = "",         // "ORDER", "BOOKING", "PAYMENT"
    val relatedId: String = "",    // OrderId or BookingId
    val relatedType: String = "",   // "order", "booking"
    val imageUrl: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
)