package com.example.bunnix.domain.usecase.booking

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Booking
import com.example.bunnix.domain.repository.BookingRepository
import com.example.bunnix.domain.repository.NotificationRepository
import com.google.firebase.Timestamp
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        customerId: String,
        customerName: String,
        vendorId: String,
        vendorName: String,
        serviceId: String,
        serviceName: String,
        servicePrice: Double,
        scheduledDate: Timestamp,
        scheduledTime: String,
        paymentMethod: String,
        customerNotes: String = ""
    ): AuthResult<Booking> {
        // Validation
        if (servicePrice <= 0) {
            return AuthResult.Error("Invalid service price")
        }
        
        if (scheduledTime.isBlank()) {
            return AuthResult.Error("Scheduled time is required")
        }
        
        if (paymentMethod.isBlank()) {
            return AuthResult.Error("Payment method is required")
        }
        
        val result = bookingRepository.createBooking(
            customerId = customerId,
            customerName = customerName,
            vendorId = vendorId,
            vendorName = vendorName,
            serviceId = serviceId,
            serviceName = serviceName,
            servicePrice = servicePrice,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            paymentMethod = paymentMethod,
            customerNotes = customerNotes
        )
        
        // Notify vendor of new booking
        if (result is AuthResult.Success) {
            notificationRepository.notifyVendorNewBooking(
                vendorId = vendorId,
                bookingId = result.data.bookingId,
                bookingNumber = result.data.bookingNumber,
                customerName = customerName
            )
        }
        
        return result
    }
}
