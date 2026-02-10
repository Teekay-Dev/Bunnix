package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Booking(
    @DocumentId
    val bookingId: String = "",
    val bookingNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val servicePrice: Double = 0.0,
    val scheduledDate: Timestamp? = null,
    val scheduledTime: String = "",
    val status: String = "Booking Requested",
    val paymentMethod: String = "",
    val paymentReceiptUrl: String = "",
    val paymentVerified: Boolean = false,
    val customerNotes: String = "",
    val vendorNotes: String = "",
    val statusHistory: List<Map<String, Any>> = emptyList(),
    val createdAt: Timestamp? = null,
    val completedAt: Timestamp? = null
)