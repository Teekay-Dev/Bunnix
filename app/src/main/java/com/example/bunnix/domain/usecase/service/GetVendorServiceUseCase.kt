package com.example.bunnix.domain.usecase.service


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import com.example.bunnix.domain.repository.ServiceRepository
import javax.inject.Inject

/**
 * Use case for getting all services belonging to a vendor.
 * Returns sorted list of services.
 */
class GetVendorServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(vendorId: String): AuthResult<List<Service>> {
        if (vendorId.isBlank()) {
            return AuthResult.Error("Invalid vendor ID")
        }

        return serviceRepository.getVendorServices(vendorId)
    }
}
