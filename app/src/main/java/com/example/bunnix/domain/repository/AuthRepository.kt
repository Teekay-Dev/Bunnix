package com.example.bunnix.domain.repository

import android.app.Activity
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * This defines the contract that implementation must follow.
 *
 * All methods return AuthResult for consistent error handling.
 *
 * UPDATED: Now includes Apple Sign-In support
 */
interface AuthRepository {

    /**
     * Sign up a new user with email and password
     * Creates Firebase Auth account AND Firestore user document
     * If isBusinessAccount = true, also creates VendorProfile document
     *
     * @param email User's email address
     * @param password User's password (min 6 characters)
     * @param displayName User's full name
     * @param phone Phone number
     * @param isBusinessAccount Whether this is a business/vendor signup
     * @param businessName Business name (required if isBusinessAccount = true)
     * @param businessAddress Business address (required if isBusinessAccount = true)
     * @return AuthResult with User data or error
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        phone: String = "",
        isBusinessAccount: Boolean = false,
        businessName: String = "",
        businessAddress: String = ""
    ): AuthResult<User>

    /**
     * Sign in existing user with email and password
     *
     * @param email User's email address
     * @param password User's password
     * @return AuthResult with User data or error
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User>

    /**
     * Sign in with phone number (requires OTP verification)
     * This is a two-step process:
     * 1. Send OTP to phone
     * 2. Verify OTP code
     *
     * @param phoneNumber Phone number in E.164 format (+234...)
     * @param activity Activity context for SMS retrieval
     * @return AuthResult with verification ID for OTP step
     */
    suspend fun signInWithPhone(
        phoneNumber: String,
        activity: Activity
    ): AuthResult<String> // Returns verification ID

    /**
     * Verify phone OTP and complete sign-in
     *
     * @param verificationId ID from signInWithPhone step
     * @param code 6-digit OTP code
     * @return AuthResult with User data or error
     */
    suspend fun verifyPhoneOtp(
        verificationId: String,
        code: String
    ): AuthResult<User>

    /**
     * Sign in with Google (OAuth)
     *
     * @param idToken Google ID token from Google Sign-In
     * @return AuthResult with User data or error
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult<User>

    // ==================== APPLE SIGN-IN (NEW) ====================

    /**
     * Sign in with Apple (OAuth)
     * Uses Apple's ID token and nonce for authentication
     *
     * @param idToken Apple ID token from Apple Sign-In
     * @param rawNonce Raw nonce used for verification
     * @return AuthResult with User data or error
     */
    suspend fun signInWithApple(
        idToken: String,
        rawNonce: String
    ): AuthResult<User>

    /**
     * Start Apple Sign-In flow (Alternative method)
     * Handles the entire Apple sign-in process automatically
     *
     * @param activity Current activity context
     * @return AuthResult with User data or error
     */
    suspend fun startAppleSignIn(activity: Activity): AuthResult<User>

    // ==================== END APPLE SIGN-IN ====================

    /**
     * Sign out current user
     * Clears Firebase Auth session
     *
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend fun signOut(): AuthResult<Unit>

    /**
     * Get currently authenticated user
     * Returns null if no user is signed in
     *
     * @return AuthResult with User data or null
     */
    suspend fun getCurrentUser(): AuthResult<User?>

    /**
     * Observe authentication state changes
     * Emits whenever user signs in/out
     *
     * @return Flow of FirebaseUser (null when signed out)
     */
    fun observeAuthState(): Flow<FirebaseUser?>

    /**
     * Reset password via email
     * Sends password reset link to user's email
     *
     * @param email User's email address
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend fun resetPassword(email: String): AuthResult<Unit>

    /**
     * Update user profile (name, phone, etc.)
     * Updates both Firebase Auth and Firestore
     *
     * @param displayName New display name (optional)
     * @param phone New phone number (optional)
     * @return AuthResult with updated User data
     */
    suspend fun updateProfile(
        displayName: String? = null,
        phone: String? = null
    ): AuthResult<User>

    /**
     * Delete user account permanently
     * Removes Firebase Auth account and Firestore data
     *
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend fun deleteAccount(): AuthResult<Unit>
}
