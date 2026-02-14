package com.example.bunnix.domain.usecase.mode

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.domain.manager.UserModeManager
import com.example.bunnix.domain.model.UserMode
import com.example.bunnix.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for switching between Customer and Vendor modes.
 *
 * IMPORTANT: This only changes LOCAL mode preference.
 * It does NOT change the user's isVendor status in Firestore.
 *
 * The user must ALREADY be a vendor (isVendor = true) to switch to Vendor mode.
 */
class SwitchModeUseCase @Inject constructor(
    private val userModeManager: UserModeManager,
    private val authRepository: AuthRepository
) {
    /**
     * Switch to specified mode
     *
     * @param userId Current user ID
     * @param targetMode Mode to switch to
     * @return AuthResult<UserMode> with the new mode or error
     */
    suspend operator fun invoke(
        userId: String,
        targetMode: UserMode
    ): AuthResult<UserMode> {
        return try {
            // If switching to VENDOR mode, verify user is actually a vendor
            if (targetMode == UserMode.VENDOR) {
                val userResult = authRepository.getCurrentUser()

                when (userResult) {
                    is AuthResult.Success -> {
                        val user = userResult.data

                        if (user == null) {
                            return AuthResult.Error("User not found")
                        }

                        if (!user.isVendor) {
                            return AuthResult.Error(
                                "You need to become a vendor first. " +
                                        "Go to Settings → Become a Vendor."
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        return AuthResult.Error(
                            message = "Failed to verify vendor status: ${userResult.message}"
                        )
                    }
                    else -> {}
                }
            }

            // Switch mode
            userModeManager.setUserMode(targetMode)

            AuthResult.Success(targetMode)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to switch mode",
                exception = e
            )
        }
    }

    /**
     * Toggle between modes
     *
     * @param userId Current user ID
     * @return AuthResult<UserMode> with the new mode
     */
    suspend fun toggle(userId: String): AuthResult<UserMode> {
        return try {
            // Get current mode
            var currentMode = UserMode.CUSTOMER
            userModeManager.userModeFlow.collect {
                currentMode = it
            }

            // Determine target mode
            val targetMode = when (currentMode) {
                UserMode.CUSTOMER -> UserMode.VENDOR
                UserMode.VENDOR -> UserMode.CUSTOMER
            }

            // Use main invoke function to validate
            invoke(userId, targetMode)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to toggle mode",
                exception = e
            )
        }
    }
}