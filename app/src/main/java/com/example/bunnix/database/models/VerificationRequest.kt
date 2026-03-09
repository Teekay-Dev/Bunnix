package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Verification Request Data Model
 * Stores vendor verification submissions for admin review
 */
data class VerificationRequest(
    @DocumentId
    val requestId: String = "",
    val vendorId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val businessRegistrationNumber: String = "", // CAC number
    val taxIdentificationNumber: String = "", // TIN (optional)
    val businessAddress: String = "",

    // Document URLs (uploaded to Firebase Storage)
    val businessLicenseUrl: String = "", // CAC certificate
    val governmentIdUrl: String = "", // National ID, Driver's License, or Passport
    val proofOfAddressUrl: String = "", // Utility bill or bank statement

    // Review fields
    val status: String = "pending", // "pending", "approved", "rejected"
    val submittedAt: Timestamp? = null,
    val reviewedAt: Timestamp? = null,
    val reviewedBy: String? = null, // Admin user ID who reviewed
    val rejectionReason: String? = null, // If rejected, admin provides reason
    val adminNotes: String? = null, // Internal notes from admin

    val updatedAt: Timestamp? = null
)

/**
 * Verification Request Status Flow:
 * 1. Vendor submits → status = "pending"
 * 2. Admin reviews documents:
 *    - If approved → status = "approved", update vendorProfile.isVerified = true
 *    - If rejected → status = "rejected", provide rejectionReason
 * 3. Vendor can resubmit if rejected
 */