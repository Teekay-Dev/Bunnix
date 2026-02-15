//package com.example.bunnix.domain.usecase.auth
//
//import android.app.Activity
//import com.example.bunnix.data.auth.AuthResult
//import com.example.bunnix.database.models.User
//import com.example.bunnix.domain.repository.AuthRepository
//import javax.inject.Inject
//
///**
// * Use case for Sign in with Apple.
// * Follows the same pattern as other auth use cases.
// *
// * Handles Apple OAuth authentication with two methods:
// * 1. Manual: Using ID token and nonce (for custom implementations)
// * 2. Automatic: Handles entire flow automatically (recommended)
// */
//class SignInWithAppleUseCase @Inject constructor(
//    private val authRepository: AuthRepository
//) {
//    /**
//     * Sign in with Apple using ID token and nonce
//     * Use this when you have a custom Apple Sign-In implementation
//     * that provides the ID token and nonce
//     *
//     * @param idToken Apple ID token from Apple Sign-In response
//     * @param rawNonce Raw nonce used for verification
//     * @return AuthResult with User data or error
//     */
//    suspend operator fun invoke(
//        idToken: String,
//        rawNonce: String
//    ): AuthResult<User> {
//        // Validation
//        if (idToken.isBlank()) {
//            return AuthResult.Error("Invalid Apple ID token")
//        }
//
//        if (rawNonce.isBlank()) {
//            return AuthResult.Error("Invalid nonce")
//        }
//
//        return authRepository.signInWithApple(idToken, rawNonce)
//    }
//
//    /**
//     * Start Apple Sign-In flow automatically
//     * Use this for the simplest implementation - Firebase handles everything
//     *
//     * @param activity Current activity context
//     * @return AuthResult with User data or error
//     */
//    suspend fun startFlow(activity: Activity): AuthResult<User> {
//        return authRepository.startAppleSignIn(activity)
//    }
//}
