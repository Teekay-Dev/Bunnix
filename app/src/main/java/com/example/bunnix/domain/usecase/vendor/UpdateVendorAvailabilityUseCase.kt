package com.example.bunnix.domain.usecase.vendor

import com.example.bunnix.data.auth.getErrorMessage
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.domain.repository.VendorRepository
import javax.inject.Inject

class UpdateVendorAvailabilityUseCase @Inject constructor(
    private val vendorRepository: VendorRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        isAvailable: Boolean
    ): Result<Unit> {
        return try {
            val updates = mapOf<String, Any>("isAvailable" to isAvailable)
            val authResult = vendorRepository.updateVendorProfile(vendorId, updates)
            when {
                authResult.isSuccess() -> Result.success(Unit)
                else -> Result.failure(Exception(authResult.getErrorMessage() ?: "Failed to update availability"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}