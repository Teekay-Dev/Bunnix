package com.example.bunnix.backend

import com.example.bunnix.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerDashboardRepository @Inject constructor(
    private val productRepository: ProductRepository
) {

    fun getDashboardProducts(): Flow<List<Product>> =
        productRepository.getProducts()
            .map { result ->
                if (result is NetworkResult.Success) {
                    result.data ?: emptyList()
                } else emptyList()
            }
}
