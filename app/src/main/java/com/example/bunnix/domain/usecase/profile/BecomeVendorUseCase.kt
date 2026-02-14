package com.example.bunnix.domain.usecase.profile

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case for converting a Customer into a Vendor.
 * This is the "Become a Vendor" flow.
 */
class BecomeVendorUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    /**
     * Convert customer to vendor
     *
     * @param userId User ID
     * @param businessName Business name
     * @param businessAddress Business address
     * @param category Business category
     * @return AuthResult with created VendorProfile
     */
    suspend operator fun invoke(
        userId: String,
        businessName: String,
        businessAddress: String,
        category: String
    ): AuthResult<VendorProfile> {
        // Validate inputs
        if (businessName.isBlank() || businessName.length < 3) {
            return AuthResult.Error("Business name must be at least 3 characters")
        }

        if (businessAddress.isBlank()) {
            return AuthResult.Error("Business address is required")
        }

        if (category.isBlank()) {
            return AuthResult.Error("Please select a business category")
        }

        return profileRepository.createVendorProfile(
            userId = userId,
            businessName = businessName.trim(),
            businessAddress = businessAddress.trim(),
            category = category
        )
    }
}