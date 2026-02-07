package com.example.bunnix.model.domain.vendor

import com.example.bunnix.model.domain.model.VendorProfile

interface VendorRepository {
    suspend fun createVendor(profile: VendorProfile)
    suspend fun getVendorByUserId(userId: String): VendorProfile?
}
