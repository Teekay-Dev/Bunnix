package com.example.bunnix.domain.vendor

interface VendorProfileRepository {
    suspend fun exists(vendorId: String): Boolean
}
