package com.example.bunnix.domain.usecase.service


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import com.example.bunnix.domain.repository.ServiceRepository
import javax.inject.Inject

/**
 * Use case for adding a new service.
 * Handles validation before creating service.
 */
class AddServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        vendorName: String,
        name: String,
        description: String,
        price: Double,
        duration: Int,
        category: String,
        imageUrl: String = "",
        availability: List<String> = emptyList()
    ): AuthResult<Service> {
        // Validation
        if (name.isBlank() || name.length < 3) {
            return AuthResult.Error("Service name must be at least 3 characters")
        }

        if (description.isBlank() || description.length < 10) {
            return AuthResult.Error("Service description must be at least 10 characters")
        }

        if (price <= 0) {
            return AuthResult.Error("Price must be greater than 0")
        }

        if (duration <= 0) {
            return AuthResult.Error("Duration must be greater than 0 minutes")
        }

        if (category.isBlank()) {
            return AuthResult.Error("Please select a category")
        }

        return serviceRepository.addService(
            vendorId = vendorId,
            vendorName = vendorName,
            name = name.trim(),
            description = description.trim(),
            price = price,
            duration = duration,
            category = category,
            imageUrl = imageUrl,
            availability = availability
        )
    }
}
