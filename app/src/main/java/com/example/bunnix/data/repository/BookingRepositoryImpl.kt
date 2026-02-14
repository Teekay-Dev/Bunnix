package com.example.bunnix.data.repository

import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Booking
import com.example.bunnix.domain.repository.BookingRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CORRECTED - Payment After Service Flow
 * Booking Requested → Vendor Accepted → In Progress → Completed → Awaiting Payment → Payment Confirmed
 */
@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : BookingRepository {

    companion object {
        private const val BOOKINGS_COLLECTION = "bookings"
        private const val PAYMENT_RECEIPTS_BUCKET = "payment-receipts"
    }

    override suspend fun createBooking(
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
        customerNotes: String
    ): AuthResult<Booking> {
        return try {
            val bookingRef = firestore.collection(BOOKINGS_COLLECTION).document()
            val bookingId = bookingRef.id

            // Booking number format: BNX-YYYYMMDD-XXXXXX
            val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val random = (100000..999999).random()
            val bookingNumber = "BNX-$currentDate-$random"

            val booking = Booking(
                bookingId = bookingId,
                bookingNumber = bookingNumber,
                customerId = customerId,
                customerName = customerName,
                vendorId = vendorId,
                vendorName = vendorName,
                serviceId = serviceId,
                serviceName = serviceName,
                servicePrice = servicePrice,
                scheduledDate = scheduledDate,
                scheduledTime = scheduledTime,
                status = "Booking Requested",  // Initial status
                paymentMethod = paymentMethod,
                paymentReceiptUrl = "",
                paymentVerified = false,
                customerNotes = customerNotes,
                vendorNotes = "",
                statusHistory = listOf(
                    mapOf(
                        "status" to "Booking Requested",
                        "timestamp" to Timestamp.now(),
                        "note" to "Booking created - Payment after service"
                    )
                ),
                createdAt = Timestamp.now(),
                completedAt = null
            )

            bookingRef.set(booking).await()

            AuthResult.Success(booking)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to create booking",
                exception = e
            )
        }
    }

    override suspend fun uploadPaymentReceipt(
        bookingId: String,
        receiptUri: String
    ): AuthResult<String> {
        return try {
            val file = File(Uri.parse(receiptUri).path ?: throw Exception("Invalid URI"))
            val fileName = "${bookingId}_receipt_${System.currentTimeMillis()}.jpg"

            val bucket = supabase.storage.from(PAYMENT_RECEIPTS_BUCKET)
            bucket.upload(fileName, file.readBytes())

            val publicUrl = bucket.publicUrl(fileName)

            // Get current booking
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            // Verify booking has been completed before accepting payment
            if (booking.status != "Completed") {
                throw Exception("Cannot upload payment receipt. Service must be completed first.")
            }

            val newStatusHistory = booking.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to "Awaiting Payment",
                    "timestamp" to Timestamp.now(),
                    "note" to "Customer uploaded payment receipt"
                )
            )

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(
                    mapOf(
                        "paymentReceiptUrl" to publicUrl,
                        "status" to "Awaiting Payment",  // CORRECTED: Payment after service
                        "statusHistory" to newStatusHistory
                    )
                )
                .await()

            AuthResult.Success(publicUrl)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload receipt",
                exception = e
            )
        }
    }

    override suspend fun verifyPayment(
        bookingId: String,
        vendorId: String
    ): AuthResult<Unit> {
        return try {
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            if (booking.vendorId != vendorId) {
                throw Exception("Unauthorized")
            }

            val newStatusHistory = booking.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to "Payment Confirmed",
                    "timestamp" to Timestamp.now(),
                    "note" to "Payment verified by vendor"
                )
            )

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(
                    mapOf(
                        "paymentVerified" to true,
                        "status" to "Payment Confirmed",
                        "statusHistory" to newStatusHistory
                    )
                )
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to verify payment",
                exception = e
            )
        }
    }

    override suspend fun acceptBooking(
        bookingId: String,
        vendorId: String
    ): AuthResult<Booking> {
        return try {
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            if (booking.vendorId != vendorId) {
                throw Exception("Unauthorized")
            }

            val newStatusHistory = booking.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to "Vendor Accepted",
                    "timestamp" to Timestamp.now(),
                    "note" to "Booking accepted by vendor"
                )
            )

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to "Vendor Accepted",
                        "statusHistory" to newStatusHistory
                    )
                )
                .await()

            getBooking(bookingId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to accept booking",
                exception = e
            )
        }
    }

    override suspend fun declineBooking(
        bookingId: String,
        vendorId: String,
        reason: String
    ): AuthResult<Unit> {
        return try {
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            if (booking.vendorId != vendorId) {
                throw Exception("Unauthorized")
            }

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(
                    mapOf(
                        "status" to "Declined",
                        "vendorNotes" to reason
                    )
                )
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to decline booking",
                exception = e
            )
        }
    }

    override suspend fun updateBookingStatus(
        bookingId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Booking> {
        return try {
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            if (booking.vendorId != vendorId) {
                throw Exception("Unauthorized")
            }

            val newStatusHistory = booking.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to newStatus,
                    "timestamp" to Timestamp.now(),
                    "note" to "Status updated"
                )
            )

            val updates = mutableMapOf<String, Any>(
                "status" to newStatus,
                "statusHistory" to newStatusHistory
            )

            if (newStatus == "Completed") {
                updates["completedAt"] = Timestamp.now()
            }

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(updates)
                .await()

            getBooking(bookingId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update status",
                exception = e
            )
        }
    }

    override suspend fun getBooking(bookingId: String): AuthResult<Booking> {
        return try {
            val snapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = snapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            AuthResult.Success(booking)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get booking",
                exception = e
            )
        }
    }

    override suspend fun getCustomerBookings(customerId: String): AuthResult<List<Booking>> {
        return try {
            val snapshot = firestore.collection(BOOKINGS_COLLECTION)
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = snapshot.toObjects(Booking::class.java)
            AuthResult.Success(bookings)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get customer bookings",
                exception = e
            )
        }
    }

    override suspend fun getVendorBookings(vendorId: String): AuthResult<List<Booking>> {
        return try {
            val snapshot = firestore.collection(BOOKINGS_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = snapshot.toObjects(Booking::class.java)
            AuthResult.Success(bookings)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get vendor bookings",
                exception = e
            )
        }
    }

    override suspend fun getPendingBookings(vendorId: String): AuthResult<List<Booking>> {
        return try {
            val snapshot = firestore.collection(BOOKINGS_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("status", "Booking Requested")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = snapshot.toObjects(Booking::class.java)
            AuthResult.Success(bookings)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get pending bookings",
                exception = e
            )
        }
    }

    override fun observeCustomerBookings(customerId: String): Flow<List<Booking>> = callbackFlow {
        val listener = firestore.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                trySend(bookings)
            }

        awaitClose { listener.remove() }
    }

    override fun observeVendorBookings(vendorId: String): Flow<List<Booking>> = callbackFlow {
        val listener = firestore.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                trySend(bookings)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun cancelBooking(bookingId: String, userId: String): AuthResult<Unit> {
        return try {
            val bookingSnapshot = firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .get()
                .await()

            val booking = bookingSnapshot.toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            if (booking.customerId != userId && booking.vendorId != userId) {
                throw Exception("Unauthorized")
            }

            if (booking.status == "Completed" || booking.status == "Payment Confirmed") {
                throw Exception("Cannot cancel: Booking already completed/paid")
            }

            firestore.collection(BOOKINGS_COLLECTION)
                .document(bookingId)
                .update(mapOf("status" to "Cancelled"))
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to cancel booking",
                exception = e
            )
        }
    }
}
