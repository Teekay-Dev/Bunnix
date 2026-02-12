package com.example.bunnix.domain.service

import com.example.bunnix.model.domain.model.Service

interface ServiceRepository {
    suspend fun create(service: Service): Service
}
class FakeServiceRepository : ServiceRepository {
    private val services = mutableListOf<Service>()
    override suspend fun create(service: Service): Service {
        services.add(service)
        return service
    }
}
