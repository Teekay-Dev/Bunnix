package com.example.bunnix.domain.user


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import javax.inject.Inject

/**
 * Use case for updating user profile.
 * Handles validation and business logic.
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        state: String? = null,
        profilePicUrl: String? = null
    ): AuthResult<User> {
        // Validate inputs
        name?.let {
            if (it.isBlank() || it.length < 2) {
                return AuthResult.Error("Name must be at least 2 characters")
            }
        }

        phone?.let {
            if (!it.startsWith("+")) {
                return AuthResult.Error("Phone must start with country code (e.g., +234)")
            }
        }

        return profileRepository.updateUserProfile(
            userId = userId,
            name = name?.trim(),
            phone = phone?.trim(),
            address = address?.trim(),
            city = city?.trim(),
            state = state?.trim(),
            profilePicUrl = profilePicUrl
        )
    }
}
