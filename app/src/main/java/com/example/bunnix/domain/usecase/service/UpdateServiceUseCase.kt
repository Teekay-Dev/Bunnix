package com.example.bunnix.domain.usecase.service


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import com.example.bunnix.domain.repository.ServiceRepository
import javax.inject.Inject

/**
 * Use case for updating an existing service.
 * Validates changes before updating.
 */
class UpdateServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(
        serviceId: String,
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        duration: Int? = null,
        category: String? = null,
        imageUrl: String? = null,
        availability: List<String>? = null,
        isActive: Boolean? = null
    ): AuthResult<Service> {
        // Validation
        name?.let {
            if (it.isBlank() || it.length < 3) {
                return AuthResult.Error("Service name must be at least 3 characters")
            }
        }

        description?.let {
            if (it.isBlank() || it.length < 10) {
                return AuthResult.Error("Service description must be at least 10 characters")
            }
        }

        price?.let {
            if (it <= 0) {
                return AuthResult.Error("Price must be greater than 0")
            }
        }

        duration?.let {
            if (it <= 0) {
                return AuthResult.Error("Duration must be greater than 0 minutes")
            }
        }

        return serviceRepository.updateService(
            serviceId = serviceId,
            name = name?.trim(),
            description = description?.trim(),
            price = price,
            duration = duration,
            category = category,
            imageUrl = imageUrl,
            availability = availability,
            isActive = isActive
        )
    }
}
