package com.example.bunnix.domain.user


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile
import javax.inject.Inject

/**
 * Use case for updating vendor profile.
 * Handles validation and business logic for vendor profile updates.
 */
class UpdateVendorProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        businessName: String? = null,
        description: String? = null,
        category: String? = null,
        subCategories: List<String>? = null,
        address: String? = null,
        phone: String? = null,
        email: String? = null,
        coverPhotoUrl: String? = null,
        bankName: String? = null,
        accountNumber: String? = null,
        accountName: String? = null,
        alternativePayment: String? = null,
        workingHours: Map<String, String>? = null,
        isAvailable: Boolean? = null
    ): AuthResult<VendorProfile> {
        // Validate business name
        businessName?.let {
            if (it.isBlank() || it.length < 3) {
                return AuthResult.Error("Business name must be at least 3 characters")
            }
        }

        // Validate account number if provided
        accountNumber?.let {
            if (it.length !in 10..10) {
                return AuthResult.Error("Account number must be 10 digits")
            }
        }

        return profileRepository.updateVendorProfile(
            vendorId = vendorId,
            businessName = businessName?.trim(),
            description = description?.trim(),
            category = category,
            subCategories = subCategories,
            address = address?.trim(),
            phone = phone?.trim(),
            email = email?.trim(),
            coverPhotoUrl = coverPhotoUrl,
            bankName = bankName?.trim(),
            accountNumber = accountNumber?.trim(),
            accountName = accountName?.trim(),
            alternativePayment = alternativePayment?.trim(),
            workingHours = workingHours,
            isAvailable = isAvailable
        )
    }
}
