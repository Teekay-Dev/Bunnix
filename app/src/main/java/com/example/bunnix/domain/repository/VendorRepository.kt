package com.example.bunnix.domain.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile

interface VendorRepository {
    suspend fun getVendorProfile(vendorId: String): AuthResult<VendorProfile>
    suspend fun updateVendorProfile(vendorId: String, updates: Map<String, Any>): AuthResult<Unit>
}