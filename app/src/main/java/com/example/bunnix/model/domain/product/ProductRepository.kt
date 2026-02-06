package com.example.bunnix.model.domain.product

import com.example.bunnix.model.domain.model.Product
import com.example.bunnix.model.domain.model.Service

interface ProductRepository {
    suspend fun create(product: Product): Product
}

interface ServiceRepository {
    suspend fun create(service: Service): Service
}

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    override suspend fun create(product: Product): Product {
        products.add(product)
        return product
    }
}

class FakeServiceRepository : ServiceRepository {
    private val services = mutableListOf<Service>()
    override suspend fun create(service: Service): Service {
        services.add(service)
        return service
    }
}

