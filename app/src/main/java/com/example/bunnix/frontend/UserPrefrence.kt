package com.example.bunnix.frontend

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ✅ DataStore Setup
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {

        val IS_LOGGED_IN = booleanPreferencesKey("logged_in")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")

        val CUSTOMER_CREATED = booleanPreferencesKey("customer_created")
        val VENDOR_CREATED = booleanPreferencesKey("vendor_created")

        val CURRENT_MODE = stringPreferencesKey("current_mode")
    }

    // ---------------------------
    // ✅ READ VALUES (Flows)
    // ---------------------------

    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }

    val isFirstLaunch: Flow<Boolean> =
        context.dataStore.data.map { it[FIRST_LAUNCH] ?: true }

    val currentMode: Flow<String> =
        context.dataStore.data.map { it[CURRENT_MODE] ?: "CUSTOMER" }

    val vendorCreated: Flow<Boolean> =
        context.dataStore.data.map { it[VENDOR_CREATED] ?: false }

    val customerCreated: Flow<Boolean> =
        context.dataStore.data.map { it[CUSTOMER_CREATED] ?: false }

    // ---------------------------
    // ✅ SAVE VALUES
    // ---------------------------

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = value
        }
    }

    suspend fun setFirstLaunch(value: Boolean) {
        context.dataStore.edit {
            it[FIRST_LAUNCH] = value
        }
    }

    suspend fun setCustomerCreated(value: Boolean) {
        context.dataStore.edit {
            it[CUSTOMER_CREATED] = value
        }
    }

    suspend fun setVendorCreated(value: Boolean) {
        context.dataStore.edit {
            it[VENDOR_CREATED] = value
        }
    }

    suspend fun setMode(mode: String) {
        context.dataStore.edit {
            it[CURRENT_MODE] = mode
        }
    }

    // ---------------------------
    // ✅ FUNCTIONS MAINACTIVITY NEEDS
    // ---------------------------

    // ✅ getMode()
    fun getMode(): Flow<String> {
        return currentMode
    }

    // ✅ hasVendorAccount()
    fun hasVendorAccount(): Flow<Boolean> {
        return vendorCreated
    }

    // ✅ switchMode()
    suspend fun switchMode() {
        context.dataStore.edit { prefs ->

            val current = prefs[CURRENT_MODE] ?: "CUSTOMER"

            prefs[CURRENT_MODE] =
                if (current == "CUSTOMER") "VENDOR"
                else "CUSTOMER"
        }
    }

    // ---------------------------
    // ✅ LOGOUT
    // ---------------------------

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }
}
