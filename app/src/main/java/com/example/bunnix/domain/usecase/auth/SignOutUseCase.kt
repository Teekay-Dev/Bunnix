package com.example.bunnix.domain.usecase.auth


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user sign-out.
 * Clears authentication session.
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute sign-out operation
     *
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend operator fun invoke(): AuthResult<Unit> {
        return authRepository.signOut()
    }
}
