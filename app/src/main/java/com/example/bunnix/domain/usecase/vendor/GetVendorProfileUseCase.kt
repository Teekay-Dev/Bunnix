package com.example.bunnix.domain.usecase.vendor

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.data.auth.getOrNull
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.VendorRepository
import javax.inject.Inject

class GetVendorProfileUseCase @Inject constructor(
    private val vendorRepository: VendorRepository
) {
    suspend operator fun invoke(vendorId: String): Result<VendorProfile> {
        return try {
            val authResult = vendorRepository.getVendorProfile(vendorId)
            when {
                authResult.isSuccess() -> Result.success(authResult.getOrNull()!!)
                else -> Result.failure(Exception("Failed to get vendor profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}