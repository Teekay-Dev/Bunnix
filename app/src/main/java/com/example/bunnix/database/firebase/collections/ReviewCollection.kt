package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Review
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ReviewCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.REVIEWS)

    // ADD REVIEW
    suspend fun addReview(review: Review): Result<String> {
        return try {
            val docRef = collection.add(review).await()

            // Update vendor rating
            updateVendorRating(review.vendorId)

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET VENDOR REVIEWS (Real-time)
    fun getVendorReviews(vendorId: String): Flow<List<Review>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    // GET CUSTOMER REVIEWS
    fun getCustomerReviews(customerId: String): Flow<List<Review>> = callbackFlow {
        val listener = collection
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    // ADD VENDOR RESPONSE
    suspend fun addVendorResponse(reviewId: String, response: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "vendorResponse" to response,
                "vendorResponseAt" to Timestamp.now()
            )
            collection.document(reviewId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE VENDOR RATING
    private suspend fun updateVendorRating(vendorId: String) {
        try {
            val reviews = collection
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()
                .toObjects(Review::class.java)

            if (reviews.isEmpty()) return

            val totalRating = reviews.sumOf { it.rating }
            val averageRating = totalRating.toDouble() / reviews.size

            // Round to 1 decimal place
            val roundedRating = String.format("%.1f", averageRating).toDouble()

            val vendorUpdates = mapOf(
                "rating" to roundedRating,
                "totalReviews" to reviews.size
            )

            FirebaseConfig.firestore
                .collection(FirebaseConfig.Collections.VENDOR_PROFILES)
                .document(vendorId)
                .update(vendorUpdates)
                .await()
        } catch (e: Exception) {
            // Ignore errors
        }
    }
}