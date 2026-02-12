package com.example.bunnix.domain.events

interface EventBus {
    fun emit(event: DomainEvent)
}

class FakeEventBus : EventBus {
    override fun emit(event: DomainEvent) {
        println("EVENT EMITTED -> $event")
        // Later: push notification, FCM, worker job
    }
}

