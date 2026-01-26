package com.example.bunnix.backend

import com.example.bunnix.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val dao: ProductDao
) {

    // All products
    fun getProducts(): Flow<NetworkResult<List<Product>>> {
        return dao.getAllProducts()
            .map<List<Product>, NetworkResult<List<Product>>> {
                NetworkResult.Success(it)
            }
            .onStart {
                emit(NetworkResult.Loading())
            }
    }

    // 🔍 Search products (name / category)
    fun searchProducts(query: String): Flow<NetworkResult<List<Product>>> {
        return dao.searchProducts(query)
            .map<List<Product>, NetworkResult<List<Product>>> {
                NetworkResult.Success(it)
            }
            .onStart {
                emit(NetworkResult.Loading())
            }
    }

    // Vendor products
    fun getProductsByVendor(vendor_id: Int): Flow<List<Product>> {
        return dao.getProductsByVendor(vendor_id)
    }

    // Vendor actions
    suspend fun addProduct(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun update(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun delete(product: Product) {
        dao.deleteProduct(product)
    }
}
