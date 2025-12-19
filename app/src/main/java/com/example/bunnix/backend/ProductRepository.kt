package com.example.bunnix.backend


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val dao: ProductDao
) {

    fun getProducts(): Flow<NetworkResult<List<Product>>> = flow {
        emit(NetworkResult.Loading())
        dao.getAllProducts().collect { products ->
            emit(NetworkResult.Success(products))
        }
    }

    suspend fun addProduct(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun update(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun delete(product: Product) {
        dao.deleteProduct(product)
    }

    fun getProductsByVendor(vendorId: Int): Flow<List<Product>> {
        return dao.getProductsByVendor(vendorId)
    }
}
