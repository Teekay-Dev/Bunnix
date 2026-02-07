package com.example.bunnix.model.domain.model

import kotlinx.datetime.Instant

data class VendorProfile(
    val id: String,
    val ownerUserId: String,
    val businessName: String,
    val description: String,
    val category: VendorCategory,
    val bankDetails: BankDetails,
    val location: GeoLocation,
    val isVerified: Boolean = false,
    val createdAt: Instant
)

data class BankDetails(
    val bankName: String,
    val accountNumber: String,
    val accountName: String
)

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

enum class VendorCategory {
    FOOD,
    BEAUTY,
    ELECTRONICS,
    SERVICES,
    OTHER
}

