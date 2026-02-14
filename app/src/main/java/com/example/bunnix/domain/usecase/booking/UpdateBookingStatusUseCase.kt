package com.example.bunnix.domain.usecase.booking

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Booking
import com.example.bunnix.domain.repository.BookingRepository
import com.example.bunnix.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * CORRECTED - Payment After Service Flow
 * Booking Requested → Vendor Accepted → In Progress → Completed → Awaiting Payment → Payment Confirmed
 */
class UpdateBookingStatusUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        bookingId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Booking> {
        // CORRECTED - Valid statuses for Payment After Service
        val validStatuses = listOf(
            "Booking Requested",
            "Vendor Accepted",
            "In Progress",
            "Completed",
            "Awaiting Payment",     // After service completion
            "Payment Confirmed",
            "Declined",
            "Cancelled"
        )

        if (newStatus !in validStatuses) {
            return AuthResult.Error("Invalid booking status")
        }

        val result = bookingRepository.updateBookingStatus(bookingId, newStatus, vendorId)

        // Notify customer of status change
        if (result is AuthResult.Success) {
            when (newStatus) {
                "Vendor Accepted" -> {
                    notificationRepository.notifyBookingResponse(
                        customerId = result.data.customerId,
                        bookingId = bookingId,
                        bookingNumber = result.data.bookingNumber,
                        isAccepted = true
                    )
                }
                "Declined" -> {
                    notificationRepository.notifyBookingResponse(
                        customerId = result.data.customerId,
                        bookingId = bookingId,
                        bookingNumber = result.data.bookingNumber,
                        isAccepted = false
                    )
                }
            }
        }

        return result
    }
}
