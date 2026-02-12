package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

/**
 * Vendor Profile Data Model
 * Created when user signs up as "Business"
 * Additional details filled in profile completion flow
 */
data class VendorProfile(
    @DocumentId
    val vendorId: String = "",
    val userId: String = "",
    val businessName: String = "",
    val description: String = "",
    val coverPhotoUrl: String = "",
    val category: String = "",
    val subCategories: List<String> = emptyList(),
    val bankName: String = "",
    val accountNumber: String = "",
    val accountName: String = "",
    val alternativePayment: String = "",
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalSales: Int = 0,
    val totalRevenue: Double = 0.0,
    val isAvailable: Boolean = true,
    val workingHours: Map<String, String> = emptyMap(),
    val location: GeoPoint? = null,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

/**
 * Working Hours Map Structure
 * Example:
 * {
 *   "monday": "9:00 AM - 5:00 PM",
 *   "tuesday": "9:00 AM - 5:00 PM",
 *   "wednesday": "9:00 AM - 5:00 PM",
 *   "thursday": "9:00 AM - 5:00 PM",
 *   "friday": "9:00 AM - 5:00 PM",
 *   "saturday": "10:00 AM - 2:00 PM",
 *   "sunday": "Closed"
 * }
 */
