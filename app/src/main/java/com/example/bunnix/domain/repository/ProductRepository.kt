package com.example.bunnix.domain.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Product management.
 * Handles CRUD operations for vendor products.
 */
interface ProductRepository {

    /**
     * Add a new product
     *
     * @param vendorId Vendor's user ID
     * @param vendorName Vendor's business name
     * @param name Product name
     * @param description Product description
     * @param price Product price
     * @param category Product category
     * @param imageUrls List of product image URLs
     * @param totalStock Initial stock quantity
     * @param discountPrice Optional discounted price
     * @param variants Optional product variants (size, color, etc.)
     * @param tags Optional search tags
     * @return AuthResult with created Product
     */
    suspend fun addProduct(
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
    ): AuthResult<Product>

    /**
     * Update an existing product
     *
     * @param productId Product ID to update
     * @param name Updated name (optional)
     * @param description Updated description (optional)
     * @param price Updated price (optional)
     * @param category Updated category (optional)
     * @param imageUrls Updated image URLs (optional)
     * @param totalStock Updated stock (optional)
     * @param discountPrice Updated discount price (optional)
     * @param variants Updated variants (optional)
     * @param tags Updated tags (optional)
     * @param inStock Stock availability (optional)
     * @return AuthResult with updated Product
     */
    suspend fun updateProduct(
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
    ): AuthResult<Product>

    /**
     * Delete a product
     *
     * @param productId Product ID to delete
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend fun deleteProduct(productId: String): AuthResult<Unit>

    /**
     * Get a single product by ID
     *
     * @param productId Product ID
     * @return AuthResult with Product data
     */
    suspend fun getProduct(productId: String): AuthResult<Product>

    /**
     * Get all products for a vendor
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Products
     */
    suspend fun getVendorProducts(vendorId: String): AuthResult<List<Product>>

    /**
     * Get products by category
     *
     * @param category Category name
     * @param limit Maximum number of products to return
     * @return AuthResult with list of Products
     */
    suspend fun getProductsByCategory(
        category: String,
        limit: Int = 20
    ): AuthResult<List<Product>>

    /**
     * Search products by name or tags
     *
     * @param query Search query
     * @param limit Maximum results
     * @return AuthResult with list of Products
     */
    suspend fun searchProducts(
        query: String,
        limit: Int = 20
    ): AuthResult<List<Product>>

    /**
     * Observe vendor's products in real-time
     *
     * @param vendorId Vendor ID
     * @return Flow of Product list
     */
    fun observeVendorProducts(vendorId: String): Flow<List<Product>>

    /**
     * Update product stock quantity
     * Used when orders are placed or cancelled
     *
     * @param productId Product ID
     * @param newStock New stock quantity
     * @return AuthResult<Unit>
     */
    suspend fun updateProductStock(
        productId: String,
        newStock: Int
    ): AuthResult<Unit>

    /**
     * Increment product views counter
     *
     * @param productId Product ID
     * @return AuthResult<Unit>
     */
    suspend fun incrementProductViews(productId: String): AuthResult<Unit>

    /**
     * Upload product images to Supabase Storage
     *
     * @param productId Product ID
     * @param imageUris List of local image URIs
     * @return AuthResult with list of uploaded image URLs
     */
    suspend fun uploadProductImages(
        productId: String,
        imageUris: List<String>
    ): AuthResult<List<String>>
}