package com.example.bunnix.model.domain.events

sealed class DomainEvent {
    data class OrderPlaced(val orderId: String, val vendorId: String) : DomainEvent()
    data class BookingRequested(val bookingId: String, val vendorId: String) : DomainEvent()
    data class ProductAdded(val productId: String, val vendorId: String) : DomainEvent()
    data class ServiceAdded(val serviceId: String, val vendorId: String) : DomainEvent()
    data class PaymentProofUploaded(val orderId: String, val customerId: String) : DomainEvent()
    data class PaymentConfirmed(val orderId: String, val vendorId: String) : DomainEvent()
}

