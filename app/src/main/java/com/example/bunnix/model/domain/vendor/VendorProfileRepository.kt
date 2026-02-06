package com.example.bunnix.model.domain.vendor

interface VendorProfileRepository {
    suspend fun exists(vendorId: String): Boolean
}
