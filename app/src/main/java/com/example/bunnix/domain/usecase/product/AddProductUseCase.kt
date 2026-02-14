package com.example.bunnix.domain.usecase.product

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Product
import com.example.bunnix.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for adding a new product.
 * Handles validation before creating product.
 */
class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        vendorId: String,
        vendorName: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        imageUrls: List<String> = emptyList(),
        totalStock: Int = 0,
        discountPrice: Double? = null,
        variants: List<Map<String, Any>> = emptyList(),
        tags: List<String> = emptyList()
    ): AuthResult<Product> {
        // Validation
        if (name.isBlank() || name.length < 3) {
            return AuthResult.Error("Product name must be at least 3 characters")
        }

        if (description.isBlank() || description.length < 10) {
            return AuthResult.Error("Product description must be at least 10 characters")
        }

        if (price <= 0) {
            return AuthResult.Error("Price must be greater than 0")
        }

        if (category.isBlank()) {
            return AuthResult.Error("Please select a category")
        }

        if (totalStock < 0) {
            return AuthResult.Error("Stock cannot be negative")
        }

        discountPrice?.let {
            if (it >= price) {
                return AuthResult.Error("Discount price must be less than original price")
            }
            if (it <= 0) {
                return AuthResult.Error("Discount price must be greater than 0")
            }
        }

        return productRepository.addProduct(
            vendorId = vendorId,
            vendorName = vendorName,
            name = name.trim(),
            description = description.trim(),
            price = price,
            category = category,
            imageUrls = imageUrls,
            totalStock = totalStock,
            discountPrice = discountPrice,
            variants = variants,
            tags = tags.map { it.lowercase().trim() }
        )
    }
}
