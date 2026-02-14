package com.example.bunnix.domain.usecase.product


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Product
import com.example.bunnix.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for getting all products belonging to a vendor.
 * Returns sorted list of products.
 */
class GetVendorProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(vendorId: String): AuthResult<List<Product>> {
        if (vendorId.isBlank()) {
            return AuthResult.Error("Invalid vendor ID")
        }

        return productRepository.getVendorProducts(vendorId)
    }
}
