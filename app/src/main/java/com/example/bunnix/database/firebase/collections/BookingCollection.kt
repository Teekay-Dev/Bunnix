package com.example.bunnix.database.firebase.collections

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.bunnix.database.models.Booking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object BookingCollection {
    val db = FirebaseFirestore.getInstance()
    private val bookingsRef = db.collection("bookings")

    suspend fun createBooking(booking: Booking): String {
        val docRef = bookingsRef.document()

        // ✅ CRITICAL: Set the ID inside the object before saving
        val newBooking = booking.copy(bookingId = docRef.id, bookingNumber = generateBookingNumber())

        docRef.set(newBooking).await()
        return docRef.id
    }

    // ✅ REAL-TIME LISTENER
    fun getBookingByIdFlow(bookingId: String): Flow<Booking?> = callbackFlow {
        val listener = bookingsRef.document(bookingId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val booking = snapshot?.toObject(Booking::class.java)
            trySend(booking)
        }
        awaitClose { listener.remove() }
    }

    private fun generateBookingNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (100000..999999).random()
        return "SV-$date-$random"
    }
}