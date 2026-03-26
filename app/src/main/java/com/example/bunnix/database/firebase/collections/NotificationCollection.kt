package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.models.Notification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object NotificationCollection {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsRef = db.collection("notifications")

    suspend fun sendNotification(
        userId: String,      // Customer ID
        vendorId: String,    // Vendor ID
        title: String,
        message: String,
        type: String,
        relatedId: String,
        relatedType: String
    ) {
        val timestamp = Timestamp.now()

        // 1. Notification for the VENDOR
        val vendorNotification = Notification(
            notificationId = db.collection("notifications").document().id,
            userId = vendorId, // For vendor, userId field acts as recipient ID in your ViewModel logic
            vendorId = vendorId,
            title = title,
            message = message,
            type = type,
            relatedId = relatedId,
            relatedType = relatedType,
            createdAt = timestamp
        )

        // 2. Notification for the CUSTOMER
        val customerNotification = Notification(
            notificationId = db.collection("notifications").document().id,
            userId = userId,
            vendorId = vendorId,
            title = title,
            message = "Your $relatedType has been updated.",
            type = type,
            relatedId = relatedId,
            relatedType = relatedType,
            createdAt = timestamp
        )

        // Send both in parallel
        val batch = db.batch()
        batch.set(notificationsRef.document(vendorNotification.notificationId), vendorNotification)
        batch.set(notificationsRef.document(customerNotification.notificationId), customerNotification)
        batch.commit().await()
    }
}