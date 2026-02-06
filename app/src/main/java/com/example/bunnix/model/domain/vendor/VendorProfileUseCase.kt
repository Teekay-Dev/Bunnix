package com.example.bunnix.model.domain.vendor

import com.example.bunnix.model.domain.model.BankDetails
import com.example.bunnix.model.domain.model.GeoLocation
import com.example.bunnix.model.domain.model.VendorCategory
import com.example.bunnix.model.domain.model.VendorProfile
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

class CreateVendorProfileUseCase(
    private val vendorRepository: VendorRepository
) {
    suspend operator fun invoke(
        userId: String,
        businessName: String,
        description: String,
        category: VendorCategory,
        bankDetails: BankDetails,
        location: GeoLocation
    ): VendorProfile {

        require(businessName.isNotBlank())
        require(bankDetails.accountNumber.length >= 10)

        val vendor = VendorProfile(
            id = UUID.randomUUID().toString(),
            ownerUserId = userId,
            businessName = businessName,
            description = description,
            category = category,
            bankDetails = bankDetails,
            location = location,
            createdAt = Clock.System.now()
        )

        vendorRepository.createVendor(vendor)
        return vendor
    }
}
