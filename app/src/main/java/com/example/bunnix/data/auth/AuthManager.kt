package com.example.bunnix.data.auth


import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for Firebase Authentication operations.
 * Wraps Firebase Auth SDK with suspend functions and proper error handling.
 *
 * This is injected as a singleton via Hilt.
 */
@Singleton
class AuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    /**
     * Get currently signed-in Firebase user
     */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Create new user with email and password
     *
     * @throws FirebaseAuthException if creation fails
     */
    suspend fun createUserWithEmail(
        email: String,
        password: String
    ): FirebaseUser {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            result.user ?: throw Exception("User creation failed - no user returned")
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Sign in existing user with email and password
     *
     * @throws FirebaseAuthException if sign-in fails
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): FirebaseUser {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            result.user ?: throw Exception("Sign-in failed - no user returned")
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Send phone verification code
     * Returns verification ID needed for OTP verification
     *
     * @param phoneNumber Phone in E.164 format (+234...)
     * @param activity Activity for SMS auto-retrieval
     * @return Verification ID string
     */
    suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): String {
        return try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // Auto-verification completed
                    }

                    override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                        throw e
                    }
                })
                .build()

            // This is simplified - in production, use a callback-based approach
            PhoneAuthProvider.verifyPhoneNumber(options)

            // Return placeholder - actual implementation needs callback handling
            "verification_id_placeholder"
        } catch (e: Exception) {
            throw Exception("Phone verification failed: ${e.message}")
        }
    }

    /**
     * Verify phone OTP code
     *
     * @param verificationId ID from sendPhoneVerificationCode
     * @param code 6-digit OTP code
     * @return Signed-in FirebaseUser
     */
    suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): FirebaseUser {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = firebaseAuth.signInWithCredential(credential).await()

            result.user ?: throw Exception("Phone verification failed - no user returned")
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Sign in with Google OAuth token
     *
     * @param idToken Google ID token from Google Sign-In SDK
     * @return Signed-in FirebaseUser
     */
    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            result.user ?: throw Exception("Google sign-in failed - no user returned")
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Update user's display name in Firebase Auth
     */
    suspend fun updateDisplayName(displayName: String) {
        currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates).await()
        } ?: throw Exception("No user signed in")
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Delete current user account
     */
    suspend fun deleteUser() {
        try {
            currentUser?.delete()?.await()
                ?: throw Exception("No user signed in")
        } catch (e: FirebaseAuthException) {
            throw mapFirebaseAuthException(e)
        }
    }

    /**
     * Observe Firebase Auth state changes
     * Emits whenever user signs in/out
     */
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    /**
     * Map Firebase Auth exceptions to user-friendly messages
     */
    private fun mapFirebaseAuthException(exception: FirebaseAuthException): Exception {
        val message = when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address format"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak (minimum 6 characters)"
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials provided"
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "Account exists with different sign-in method"
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already linked to another account"
            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled"
            "ERROR_INVALID_VERIFICATION_CODE" -> "Invalid verification code"
            "ERROR_INVALID_VERIFICATION_ID" -> "Invalid verification ID"
            else -> exception.message ?: "Authentication failed"
        }

        return Exception(message, exception)
    }
}
