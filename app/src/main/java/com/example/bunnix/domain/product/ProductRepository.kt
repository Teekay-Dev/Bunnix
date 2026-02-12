package com.example.bunnix.domain.product

import com.example.bunnix.model.domain.model.Product

interface ProductRepository {
    suspend fun create(product: Product): Product
}



class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    override suspend fun create(product: Product): Product {
        products.add(product)
        return product
    }
}



