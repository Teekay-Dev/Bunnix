package com.example.bunnix.domain.usecase.vendor

import com.example.bunnix.data.auth.getErrorMessage
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.domain.repository.VendorRepository
import javax.inject.Inject

class UpdateVendorProfileUseCase @Inject constructor(
    private val vendorRepository: VendorRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            @Suppress("UNCHECKED_CAST")
            val authResult = vendorRepository.updateVendorProfile(
                vendorId,
                updates as Map<String, Boolean>
            )
            when {
                authResult.isSuccess() -> Result.success(Unit)
                else -> Result.failure(Exception(authResult.getErrorMessage() ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}