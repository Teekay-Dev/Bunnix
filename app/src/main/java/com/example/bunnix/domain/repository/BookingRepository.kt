package com.example.bunnix.domain.repository


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Booking
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Booking management.
 * Handles service bookings, payment verification, and status updates.
 */
interface BookingRepository {

    /**
     * Create a new booking (Customer books service)
     *
     * @param customerId Customer's user ID
     * @param customerName Customer's name
     * @param vendorId Vendor's user ID
     * @param vendorName Vendor's business name
     * @param serviceId Service ID being booked
     * @param serviceName Service name
     * @param servicePrice Service price
     * @param scheduledDate Date of service
     * @param scheduledTime Time of service
     * @param paymentMethod Payment method (Bank Transfer, Cash, etc.)
     * @param customerNotes Optional notes from customer
     * @return AuthResult with created Booking
     */
    suspend fun createBooking(
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
    ): AuthResult<Booking>

    /**
     * Upload payment receipt (Customer uploads transfer screenshot)
     *
     * @param bookingId Booking ID
     * @param receiptUri Local image URI
     * @return AuthResult with uploaded receipt URL
     */
    suspend fun uploadPaymentReceipt(
        bookingId: String,
        receiptUri: String
    ): AuthResult<String>

    /**
     * Verify payment (Vendor confirms payment received)
     *
     * @param bookingId Booking ID
     * @param vendorId Vendor ID (for authorization)
     * @return AuthResult<Unit>
     */
    suspend fun verifyPayment(
        bookingId: String,
        vendorId: String
    ): AuthResult<Unit>

    /**
     * Accept booking (Vendor accepts the booking request)
     *
     * @param bookingId Booking ID
     * @param vendorId Vendor ID (for authorization)
     * @return AuthResult with updated Booking
     */
    suspend fun acceptBooking(
        bookingId: String,
        vendorId: String
    ): AuthResult<Booking>

    /**
     * Decline booking (Vendor declines the booking request)
     *
     * @param bookingId Booking ID
     * @param vendorId Vendor ID (for authorization)
     * @param reason Optional reason for declining
     * @return AuthResult<Unit>
     */
    suspend fun declineBooking(
        bookingId: String,
        vendorId: String,
        reason: String = ""
    ): AuthResult<Unit>

    /**
     * Update booking status
     *
     * @param bookingId Booking ID
     * @param newStatus New status (e.g., "In Progress", "Completed")
     * @param vendorId Vendor ID (for authorization)
     * @return AuthResult with updated Booking
     */
    suspend fun updateBookingStatus(
        bookingId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Booking>

    /**
     * Get a single booking by ID
     *
     * @param bookingId Booking ID
     * @return AuthResult with Booking data
     */
    suspend fun getBooking(bookingId: String): AuthResult<Booking>

    /**
     * Get customer's bookings
     *
     * @param customerId Customer ID
     * @return AuthResult with list of Bookings
     */
    suspend fun getCustomerBookings(customerId: String): AuthResult<List<Booking>>

    /**
     * Get vendor's bookings
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Bookings
     */
    suspend fun getVendorBookings(vendorId: String): AuthResult<List<Booking>>

    /**
     * Get pending bookings for vendor (awaiting acceptance)
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Bookings
     */
    suspend fun getPendingBookings(vendorId: String): AuthResult<List<Booking>>

    /**
     * Observe customer's bookings in real-time
     *
     * @param customerId Customer ID
     * @return Flow of Booking list
     */
    fun observeCustomerBookings(customerId: String): Flow<List<Booking>>

    /**
     * Observe vendor's bookings in real-time
     *
     * @param vendorId Vendor ID
     * @return Flow of Booking list
     */
    fun observeVendorBookings(vendorId: String): Flow<List<Booking>>

    /**
     * Cancel booking (before vendor accepts)
     *
     * @param bookingId Booking ID
     * @param userId User ID (customer or vendor)
     * @return AuthResult<Unit>
     */
    suspend fun cancelBooking(
        bookingId: String,
        userId: String
    ): AuthResult<Unit>
}
