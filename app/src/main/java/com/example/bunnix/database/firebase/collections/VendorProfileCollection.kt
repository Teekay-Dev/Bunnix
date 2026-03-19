package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.VendorProfile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object VendorProfileCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.VENDOR_PROFILES)

    // GET VENDOR PROFILE (One-time fetch for Bank Details)
    suspend fun getVendorProfile(vendorId: String): Result<VendorProfile> {
        return try {
            val snapshot = collection.document(vendorId).get().await()
            val vendor = snapshot.toObject(VendorProfile::class.java)
            if (vendor != null) Result.success(vendor) else Result.failure(Exception("Vendor not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET VENDOR PUBLIC INFO (Real-time for Vendor Profile Screen)
    fun getVendorRealtime(vendorId: String): Flow<VendorProfile?> = callbackFlow {
        val listener = collection.document(vendorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val vendor = snapshot?.toObject(VendorProfile::class.java)
                trySend(vendor)
            }
        awaitClose { listener.remove() }
    }

    /**
     * GET ALL VENDOR PROFILES (Real-time)
     */
    fun getAllVendorProfiles(): Flow<List<VendorProfile>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val profiles = snapshot?.toObjects(VendorProfile::class.java) ?: emptyList()
            trySend(profiles)
        }

        awaitClose { listener.remove() }
    }

    /**
     * GET VENDORS BY CATEGORY (Real-time)
     */
    fun getVendorsByCategory(category: String): Flow<List<VendorProfile>> = callbackFlow {
        val listener = collection
            .whereEqualTo("category", category)
            .whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val profiles = snapshot?.toObjects(VendorProfile::class.java) ?: emptyList()
                trySend(profiles)
            }

        awaitClose { listener.remove() }
    }

    /**
     * GET VENDOR PROFILE BY USER ID
     */
    suspend fun getVendorProfileByUserId(userId: String): Result<VendorProfile?> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            val profile = snapshot.documents.firstOrNull()?.toObject(VendorProfile::class.java)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE VENDOR PROFILE
     */
    suspend fun updateVendorProfile(vendorId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updateData = updates.toMutableMap()
            updateData["updatedAt"] = FieldValue.serverTimestamp()

            collection.document(vendorId).update(updateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE COVER PHOTO
     */
    suspend fun updateCoverPhoto(vendorId: String, coverPhotoUrl: String): Result<Unit> {
        return try {
            collection.document(vendorId).update(
                mapOf(
                    "coverPhotoUrl" to coverPhotoUrl,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * DELETE VENDOR PROFILE
     */
    suspend fun deleteVendorProfile(vendorId: String): Result<Unit> {
        return try {
            collection.document(vendorId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE VENDOR RATING
     */
    suspend fun updateVendorRating(vendorId: String, newRating: Double, totalReviews: Int): Result<Unit> {
        return try {
            collection.document(vendorId).update(
                mapOf(
                    "rating" to newRating,
                    "totalReviews" to totalReviews,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * INCREMENT TOTAL SALES
     */
    suspend fun incrementTotalSales(vendorId: String, amount: Double): Result<Unit> {
        return try {
            collection.document(vendorId).update(
                mapOf(
                    "totalSales" to FieldValue.increment(1),
                    "totalRevenue" to FieldValue.increment(amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * TOGGLE AVAILABILITY
     */
    suspend fun toggleAvailability(vendorId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            collection.document(vendorId).update(
                mapOf(
                    "isAvailable" to isAvailable,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE WORKING HOURS
     */
    suspend fun updateWorkingHours(vendorId: String, workingHours: Map<String, String>): Result<Unit> {
        return try {
            collection.document(vendorId).update(
                mapOf(
                    "workingHours" to workingHours,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * SUBSCRIBE TO VENDOR PROFILE (Real-time)
     */
    fun subscribeToVendorProfile(vendorId: String, callback: (VendorProfile?) -> Unit): ListenerRegistration {
        return collection.document(vendorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(null)
                    return@addSnapshotListener
                }

                val profile = snapshot?.toObject(VendorProfile::class.java)
                callback(profile)
            }
    }

    /**
     * SEARCH VENDORS BY BUSINESS NAME
     */
    suspend fun searchVendors(searchQuery: String): Result<List<VendorProfile>> {
        return try {
            val snapshot = collection
                .whereEqualTo("isAvailable", true)
                .get()
                .await()

            val allProfiles = snapshot.toObjects(VendorProfile::class.java)
            val filteredProfiles = allProfiles.filter { profile ->
                profile.businessName.contains(searchQuery, ignoreCase = true) ||
                        profile.description.contains(searchQuery, ignoreCase = true)
            }

            Result.success(filteredProfiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}