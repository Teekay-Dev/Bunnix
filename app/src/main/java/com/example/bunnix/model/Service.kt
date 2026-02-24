package com.example.bunnix.model

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Int? = null,
    val vendorid: String, // Link to the Vendor's Auth ID
    val name: String,      // e.g., "Hair Styling"
    val price: Double,    // e.g., 45.0
    val duration: String? = null, // e.g., "1 hour"
    val description: String? = null
)