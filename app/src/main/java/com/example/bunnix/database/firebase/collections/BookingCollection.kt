package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Booking
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object BookingCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.BOOKINGS)

    // CREATE BOOKING (Customer Side)
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val bookingNumber = generateBookingNumber()
            val bookingData = booking.copy(
                bookingNumber = bookingNumber,
                status = "Booking Requested",
                createdAt = Timestamp.now()
            )
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

    // GET SINGLE BOOKING
    suspend fun getBookingById(bookingId: String): Result<Booking> {
        return try {
            val snapshot = collection.document(bookingId).get().await()
            val booking = snapshot.toObject(Booking::class.java)
            if (booking != null) Result.success(booking) else Result.failure(Exception("Booking not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateBookingNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (100000..999999).random()
        return "SV-$date-$random"
    }
}