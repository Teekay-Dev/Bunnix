package com.example.bunnix.domain.usecase.booking

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Booking
import com.example.bunnix.domain.repository.BookingRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

class AcceptBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        bookingId: String,
        vendorId: String
    ): AuthResult<Booking> {
        val result = bookingRepository.acceptBooking(bookingId, vendorId)
        
        // Notify customer
        if (result is AuthResult.Success) {
            notificationRepository.notifyBookingResponse(
                customerId = result.data.customerId,
                bookingId = bookingId,
                bookingNumber = result.data.bookingNumber,
                isAccepted = true
            )
        }
        
        return result
    }
}


