package com.example.bunnix.domain.product

import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager
import com.example.bunnix.domain.vendor.VendorProfileRepository
import com.example.bunnix.model.data.auth.AuthManager
import com.example.bunnix.model.domain.model.Product
import java.util.UUID

class AddProductUseCase(
    private val auth: AuthManager,
    private val mode: UserModeManager,
    private val vendorRepo: VendorProfileRepository,
    private val productRepo: ProductRepository,
    private val eventBus: EventBus
) {
    suspend fun execute(name: String, price: Double): Result<Product> {
        val uid = auth.currentUserUid()

        if (mode.getMode() != UserMode.VENDOR)
            return Result.failure(SecurityException("Vendor mode required"))
        if (!vendorRepo.exists(uid))
            return Result.failure(SecurityException("Vendor profile missing"))

        val product = Product(
            id = UUID.randomUUID().toString(),
            vendorId = uid,
            name = name,
            price = price
        )

        val saved = productRepo.create(product)
        eventBus.emit(DomainEvent.ProductAdded(saved.id, uid))
        return Result.success(saved)
    }
}
