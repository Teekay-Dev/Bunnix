package com.example.bunnix.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.bunnix.model.Vendor

@Suppress("unused")
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "bunnix_prefs"
        private const val KEY_TOKEN = "auth_token"

        // Vendor Specific Keys

        private const val KEY_VENDOR_ID = "vendor_id"
        private const val KEY_VENDOR_NAME = "vendor_name"
        private const val KEY_VENDOR_EMAIL = "vendor_email"
        private const val KEY_VENDOR_PHONE = "vendor_phone"
        private const val KEY_VENDOR_IMAGE = "vendor_image"

        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_VENDOR_MODE = "is_vendor_mode"
        private const val KEY_IS_FIRST_TIME = "is_first_time"
    }


    // --- Auth Token Logic ---
    fun saveAuthToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getAuthHeader(): String? {
        val token = getAuthToken()
        return if (token != null) "Bearer $token" else null
    }

    // --- Vendor Profile Logic --
    fun saveVendor(vendor: Vendor) {
        prefs.edit {
            putInt(KEY_VENDOR_ID, vendor.id) // Using putInt for the Int ID
            putString(KEY_VENDOR_NAME, vendor.businessName)
            putString("vendor_firstname", vendor.firstName)
            putString("vendor_surname", vendor.surName)
            putString(KEY_VENDOR_EMAIL, vendor.email)
            putString(KEY_VENDOR_PHONE, vendor.phone)
            putString(KEY_VENDOR_IMAGE, vendor.profileImage)
            putString("vendor_role", vendor.role)
            putString("vendor_created_at", vendor.createdAt)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_VENDOR_MODE, true)
        }
    }

    fun getVendor(): Vendor? {
        // Check if a valid ID exists (defaulting to -1 if not found)
        val vendorId = prefs.getInt(KEY_VENDOR_ID, -1)
        if (vendorId == -1) return null

        return Vendor(
            id = vendorId, // Now correctly passed as an Int
            firstName = prefs.getString("vendor_firstname", "") ?: "",
            surName = prefs.getString("vendor_surname", "") ?: "",
            businessName = prefs.getString(KEY_VENDOR_NAME, "") ?: "",
            email = prefs.getString(KEY_VENDOR_EMAIL, "") ?: "",
            phone = prefs.getString(KEY_VENDOR_PHONE, "") ?: "",
            profileImage = prefs.getString(KEY_VENDOR_IMAGE, null) ?: "",
            role = prefs.getString("vendor_role", "vendor") ?: "vendor",
            createdAt = prefs.getString("vendor_created_at", "") ?: ""
        )
    }

    // --- Helper Getters ---
    fun getVendorId(): String? = prefs.getString(KEY_VENDOR_ID, null)
    fun getVendorName(): String? = prefs.getString(KEY_VENDOR_NAME, null)

    // --- Session & State Management ---
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
    }

    fun isVendorMode(): Boolean = prefs.getBoolean(KEY_IS_VENDOR_MODE, true) //

    fun setVendorMode(isVendor: Boolean) {
        prefs.edit { putBoolean(KEY_IS_VENDOR_MODE, isVendor) } //
    }

    fun isFirstTime(): Boolean = prefs.getBoolean(KEY_IS_FIRST_TIME, true)

    fun setFirstTime(isFirstTime: Boolean) {
        prefs.edit { putBoolean(KEY_IS_FIRST_TIME, isFirstTime) }
    }

    // --- Cleanup ---
    fun clearSession() {
        prefs.edit { clear() }
    }

    fun clearAuthData() {
        prefs.edit {
            remove(KEY_TOKEN)
            remove(KEY_VENDOR_ID)
            remove(KEY_VENDOR_NAME)
            remove(KEY_VENDOR_EMAIL)
            remove(KEY_VENDOR_PHONE)
            remove(KEY_VENDOR_IMAGE)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_IS_VENDOR_MODE)
        }
    }
}