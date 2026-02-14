package com.example.bunnix.domain.manager


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bunnix.domain.model.UserMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for persisting and observing user mode state.
 * Uses DataStore to save mode preference locally.
 *
 * This is LOCAL STATE ONLY - it doesn't change Firestore.
 * It just remembers which mode the user is currently using.
 */

private val Context.userModeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_mode_preferences"
)

@Singleton
class UserModeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_MODE_KEY = stringPreferencesKey("user_mode")

    /**
     * Observe current user mode
     */
    val userModeFlow: Flow<UserMode> = context.userModeDataStore.data
        .map { preferences ->
            val modeString = preferences[USER_MODE_KEY] ?: UserMode.CUSTOMER.name
            try {
                UserMode.valueOf(modeString)
            } catch (e: IllegalArgumentException) {
                UserMode.CUSTOMER
            }
        }

    /**
     * Set user mode (switches between Customer and Vendor)
     *
     * @param mode The mode to switch to
     */
    suspend fun setUserMode(mode: UserMode) {
        context.userModeDataStore.edit { preferences ->
            preferences[USER_MODE_KEY] = mode.name
        }
    }

    /**
     * Toggle between Customer and Vendor modes
     *
     * @return The new mode after toggle
     */
    suspend fun toggleUserMode(): UserMode {
        val currentMode = getCurrentMode()
        val newMode = when (currentMode) {
            UserMode.CUSTOMER -> UserMode.VENDOR
            UserMode.VENDOR -> UserMode.CUSTOMER
        }
        setUserMode(newMode)
        return newMode
    }

    /**
     * Get current mode synchronously
     * Note: Prefer using userModeFlow for reactive updates
     */
    private suspend fun getCurrentMode(): UserMode {
        var currentMode = UserMode.CUSTOMER
        context.userModeDataStore.data.collect { preferences ->
            val modeString = preferences[USER_MODE_KEY] ?: UserMode.CUSTOMER.name
            currentMode = try {
                UserMode.valueOf(modeString)
            } catch (e: IllegalArgumentException) {
                UserMode.CUSTOMER
            }
        }
        return currentMode
    }

    /**
     * Reset to customer mode (e.g., on sign out)
     */
    suspend fun resetToCustomerMode() {
        setUserMode(UserMode.CUSTOMER)
    }
}
