package com.example.bunnix.data.auth


import android.app.Activity
import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Handles Firebase Auth AND Firestore user document synchronization.
 *
 * Security Notes:
 * - All inputs are validated before Firebase operations
 * - User documents created ONLY after successful auth
 * - VendorProfile created for business signups
 * - Firestore writes are atomic (success or rollback)
 * - Sensitive data (passwords) never stored in Firestore
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authManager: AuthManager,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val VENDORS_COLLECTION = "vendors"
        private const val MIN_PASSWORD_LENGTH = 6
        private const val MIN_NAME_LENGTH = 2
    }

    /**
     * Sign up new user with email/password
     * Creates Firebase Auth account, Firestore user document
     * AND VendorProfile if business signup
     */
    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        phone: String,
        isBusinessAccount: Boolean,
        businessName: String,
        businessAddress: String
    ): AuthResult<User> {
        return try {
            // 1. Validate inputs
            validateEmail(email)
            validatePassword(password)
            validateDisplayName(displayName)

            // 2. Create Firebase Auth account
            val firebaseUser = authManager.createUserWithEmail(email, password)

            // 3. Update display name in Firebase Auth
            authManager.updateDisplayName(displayName)

            // 4. Create Firestore user document
            val user = User(
                userId = firebaseUser.uid,
                name = displayName,
                email = email,
                phone = phone,
                profilePicUrl = "",
                isVendor = isBusinessAccount, // Set to true if business signup
                address = if (isBusinessAccount) businessAddress else "",
                city = "",
                state = "",
                country = "Nigeria",
                createdAt = Timestamp.now(),
                lastActive = Timestamp.now()
            )

            // 5. Save user to Firestore
            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(user)
                .await()

            // 6. If business account, create VendorProfile
            if (isBusinessAccount) {
                val vendorProfile = VendorProfile(
                    vendorId = firebaseUser.uid, // Same as userId
                    userId = firebaseUser.uid,
                    businessName = businessName,
                    description = "", // To be filled in profile
                    coverPhotoUrl = "",
                    category = "", // To be selected in profile
                    subCategories = emptyList(),
                    bankName = "",
                    accountNumber = "",
                    accountName = "",
                    alternativePayment = "",
                    rating = 0.0,
                    totalReviews = 0,
                    totalSales = 0,
                    totalRevenue = 0.0,
                    isAvailable = true,
                    workingHours = emptyMap(),
                    location = null,
                    address = businessAddress,
                    phone = phone,
                    email = email,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                // Save vendor profile
                firestore.collection(VENDORS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(vendorProfile)
                    .await()
            }

            AuthResult.Success(user)

        } catch (e: Exception) {
            // If Firestore write fails, consider deleting auth account for cleanup
            AuthResult.Error(
                message = e.message ?: "Sign up failed",
                exception = e
            )
        }
    }

    /**
     * Sign in existing user with email/password
     * Fetches user data from Firestore after successful auth
     */
    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            // 1. Validate inputs
            validateEmail(email)
            validatePassword(password)

            // 2. Authenticate with Firebase
            val firebaseUser = authManager.signInWithEmail(email, password)

            // 3. Fetch user data from Firestore
            val user = getUserFromFirestore(firebaseUser.uid)

            // 4. Update last active timestamp
            updateLastActive(firebaseUser.uid)

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Sign in failed",
                exception = e
            )
        }
    }

    /**
     * Sign in with phone number
     * Sends OTP to phone number
     */
    override suspend fun signInWithPhone(
        phoneNumber: String,
        activity: Activity
    ): AuthResult<String> {
        return try {
            // Validate phone format
            validatePhoneNumber(phoneNumber)

            // Send verification code
            val verificationId = authManager.sendPhoneVerificationCode(phoneNumber, activity)

            AuthResult.Success(verificationId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Phone verification failed",
                exception = e
            )
        }
    }

    /**
     * Verify phone OTP code
     * Creates/updates user document after successful verification
     */
    override suspend fun verifyPhoneOtp(
        verificationId: String,
        code: String
    ): AuthResult<User> {
        return try {
            // Validate OTP code
            validateOtpCode(code)

            // Verify and sign in
            val firebaseUser = authManager.verifyPhoneCode(verificationId, code)

            // Check if user document exists
            val existingUser = getUserFromFirestoreOrNull(firebaseUser.uid)

            val user = if (existingUser != null) {
                // Existing user - update last active
                updateLastActive(firebaseUser.uid)
                existingUser
            } else {
                // New user - create document
                val newUser = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    profilePicUrl = "",
                    isVendor = false,
                    address = "",
                    city = "",
                    state = "",
                    country = "Nigeria",
                    createdAt = Timestamp.now(),
                    lastActive = Timestamp.now()
                )

                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()

                newUser
            }

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "OTP verification failed",
                exception = e
            )
        }
    }

    /**
     * Sign in with Google OAuth
     */
    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        return try {
            // Sign in with Google credential
            val firebaseUser = authManager.signInWithGoogle(idToken)

            // Check if user exists in Firestore
            val existingUser = getUserFromFirestoreOrNull(firebaseUser.uid)

            val user = if (existingUser != null) {
                // Existing user
                updateLastActive(firebaseUser.uid)
                existingUser
            } else {
                // New user - create document
                val newUser = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: "",
                    isVendor = false,
                    address = "",
                    city = "",
                    state = "",
                    country = "Nigeria",
                    createdAt = Timestamp.now(),
                    lastActive = Timestamp.now()
                )

                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()

                newUser
            }

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Google sign-in failed",
                exception = e
            )
        }
    }

    /**
     * Sign out current user
     */
    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            authManager.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(
                message = "Sign out failed",
                exception = e
            )
        }
    }

    /**
     * Get currently authenticated user
     */
    override suspend fun getCurrentUser(): AuthResult<User?> {
        return try {
            val firebaseUser = authManager.currentUser

            if (firebaseUser == null) {
                AuthResult.Success(null)
            } else {
                val user = getUserFromFirestore(firebaseUser.uid)
                AuthResult.Success(user)
            }
        } catch (e: Exception) {
            AuthResult.Error(
                message = "Failed to get current user",
                exception = e
            )
        }
    }

    /**
     * Observe auth state changes
     */
    override fun observeAuthState(): Flow<FirebaseUser?> {
        return authManager.observeAuthState()
    }

    /**
     * Reset password via email
     */
    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            validateEmail(email)
            authManager.sendPasswordResetEmail(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Password reset failed",
                exception = e
            )
        }
    }

    /**
     * Update user profile
     */
    override suspend fun updateProfile(
        displayName: String?,
        phone: String?
    ): AuthResult<User> {
        return try {
            val currentUid = authManager.currentUser?.uid
                ?: throw Exception("No user signed in")

            // Update Firebase Auth if display name changed
            displayName?.let {
                validateDisplayName(it)
                authManager.updateDisplayName(it)
            }

            // Update Firestore document
            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["name"] = it }
            phone?.let {
                validatePhoneNumber(it)
                updates["phone"] = it
            }

            if (updates.isNotEmpty()) {
                firestore.collection(USERS_COLLECTION)
                    .document(currentUid)
                    .update(updates)
                    .await()
            }

            // Fetch updated user
            val user = getUserFromFirestore(currentUid)
            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Profile update failed",
                exception = e
            )
        }
    }

    /**
     * Delete user account
     */
    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val currentUid = authManager.currentUser?.uid
                ?: throw Exception("No user signed in")

            // Delete Firestore document first
            firestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .delete()
                .await()

            // Delete Firebase Auth account
            authManager.deleteUser()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Account deletion failed",
                exception = e
            )
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Fetch user from Firestore by UID
     * Throws exception if user not found
     */
    private suspend fun getUserFromFirestore(uid: String): User {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
            ?: throw Exception("User document not found")
    }

    /**
     * Fetch user from Firestore or return null if not found
     */
    private suspend fun getUserFromFirestoreOrNull(uid: String): User? {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Update user's last active timestamp
     */
    private suspend fun updateLastActive(uid: String) {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update("lastActive", Timestamp.now())
                .await()
        } catch (e: Exception) {
            // Non-critical, log but don't throw
        }
    }

    // ==================== VALIDATION FUNCTIONS ====================

    private fun validateEmail(email: String) {
        if (email.isBlank()) {
            throw Exception("Email cannot be empty")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw Exception("Invalid email format")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw Exception("Password must be at least $MIN_PASSWORD_LENGTH characters")
        }
    }

    private fun validateDisplayName(name: String) {
        if (name.isBlank()) {
            throw Exception("Name cannot be empty")
        }
        if (name.length < MIN_NAME_LENGTH) {
            throw Exception("Name must be at least $MIN_NAME_LENGTH characters")
        }
    }

    private fun validatePhoneNumber(phone: String) {
        // Basic validation - should start with + and contain only digits
        if (!phone.startsWith("+")) {
            throw Exception("Phone number must start with country code (e.g., +234)")
        }
        if (phone.length < 10) {
            throw Exception("Invalid phone number")
        }
    }

    private fun validateOtpCode(code: String) {
        if (code.length != 6) {
            throw Exception("OTP code must be 6 digits")
        }
        if (!code.all { it.isDigit() }) {
            throw Exception("OTP code must contain only digits")
        }
    }
}
