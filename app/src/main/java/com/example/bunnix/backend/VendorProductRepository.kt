package com.example.bunnix.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VendorProductRepository @Inject constructor(
    private val  productRepository: ProductRepository
) {
    fun getVendorProducts(vendorId: Int): Flow<List<Product>> =
        productRepository.getProducts()
            .map { result ->
                if (result is NetworkResult.Success) {
                    result.data?.filter { it.vendor_id == vendorId } ?: emptyList()
                } else emptyList()
            }

    suspend fun addProduct(product: Product) {
        productRepository.addProduct(product)

    }

    suspend fun updateProduct(product: Product) {
        productRepository.update(product)
    }

    suspend fun deleteProduct(product: Product) {
        productRepository.delete(product)
    }
}