package com.example.bunnix.model.domain.service

import com.example.bunnix.model.domain.model.Service

interface ServiceRepository {
    suspend fun create(service: Service): Service
}
class FakeServiceRepository : ServiceRepository {
    private val items = mutableMapOf<String, Service>()
    override suspend fun create(service: Service) = service.also { items[it.id] = it }
}
