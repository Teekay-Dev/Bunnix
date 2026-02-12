package com.example.bunnix.domain.booking

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager

class ConfirmBookingPaymentUseCase(
    private val authManager: AuthManager,
    private val userModeManager: UserModeManager,
    private val bookingRepository: BookingRepository,
    private val stateMachine: BookingStateMachine
) {

    suspend fun confirm(bookingId: String): Result<Booking> {

        if (userModeManager.getMode() != UserMode.VENDOR)
            return Result.failure(SecurityException("Only vendors can confirm payments"))

        val booking = bookingRepository.getById(bookingId)
            ?: return Result.failure(Exception("Booking not found"))

        if (booking.vendorId != authManager.currentUserUid())
            return Result.failure(SecurityException("Unauthorized"))

        if (!stateMachine.canTransition(
                booking.status,
                BookingStatus.PAYMENT_CONFIRMED,
                UserMode.VENDOR
            )
        ) {
            return Result.failure(IllegalStateException("Invalid payment confirmation"))
        }

        val updated = booking.copy(status = BookingStatus.PAYMENT_CONFIRMED)
        bookingRepository.update(updated)

        return Result.success(updated)
    }
}
