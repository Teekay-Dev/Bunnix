package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePicUrl: String = "",
    val isVendor: Boolean = false,
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "Nigeria",
    val createdAt: Timestamp? = null,
    val lastActive: Timestamp? = null
)