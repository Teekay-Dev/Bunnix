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
            val profile = supabase
                .from("vendors")
                .select()
                .decodeSingle<VendorProfile>()

            AuthResult.Success(profile)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error")
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
}