package com.example.bunnix.backend

import com.example.bunnix.frontend.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CustomerProfileRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {

    fun getProfile(): Flow<UserProfile> =
        dataStoreManager.getUserProfile()

    suspend fun updateProfile(profile: UserProfile) {
        dataStoreManager.saveUserProfile(profile)
    }
}
