package com.example.bunnix.domain.usecase.service


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.domain.repository.ServiceRepository
import javax.inject.Inject

/**
 * Use case for deleting a service.
 * Vendors can remove services from their catalog.
 */
class DeleteServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(serviceId: String): AuthResult<Unit> {
        if (serviceId.isBlank()) {
            return AuthResult.Error("Invalid service ID")
        }

        return serviceRepository.deleteService(serviceId)
    }
}
