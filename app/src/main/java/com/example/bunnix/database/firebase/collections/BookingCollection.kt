package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Booking
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object BookingCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.BOOKINGS)

    // CREATE BOOKING
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val bookingNumber = generateBookingNumber()
            val bookingData = booking.copy(bookingNumber = bookingNumber)
            val docRef = collection.add(bookingData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET CUSTOMER BOOKINGS (Real-time)
    fun getCustomerBookings(customerId: String): Flow<List<Booking>> = callbackFlow {
        val listener = collection
            .whereEqualTo("customerId", customerId)
            .orderBy("scheduledDate", Query.Direction.DESCENDING)
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

    // GET VENDOR BOOKINGS (Real-time)
    fun getVendorBookings(vendorId: String): Flow<List<Booking>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
            .orderBy("scheduledDate", Query.Direction.DESCENDING)
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

    // GET PENDING BOOKINGS (Awaiting payment verification)
    fun getPendingBookings(vendorId: String): Flow<List<Booking>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
            .whereEqualTo("status", "Payment Submitted")
            .whereEqualTo("paymentVerified", false)
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

    // UPLOAD PAYMENT RECEIPT
    suspend fun uploadPaymentReceipt(
        bookingId: String,
        receiptUrl: String,
        paymentMethod: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "paymentReceiptUrl" to receiptUrl,
                "paymentMethod" to paymentMethod,
                "status" to "Payment Submitted"
            )
            collection.document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // VERIFY PAYMENT (Vendor confirms)
    suspend fun verifyPayment(bookingId: String, vendorId: String): Result<Unit> {
        return try {
            val statusUpdate = mapOf(
                "status" to "Payment Confirmed",
                "timestamp" to Timestamp.now(),
                "updatedBy" to vendorId
            )

            val updates = mapOf(
                "paymentVerified" to true,
                "status" to "Payment Confirmed",
                "statusHistory" to FieldValue.arrayUnion(statusUpdate)
            )

            collection.document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // VENDOR ACCEPT BOOKING
    suspend fun acceptBooking(bookingId: String, vendorId: String, notes: String = ""): Result<Unit> {
        return try {
            val statusUpdate = mapOf(
                "status" to "Vendor Accepted",
                "timestamp" to Timestamp.now(),
                "updatedBy" to vendorId
            )

            val updates = mapOf(
                "status" to "Vendor Accepted",
                "vendorNotes" to notes,
                "statusHistory" to FieldValue.arrayUnion(statusUpdate)
            )

            collection.document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE BOOKING STATUS
    suspend fun updateBookingStatus(bookingId: String, newStatus: String, userId: String): Result<Unit> {
        return try {
            val statusUpdate = mapOf(
                "status" to newStatus,
                "timestamp" to Timestamp.now(),
                "updatedBy" to userId
            )

            val updates = mutableMapOf<String, Any>(
                "status" to newStatus,
                "statusHistory" to FieldValue.arrayUnion(statusUpdate)
            )

            if (newStatus == "Completed") {
                updates["completedAt"] = Timestamp.now()
            }

            collection.document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CANCEL BOOKING
    suspend fun cancelBooking(bookingId: String, userId: String): Result<Unit> {
        return updateBookingStatus(bookingId, "Cancelled", userId)
    }

    // GENERATE BOOKING NUMBER
    private fun generateBookingNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (100000..999999).random()
        return "BNX-$date-$random"
    }
}