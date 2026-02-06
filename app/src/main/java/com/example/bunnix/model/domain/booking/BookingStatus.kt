package com.example.bunnix.model.domain.booking

enum class BookingStatus {
    REQUESTED,
    PAYMENT_AWAITING_CONFIRMATION,
    PAYMENT_CONFIRMED,
    VENDOR_ACCEPTED,
    IN_PROGRESS,
    COMPLETED
}
