package com.example.bunnix.data.repository

import android.app.Activity
import android.util.Log
import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.config.FirebaseConfig.auth
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CORRECTED Implementation matching BUNNIX_COMPLETE_DATABASE_GUIDE.txt
 * Collection names and fields strictly adhere to the guide
 *
 * UPDATED: Now includes Apple Sign-In support
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authManager: AuthManager,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        // EXACT collection names from BUNNIX_COMPLETE_DATABASE_GUIDE.txt
        private const val USERS_COLLECTION = "users"
        private const val VENDOR_PROFILES_COLLECTION = "vendorProfiles"  // CORRECTED from "vendors"
        private const val MIN_PASSWORD_LENGTH = 6
        private const val MIN_NAME_LENGTH = 2
    }

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

            // 4. Create Firestore user document - EXACT fields from guide
            val user = User(
                userId = firebaseUser.uid,
                name = displayName,
                email = email,
                phone = phone,
                profilePicUrl = "",
                isVendor = isBusinessAccount,
                address = if (isBusinessAccount) businessAddress else "",
                city = "",
                state = "",
                country = "Nigeria",  // Default from guide
                createdAt = Timestamp.now(),
                lastActive = Timestamp.now()
            )

            // 5. Save user to Firestore
            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(user)
                .await()

            // 6. If business account, create VendorProfile - EXACT fields from guide
            if (isBusinessAccount) {
                val vendorProfile = VendorProfile(
                    vendorId = firebaseUser.uid,
                    userId = firebaseUser.uid,
                    businessName = businessName,
                    description = "",
                    coverPhotoUrl = "",
                    category = "",
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

                // Save to vendorProfiles collection (NOT "vendors")
                firestore.collection(VENDOR_PROFILES_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(vendorProfile)
                    .await()
            }

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Sign up failed",
                exception = e
            )
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            validateEmail(email)
            validatePassword(password)

            val firebaseUser = authManager.signInWithEmail(email, password)
            val user = getUserFromFirestore(firebaseUser.uid)

            updateLastActive(firebaseUser.uid)

            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Sign in failed",
                exception = e
            )
        }
    }

    override suspend fun signInWithPhone(
        phoneNumber: String,
        activity: Activity
    ): AuthResult<String> {
        return try {
            validatePhoneNumber(phoneNumber)
            val verificationId = authManager.sendPhoneVerificationCode(phoneNumber, activity)
            AuthResult.Success(verificationId)
        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Phone verification failed",
                exception = e
            )
        }
    }

    override suspend fun verifyPhoneOtp(
        verificationId: String,
        code: String
    ): AuthResult<User> {
        return try {
            validateOtpCode(code)
            val firebaseUser = authManager.verifyPhoneCode(verificationId, code)
            val existingUser = getUserFromFirestoreOrNull(firebaseUser.uid)

            val user = if (existingUser != null) {
                updateLastActive(firebaseUser.uid)
                existingUser
            } else {
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

    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                    ?: return@withContext AuthResult.Error("Authentication failed")

                val userRef = firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)

                val userDoc = userRef.get().await()

                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)

                    if (user != null) {
                        userRef.update("lastActive", Timestamp.now()).await()
                        AuthResult.Success(user)
                    } else {
                        AuthResult.Error("Failed to load user data")
                    }
                } else {
                    val newUser = User(
                        userId = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "User",
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

                    userRef.set(newUser).await()
                    AuthResult.Success(newUser)
                }

            } catch (e: Exception) {
                Log.e("AuthRepository", "Google Sign-In failed", e)
                AuthResult.Error(e.message ?: "Google Sign-In failed")
            }
        }
    }
//    // ==================== APPLE SIGN-IN (NEW) ====================
//
//    override suspend fun signInWithApple(
//        idToken: String,
//        rawNonce: String
//    ): AuthResult<User> {
//        return try {
//            val firebaseUser = authManager.signInWithApple(idToken, rawNonce)
//            val existingUser = getUserFromFirestoreOrNull(firebaseUser.uid)
//
//            val user = if (existingUser != null) {
//                updateLastActive(firebaseUser.uid)
//                existingUser
//            } else {
//                // Create new user from Apple sign-in
//                val newUser = User(
//                    userId = firebaseUser.uid,
//                    name = firebaseUser.displayName ?: "",
//                    email = firebaseUser.email ?: "",
//                    phone = firebaseUser.phoneNumber ?: "",
//                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: "",
//                    isVendor = false,
//                    address = "",
//                    city = "",
//                    state = "",
//                    country = "Nigeria",
//                    createdAt = Timestamp.now(),
//                    lastActive = Timestamp.now()
//                )
//
//                firestore.collection(USERS_COLLECTION)
//                    .document(firebaseUser.uid)
//                    .set(newUser)
//                    .await()
//
//                newUser
//            }
//
//            AuthResult.Success(user)
//
//        } catch (e: Exception) {
//            AuthResult.Error(
//                message = e.message ?: "Apple sign-in failed",
//                exception = e
//            )
//        }
//    }
//
//    override suspend fun startAppleSignIn(activity: Activity): AuthResult<User> {
//        return try {
//            val firebaseUser = authManager.startAppleSignIn(activity)
//            val existingUser = getUserFromFirestoreOrNull(firebaseUser.uid)
//
//            val user = if (existingUser != null) {
//                updateLastActive(firebaseUser.uid)
//                existingUser
//            } else {
//                val newUser = User(
//                    userId = firebaseUser.uid,
//                    name = firebaseUser.displayName ?: "",
//                    email = firebaseUser.email ?: "",
//                    phone = firebaseUser.phoneNumber ?: "",
//                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: "",
//                    isVendor = false,
//                    address = "",
//                    city = "",
//                    state = "",
//                    country = "Nigeria",
//                    createdAt = Timestamp.now(),
//                    lastActive = Timestamp.now()
//                )
//
//                firestore.collection(USERS_COLLECTION)
//                    .document(firebaseUser.uid)
//                    .set(newUser)
//                    .await()
//
//                newUser
//            }
//
//            AuthResult.Success(user)
//
//        } catch (e: Exception) {
//            AuthResult.Error(
//                message = e.message ?: "Apple sign-in failed",
//                exception = e
//            )
//        }
//    }
//
//    // ==================== END APPLE SIGN-IN ====================

    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            authManager.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Sign out failed", e)
        }
    }

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
            AuthResult.Error("Failed to get current user", e)
        }
    }

    override fun observeAuthState(): Flow<FirebaseUser?> {
        return authManager.observeAuthState()
    }

    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            validateEmail(email)
            authManager.sendPasswordResetEmail(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password reset failed", e)
        }
    }

    override suspend fun updateProfile(
        displayName: String?,
        phone: String?
    ): AuthResult<User> {
        return try {
            val currentUid = authManager.currentUser?.uid
                ?: throw Exception("No user signed in")

            displayName?.let {
                validateDisplayName(it)
                authManager.updateDisplayName(it)
            }

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

            val user = getUserFromFirestore(currentUid)
            AuthResult.Success(user)

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Profile update failed", e)
        }
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val currentUid = authManager.currentUser?.uid
                ?: throw Exception("No user signed in")

            firestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .delete()
                .await()

            authManager.deleteUser()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Account deletion failed", e)
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    private suspend fun getUserFromFirestore(uid: String): User {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
            ?: throw Exception("User document not found")
    }

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

    private suspend fun updateLastActive(uid: String) {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update("lastActive", Timestamp.now())
                .await()
        } catch (e: Exception) {
            // Non-critical
        }
    }

    // ==================== VALIDATION FUNCTIONS ====================

    private fun validateEmail(email: String) {
        if (email.isBlank()) throw Exception("Email cannot be empty")
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
        if (name.isBlank()) throw Exception("Name cannot be empty")
        if (name.length < MIN_NAME_LENGTH) {
            throw Exception("Name must be at least $MIN_NAME_LENGTH characters")
        }
    }

    private fun validatePhoneNumber(phone: String) {
        if (!phone.startsWith("+")) {
            throw Exception("Phone number must start with country code (e.g., +234)")
        }
        if (phone.length < 10) throw Exception("Invalid phone number")
    }

    private fun validateOtpCode(code: String) {
        if (code.length != 6) throw Exception("OTP code must be 6 digits")
        if (!code.all { it.isDigit() }) {
            throw Exception("OTP code must contain only digits")
        }
    }
}
