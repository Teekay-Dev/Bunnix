package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId
    val notificationId: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val message: String = "",
    val relatedId: String = "",
    val relatedType: String = "",
    val actionUrl: String = "",
    val imageUrl: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null,
    val expiresAt: Timestamp? = null
)