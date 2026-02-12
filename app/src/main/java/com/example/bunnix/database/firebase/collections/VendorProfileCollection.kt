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

    private val collection = FirebaseConfig.firestore
        .collection(FirebaseConfig.Collections.VENDOR_PROFILES)

    /**
     * CREATE VENDOR PROFILE
     */
    suspend fun createVendorProfile(vendorId: String, vendorProfile: VendorProfile): Result<Unit> {
        return try {
            val profileData = hashMapOf(
                "userId" to vendorProfile.userId,
                "businessName" to vendorProfile.businessName,
                "description" to vendorProfile.description,
                "coverPhotoUrl" to vendorProfile.coverPhotoUrl,
                "category" to vendorProfile.category,
                "subCategories" to vendorProfile.subCategories,
                "bankName" to vendorProfile.bankName,
                "accountNumber" to vendorProfile.accountNumber,
                "accountName" to vendorProfile.accountName,
                "alternativePayment" to vendorProfile.alternativePayment,
                "rating" to vendorProfile.rating,
                "totalReviews" to vendorProfile.totalReviews,
                "totalSales" to vendorProfile.totalSales,
                "totalRevenue" to vendorProfile.totalRevenue,
                "isAvailable" to vendorProfile.isAvailable,
                "workingHours" to vendorProfile.workingHours,
                "location" to vendorProfile.location,
                "address" to vendorProfile.address,
                "phone" to vendorProfile.phone,
                "email" to vendorProfile.email,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            collection.document(vendorId).set(profileData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * GET VENDOR PROFILE
     */
    suspend fun getVendorProfile(vendorId: String): Result<VendorProfile?> {
        return try {
            val document = collection.document(vendorId).get().await()
            val profile = document.toObject(VendorProfile::class.java)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
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