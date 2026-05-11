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
    val profilePhotoUrl: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val accountName: String = "",
    val alternativePayment: String = "",
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalSales: Int = 0,
    val totalRevenue: Double = 0.0,
    val availableBalance: Double = 0.0, // Balance vendor can withdraw
    val isAvailable: Boolean = true,
    val workingHours: Map<String, String> = emptyMap(),
    val location: GeoPoint? = null,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val status: String = "pending", // Account status: "pending", "active", "suspended"

    // ✅ VERIFICATION FIELDS (NEW)
    val isVerified: Boolean = false, // Blue checkmark badge
    val verificationStatus: String = "none", // "none", "pending", "approved", "rejected"
    val verificationRequestedAt: Timestamp? = null, // When vendor requested verification
    val verificationApprovedAt: Timestamp? = null, // When admin approved
    val verificationRejectedAt: Timestamp? = null, // When admin rejected
    val rejectionReason: String? = null, // Reason if rejected

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

/**
 * Verification Status Flow:
 * 1. "none" → Vendor hasn't requested verification
 * 2. "pending" → Verification request submitted, waiting for admin review
 * 3. "approved" → Admin approved, isVerified = true, blue badge shows
 * 4. "rejected" → Admin rejected, vendor can see rejection reason and reapply
 */