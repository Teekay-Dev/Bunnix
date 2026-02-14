package com.example.bunnix.domain.usecase.product


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Product
import com.example.bunnix.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for updating an existing product.
 * Validates changes before updating.
 */
class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        productId: String,
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        category: String? = null,
        imageUrls: List<String>? = null,
        totalStock: Int? = null,
        discountPrice: Double? = null,
        variants: List<Map<String, Any>>? = null,
        tags: List<String>? = null,
        inStock: Boolean? = null
    ): AuthResult<Product> {
        // Validation
        name?.let {
            if (it.isBlank() || it.length < 3) {
                return AuthResult.Error("Product name must be at least 3 characters")
            }
        }

        description?.let {
            if (it.isBlank() || it.length < 10) {
                return AuthResult.Error("Product description must be at least 10 characters")
            }
        }

        price?.let {
            if (it <= 0) {
                return AuthResult.Error("Price must be greater than 0")
            }
        }

        totalStock?.let {
            if (it < 0) {
                return AuthResult.Error("Stock cannot be negative")
            }
        }

        discountPrice?.let {
            val productPrice = price ?: return AuthResult.Error("Cannot set discount without price")
            if (it >= productPrice) {
                return AuthResult.Error("Discount price must be less than original price")
            }
            if (it <= 0) {
                return AuthResult.Error("Discount price must be greater than 0")
            }
        }

        return productRepository.updateProduct(
            productId = productId,
            name = name?.trim(),
            description = description?.trim(),
            price = price,
            category = category,
            imageUrls = imageUrls,
            totalStock = totalStock,
            discountPrice = discountPrice,
            variants = variants,
            tags = tags?.map { it.lowercase().trim() },
            inStock = inStock
        )
    }
}
