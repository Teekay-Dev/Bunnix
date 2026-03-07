package com.example.bunnix.data.repository

import android.app.Activity
import android.util.Log
import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authManager: AuthManager,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    private val TAG = "AuthRepository"

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val VENDOR_PROFILES_COLLECTION = "vendorProfiles"
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
        businessAddress: String,
        category: String
    ): AuthResult<User> {
        return try {
            // Check if email already exists
            val existingUserQuery = db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!existingUserQuery.isEmpty) {
                val document = existingUserQuery.documents[0]
                val isVendor = document.getBoolean("isVendor") ?: false
                val role = if (isVendor) "Business/Vendor" else "Customer"
                return AuthResult.Error("Email already exists as $role. Use another email!")
            }

            // Create Firebase Auth account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return AuthResult.Error("Account creation failed")

            val userId = firebaseUser.uid

            // Create User document in Firestore
            val newUser = User(
                userId = userId,
                name = displayName,
                email = email,
                phone = phone,
                profilePicUrl = "",
                isVendor = isBusinessAccount,
                address = if (isBusinessAccount) businessAddress else "",
                city = "",
                state = "",
                country = "Nigeria",
                createdAt = Timestamp.now(),
                lastActive = Timestamp.now()
            )

            // Save user to Firestore
            db.collection(USERS_COLLECTION)
                .document(userId)
                .set(newUser)
                .await()

            Log.d(TAG, "✅ User created in Firestore: $userId")

            // If business account, create VendorProfile with pending status
            if (isBusinessAccount) {
                val vendorProfile = VendorProfile(
                    vendorId = userId,
                    userId = userId,
                    businessName = businessName,
                    description = "",
                    coverPhotoUrl = "",
                    category = category,
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
                    status = "pending", // Pending admin approval
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                db.collection(VENDOR_PROFILES_COLLECTION)
                    .document(userId)
                    .set(vendorProfile)
                    .await()

                Log.d(TAG, "✅ VendorProfile created with pending status: $userId")
            }

            AuthResult.Success(newUser)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Signup failed", e)
            AuthResult.Error(e.message ?: "Signup failed. Please try again.")
        }
    }

    override suspend fun createFinalUser(user: User): AuthResult<User> {
        // This method is for creating user after verification
        // In the current flow, signUpWithEmail handles everything
        // Keeping this for compatibility
        return try {
            db.collection(USERS_COLLECTION)
                .document(user.userId)
                .set(user)
                .await()

            Log.d(TAG, "✅ Final user created: ${user.userId}")
            AuthResult.Success(user)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Final user creation failed", e)
            AuthResult.Error(e.message ?: "Failed to save user data.")
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            // Sign in with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return AuthResult.Error("Login failed. No user signed in.")

            val userId = firebaseUser.uid

            // Fetch user document from Firestore
            val userDoc = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!userDoc.exists()) {
                Log.e(TAG, "❌ User document not found for: $userId")
                return AuthResult.Error("User document not found. Please contact support.")
            }

            val user = userDoc.toObject(User::class.java)
                ?: return AuthResult.Error("Failed to load user data")

            // Update last active
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update("lastActive", Timestamp.now())
                .await()

            Log.d(TAG, "✅ Login successful: ${user.email}, isVendor: ${user.isVendor}")

            AuthResult.Success(user)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Login failed", e)
            AuthResult.Error(e.message ?: "Login failed. Please check your credentials.")
        }
    }

    override suspend fun signInWithPhone(phoneNumber: String, activity: Activity): AuthResult<String> {
        return try {
            val verificationId = authManager.sendPhoneVerificationCode(phoneNumber, activity)
            AuthResult.Success(verificationId)
        } catch (e: Exception) {
            AuthResult.Error("Failed to send SMS: ${e.message}")
        }
    }

    override suspend fun verifyPhoneOtp(verificationId: String, code: String): AuthResult<String> {
        return try {
            val firebaseUser = authManager.verifyPhoneCode(verificationId, code)
            AuthResult.Success(firebaseUser.uid)
        } catch (e: Exception) {
            AuthResult.Error("Invalid OTP: ${e.message}")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return AuthResult.Error("Google Sign-In failed")

            val userId = firebaseUser.uid

            // Check if user exists
            val userDoc = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                    ?: return AuthResult.Error("Failed to load user data")

                db.collection(USERS_COLLECTION)
                    .document(userId)
                    .update("lastActive", Timestamp.now())
                    .await()

                Log.d(TAG, "✅ Google Sign-In (existing): ${user.email}")
                AuthResult.Success(user)

            } else {
                // Create new user
                val newUser = User(
                    userId = userId,
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

                db.collection(USERS_COLLECTION)
                    .document(userId)
                    .set(newUser)
                    .await()

                Log.d(TAG, "✅ Google Sign-In (new user): ${newUser.email}")
                AuthResult.Success(newUser)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Google Sign-In failed", e)
            val errorMessage = when {
                e.message?.contains("NETWORK_ERROR") == true -> "No internet connection. Please check your network."
                e.message?.contains("10:") == true -> "Google Sign-In configuration error. Please contact support."
                e.message?.contains("12500") == true -> "SHA-1 fingerprint not configured. Please contact support."
                else -> e.message ?: "Google Sign-In failed. Please try again."
            }
            AuthResult.Error(errorMessage)
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            auth.signOut()
            Log.d(TAG, "✅ User signed out")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Sign out failed", e)
            AuthResult.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun getCurrentUser(): AuthResult<User> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return AuthResult.Error("No user logged in")

            val userId = firebaseUser.uid

            val userDoc = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!userDoc.exists()) {
                return AuthResult.Error("User data not found")
            }

            val user = userDoc.toObject(User::class.java)
                ?: return AuthResult.Error("Failed to load user data")

            AuthResult.Success(user)

        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            AuthResult.Error(e.message ?: "Failed to get user")
        }
    }

    override fun observeAuthState(): Flow<FirebaseUser?> = authManager.observeAuthState()

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
            val currentUid = firebaseAuth.currentUser?.uid
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
            val currentUid = firebaseAuth.currentUser?.uid
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

        if (!snapshot.exists()) {
            return User(
                userId = uid,
                name = "New User",
                email = firebaseAuth.currentUser?.email ?: ""
            )
        }

        return snapshot.toObject(User::class.java) ?: throw Exception("Format error")
    }

    // ==================== VALIDATION FUNCTIONS ====================

    override suspend fun checkEmailAvailability(email: String): AuthResult<String?> {
        return try {
            val query = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!query.isEmpty) {
                val document = query.documents[0]
                val isVendor = document.getBoolean("isVendor") ?: false
                val role = if (isVendor) "Business" else "Customer"
                AuthResult.Error("Email already exists as $role. Use another email!")
            } else {
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error checking email availability")
        }
    }

    override suspend fun sendEmailOtp(email: String): AuthResult<Unit> {
        return try {
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Failed to send verification code")
        }
    }

    override suspend fun verifyEmailOtp(email: String, otp: String): AuthResult<Unit> {
        return try {
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Invalid Email OTP")
        }
    }

    override suspend fun sendPhoneOtp(phoneNumber: String, activity: Activity): AuthResult<String> {
        return signInWithPhone(phoneNumber, activity)
    }

    override suspend fun checkBusinessApprovalStatus(uid: String): Flow<String> {
        return callbackFlow {
            val listener = firestore.collection(VENDOR_PROFILES_COLLECTION)
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val status = snapshot.getString("status") ?: "pending"
                        trySend(status)
                    } else {
                        trySend("pending")
                    }
                }

            // Remove listener when flow is cancelled
            awaitClose { listener.remove() }
        }
    }

    private fun validateEmail(email: String) {
        if (email.isBlank()) throw Exception("Email cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw Exception("Invalid email format")
        }
    }

    private fun validateDisplayName(name: String) {
        if (name.isBlank()) throw Exception("Name cannot be empty")
        if (name.length < 2) {
            throw Exception("Name must be at least 2 characters")
        }
    }

    private fun validatePhoneNumber(phone: String) {
        if (phone.isBlank()) throw Exception("Phone number cannot be empty")
        if (!phone.startsWith("+") && !phone.startsWith("0")) {
            throw Exception("Invalid phone number format")
        }
    }
}