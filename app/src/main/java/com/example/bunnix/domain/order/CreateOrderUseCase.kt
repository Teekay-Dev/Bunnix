package com.example.bunnix.domain.order

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.model.data.auth.AuthManager
import java.util.UUID

class CreateOrderUseCase(
    private val auth: AuthManager,
    private val orderRepo: OrderRepository,
    private val eventBus: EventBus
) {
    suspend fun execute(vendorId: String, items: List<String>): Result<Order> {
        val uid = auth.currentUserUid()
        val order = Order(
            id = UUID.randomUUID().toString(),
            customerId = uid,
            vendorId = vendorId,
            items = items
        )
        val saved = orderRepo.create(order)
        eventBus.emit(DomainEvent.OrderPlaced(saved.id, vendorId))
        return Result.success(saved)
    }
}
