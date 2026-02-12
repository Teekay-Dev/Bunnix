package com.example.bunnix.domain.vendor

import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.model.domain.model.VendorProfile

class FakeVendorRepository : VendorRepository {
    private val vendors = mutableListOf<VendorProfile>()

    override suspend fun createVendor(profile: VendorProfile) {
        if (vendors.any { it.ownerUserId == profile.ownerUserId }) {
            throw IllegalStateException("User already has a vendor profile")
        }
        vendors.add(profile)
    }

    override suspend fun getVendorByUserId(userId: String): VendorProfile? {
        return vendors.find { it.ownerUserId == userId }
    }
}
