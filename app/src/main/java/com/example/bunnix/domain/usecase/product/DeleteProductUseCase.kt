package com.example.bunnix.domain.usecase.product


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for deleting a product.
 * Vendors can remove products from their catalog.
 */
class DeleteProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): AuthResult<Unit> {
        if (productId.isBlank()) {
            return AuthResult.Error("Invalid product ID")
        }

        return productRepository.deleteProduct(productId)
    }
}
