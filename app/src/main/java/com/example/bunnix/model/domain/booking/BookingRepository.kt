package com.example.bunnix.model.domain.booking

interface BookingRepository {

    suspend fun create(booking: Booking): Booking

    suspend fun getById(id: String): Booking?

    suspend fun update(booking: Booking): Booking
}
