package com.example.bunnix.backend

import com.example.bunnix.frontend.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor() {

    private val searchHistory =
        MutableStateFlow<List<String>>(emptyList())

    private val userProfile =
        MutableStateFlow(UserProfile())

    fun getSearchHistory(): Flow<List<String>> = searchHistory

    suspend fun saveSearchHistory(history: List<String>) {
        searchHistory.value = history
    }

    fun getUserProfile(): Flow<UserProfile> = userProfile

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfile.value = profile
    }
}
