package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val reviewId: String = "",
    val vendorId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val orderId: String = "",
    val bookingId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val vendorResponse: String = "",
    val vendorResponseAt: Timestamp? = null,
    val isVerifiedPurchase: Boolean = false,
    val createdAt: Timestamp? = null
)