package com.example.bunnix.model.domain.vendor

class FakeVendorProfileRepository : VendorProfileRepository {
    private val vendors = mutableSetOf<String>()
    fun add(vendorId: String) { vendors.add(vendorId) }
    override suspend fun exists(vendorId: String) = vendors.contains(vendorId)
}
