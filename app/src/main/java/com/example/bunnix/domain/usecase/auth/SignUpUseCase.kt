package com.example.bunnix.domain.usecase.auth

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user sign-up.
 * Handles both Customer and Business signup flows.
 *
 * This is what ViewModels should call, NOT the repository directly.
 */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute sign-up operation
     *
     * @param email User's email
     * @param password User's password
     * @param displayName User's full name
     * @param phone Phone number
     * @param isBusinessAccount Whether this is a business/vendor account
     * @param businessName Business name (required if isBusinessAccount = true)
     * @param businessAddress Business address (required if isBusinessAccount = true)
     * @return AuthResult with User data or error
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        phone: String = "",
        isBusinessAccount: Boolean = false,
        businessName: String = "",
        businessAddress: String = ""
    ): AuthResult<User> {
        // Validate business-specific fields
        if (isBusinessAccount) {
            if (businessName.isBlank()) {
                return AuthResult.Error("Business name is required")
            }
            if (businessAddress.isBlank()) {
                return AuthResult.Error("Business address is required")
            }
        }

        return authRepository.signUpWithEmail(
            email = email.trim().lowercase(),
            password = password,
            displayName = displayName.trim(),
            phone = phone.trim(),
            isBusinessAccount = isBusinessAccount,
            businessName = businessName.trim(),
            businessAddress = businessAddress.trim()
        )
    }
}
