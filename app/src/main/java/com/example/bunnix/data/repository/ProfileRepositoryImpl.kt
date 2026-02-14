package com.example.bunnix.data.repository

import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.ProfileRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CORRECTED - Matches BUNNIX_COMPLETE_DATABASE_GUIDE.txt exactly
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : ProfileRepository {

    companion object {
        // CORRECTED collection names from database guide
        private const val USERS_COLLECTION = "users"
        private const val VENDOR_PROFILES_COLLECTION = "vendorProfiles"  // CORRECTED!

        // CORRECTED bucket names from database guide
        private const val PROFILE_BUCKET = "user-profiles"
        private const val VENDOR_PHOTOS_BUCKET = "vendor-photos"  // CORRECTED from "vendor-covers"
    }

    // ==================== USER PROFILE ====================

    override suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phone: String?,
        address: String?,
        city: String?,
        state: String?,
        profilePicUrl: String?
    ): AuthResult<User> {
        return try {
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            phone?.let { updates["phone"] = it }
            address?.let { updates["address"] = it }
            city?.let { updates["city"] = it }
            state?.let { updates["state"] = it }
            profilePicUrl?.let { updates["profilePicUrl"] = it }
            updates["lastActive"] = Timestamp.now()

            if (updates.isEmpty()) {
                return AuthResult.Error("No fields to update")
            }

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()

            getUserProfile(userId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update profile",
                exception = e
            )
        }
    }

    override suspend fun getUserProfile(userId: String): AuthResult<User> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("User profile not found")

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get user profile",
                exception = e
            )
        }
    }

    override fun observeUserProfile(userId: String): Flow<User?> = callbackFlow {
        val listener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }

        awaitClose {
            listener.remove()
        }
    }

    // ==================== VENDOR PROFILE ====================

    override suspend fun updateVendorProfile(
        vendorId: String,
        businessName: String?,
        description: String?,
        category: String?,
        subCategories: List<String>?,
        address: String?,
        phone: String?,
        email: String?,
        coverPhotoUrl: String?,
        bankName: String?,
        accountNumber: String?,
        accountName: String?,
        alternativePayment: String?,
        workingHours: Map<String, String>?,
        isAvailable: Boolean?
    ): AuthResult<VendorProfile> {
        return try {
            val updates = mutableMapOf<String, Any>()
            businessName?.let { updates["businessName"] = it }
            description?.let { updates["description"] = it }
            category?.let { updates["category"] = it }
            subCategories?.let { updates["subCategories"] = it }
            address?.let { updates["address"] = it }
            phone?.let { updates["phone"] = it }
            email?.let { updates["email"] = it }
            coverPhotoUrl?.let { updates["coverPhotoUrl"] = it }
            bankName?.let { updates["bankName"] = it }
            accountNumber?.let { updates["accountNumber"] = it }
            accountName?.let { updates["accountName"] = it }
            alternativePayment?.let { updates["alternativePayment"] = it }
            workingHours?.let { updates["workingHours"] = it }
            isAvailable?.let { updates["isAvailable"] = it }
            updates["updatedAt"] = Timestamp.now()

            if (updates.isEmpty()) {
                return AuthResult.Error("No fields to update")
            }

            // CORRECTED collection name
            firestore.collection(VENDOR_PROFILES_COLLECTION)
                .document(vendorId)
                .update(updates)
                .await()

            getVendorProfile(vendorId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update vendor profile",
                exception = e
            )
        }
    }

    override suspend fun getVendorProfile(vendorId: String): AuthResult<VendorProfile> {
        return try {
            // CORRECTED collection name
            val snapshot = firestore.collection(VENDOR_PROFILES_COLLECTION)
                .document(vendorId)
                .get()
                .await()

            val vendorProfile = snapshot.toObject(VendorProfile::class.java)
                ?: throw Exception("Vendor profile not found")

            AuthResult.Success(vendorProfile)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get vendor profile",
                exception = e
            )
        }
    }

    override fun observeVendorProfile(vendorId: String): Flow<VendorProfile?> = callbackFlow {
        // CORRECTED collection name
        val listener = firestore.collection(VENDOR_PROFILES_COLLECTION)
            .document(vendorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val vendorProfile = snapshot?.toObject(VendorProfile::class.java)
                trySend(vendorProfile)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun createVendorProfile(
        userId: String,
        businessName: String,
        businessAddress: String,
        category: String
    ): AuthResult<VendorProfile> {
        return try {
            // Check if vendor profile already exists
            val existingVendor = try {
                firestore.collection(VENDOR_PROFILES_COLLECTION)
                    .document(userId)
                    .get()
                    .await()
                    .toObject(VendorProfile::class.java)
            } catch (e: Exception) {
                null
            }

            if (existingVendor != null) {
                return AuthResult.Error("Vendor profile already exists")
            }

            // Get user data
            val userSnapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = userSnapshot.toObject(User::class.java)
                ?: throw Exception("User not found")

            // Create vendor profile
            val vendorProfile = VendorProfile(
                vendorId = userId,
                userId = userId,
                businessName = businessName,
                description = "",
                category = category,
                address = businessAddress,
                phone = user.phone,
                email = user.email,
                coverPhotoUrl = "",
                isAvailable = true,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            // CORRECTED collection name
            firestore.collection(VENDOR_PROFILES_COLLECTION)
                .document(userId)
                .set(vendorProfile)
                .await()

            // Update user's isVendor flag
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update("isVendor", true)
                .await()

            AuthResult.Success(vendorProfile)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to create vendor profile",
                exception = e
            )
        }
    }

    // ==================== IMAGE UPLOADS ====================

    override suspend fun uploadProfilePicture(
        userId: String,
        imageUri: String
    ): AuthResult<String> {
        return try {
            val file = File(Uri.parse(imageUri).path ?: throw Exception("Invalid image URI"))
            val fileName = "${userId}_${System.currentTimeMillis()}.jpg"

            val bucket = supabase.storage.from(PROFILE_BUCKET)
            bucket.upload(fileName, file.readBytes())

            val publicUrl = bucket.publicUrl(fileName)

            AuthResult.Success(publicUrl)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload profile picture",
                exception = e
            )
        }
    }

    override suspend fun uploadCoverPhoto(
        vendorId: String,
        imageUri: String
    ): AuthResult<String> {
        return try {
            val file = File(Uri.parse(imageUri).path ?: throw Exception("Invalid image URI"))
            val fileName = "${vendorId}_cover_${System.currentTimeMillis()}.jpg"

            // CORRECTED bucket name
            val bucket = supabase.storage.from(VENDOR_PHOTOS_BUCKET)
            bucket.upload(fileName, file.readBytes())

            val publicUrl = bucket.publicUrl(fileName)

            AuthResult.Success(publicUrl)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload cover photo",
                exception = e
            )
        }
    }
}
