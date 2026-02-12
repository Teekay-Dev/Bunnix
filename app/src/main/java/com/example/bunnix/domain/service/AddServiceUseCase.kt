package com.example.bunnix.domain.service

import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager
import com.example.bunnix.domain.vendor.VendorProfileRepository
import com.example.bunnix.model.data.auth.AuthManager
import com.example.bunnix.model.domain.model.Service
import java.util.UUID

class AddServiceUseCase(
    private val auth: AuthManager,
    private val mode: UserModeManager,
    private val vendorRepo: VendorProfileRepository,
    private val serviceRepo: ServiceRepository,
    private val eventBus: EventBus
) {
    suspend fun execute(title: String, price: Double, durationMinutes: Int): Result<Service> {
        val uid = auth.currentUserUid()

        if (mode.getMode() != UserMode.VENDOR)
            return Result.failure(SecurityException("Vendor mode required"))
        if (!vendorRepo.exists(uid))
            return Result.failure(SecurityException("Vendor profile missing"))

        val service = Service(
            id = UUID.randomUUID().toString(),
            vendorId = uid,
            title = title,
            basePrice = price,
            durationMinutes = durationMinutes
        )

        val saved = serviceRepo.create(service)
        eventBus.emit(DomainEvent.ServiceAdded(saved.id, uid))
        return Result.success(saved)
    }
}
