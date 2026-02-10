package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Service(
    @DocumentId
    val serviceId: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val duration: Int = 0,
    val category: String = "",
    val imageUrl: String = "",
    val availability: List<String> = emptyList(),
    val totalBookings: Int = 0,
    val rating: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)