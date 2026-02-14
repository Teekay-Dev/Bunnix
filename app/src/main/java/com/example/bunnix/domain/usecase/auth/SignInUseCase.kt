package com.example.bunnix.domain.usecase.auth


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user sign-in.
 * Handles business logic before authentication.
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute sign-in operation
     *
     * @param email User's email
     * @param password User's password
     * @return AuthResult with User data or error
     */
    suspend operator fun invoke(
        email: String,
        password: String
    ): AuthResult<User> {
        // Trim and lowercase email for consistency
        return authRepository.signInWithEmail(
            email = email.trim().lowercase(),
            password = password
        )
    }
}
