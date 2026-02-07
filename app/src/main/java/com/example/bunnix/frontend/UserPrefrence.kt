package com.example.bunnix.frontend


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore extension
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val FIRST_LAUNCH = booleanPreferencesKey("isFirstLaunch")
    private val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
    private val USER_ROLE = stringPreferencesKey("userRole")

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { it[FIRST_LAUNCH] ?: true }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { it[IS_LOGGED_IN] ?: false }

    val userRole: Flow<String> = context.dataStore.data
        .map { it[USER_ROLE] ?: "customer" }

    fun setFirstLaunch(isFirst: Boolean) {
        sharedPreferences.edit().putBoolean("is_first_launch", isFirst).apply()
    }

    fun setLoggedIn(loggedIn: Boolean, role: String) {
        sharedPreferences.edit().apply {
            putBoolean("is_logged_in", loggedIn)
            putString("user_role", role)
            apply()
        }
    }

    fun setVendorMode(isVendor: Boolean) {
        sharedPreferences.edit().putBoolean("is_vendor_mode", isVendor).apply()
    }
}
