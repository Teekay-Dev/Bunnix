package com.example.bunnix.domain.booking

abstract class FakeBookingRepository : BookingRepository {
    private val bookings = mutableListOf<Booking>()
    override suspend fun create(booking: Booking): Booking {
        bookings.add(booking)
        return booking
    }
}
