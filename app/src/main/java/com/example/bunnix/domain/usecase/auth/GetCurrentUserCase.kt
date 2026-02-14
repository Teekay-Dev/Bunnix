package com.example.bunnix.domain.usecase.auth


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for getting current authenticated user.
 * Returns null if no user is signed in.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Get currently signed-in user
     *
     * @return AuthResult with User data or null if not signed in
     */
    suspend operator fun invoke(): AuthResult<User?> {
        return authRepository.getCurrentUser()
    }
}
