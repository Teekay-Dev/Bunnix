package com.example.bunnix.data.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.VendorRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class VendorRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : VendorRepository {

    override suspend fun getVendorProfile(
        vendorId: String
    ): AuthResult<VendorProfile> {
        return try {

            val vendor = supabase
                .from("vendors")
                .select {
                    filter {
                        eq("id", vendorId)
                    }
                }
                .decodeSingle<VendorProfile>()

            AuthResult.Success(vendor)

        } catch (e: Exception) {

            AuthResult.Error(e.message ?: "Failed to fetch vendor")

        }
    }

    override suspend fun updateVendorProfile(
        vendorId: String,
        updates: Map<String, Any>
    ): AuthResult<Unit> {
        return try {
            supabase
                .from("vendors")
                .update(updates)

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error")
        }
    }

    override suspend fun getAllVendors(): AuthResult<List<VendorProfile>> {
        return try {
            val vendors = supabase
                .from("vendors")
                .select()
                .decodeList<VendorProfile>()

            AuthResult.Success(vendors)

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to fetch vendors")
        }
    }
}