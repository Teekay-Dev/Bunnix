package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Order(
    @DocumentId
    val orderId: String = "",
    val orderNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    val items: List<Map<String, Any>> = emptyList(),
    val totalAmount: Double = 0.0,
    val deliveryAddress: String = "",
    val status: String = "Awaiting Payment",
    val paymentMethod: String = "",
    val paymentReceiptUrl: String = "",
    val paymentVerified: Boolean = false,
    val paymentVerifiedAt: Timestamp? = null,
    val statusHistory: List<Map<String, Any>> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)