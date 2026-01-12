package com.example.bunnix.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

@Suppress("unused")
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "bunnix_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_IMAGE = "user_image"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_VENDOR = "is_vendor"
        private const val KEY_IS_FIRST_TIME = "is_first_time"
    }

    fun saveAuthToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getAuthHeader(): String? {
        val token = getAuthToken()
        return if (token != null) "Bearer $token" else null
    }

    fun saveUser(user: com.example.bunnix.data.model.User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_IMAGE, user.profileImage)
            putBoolean(KEY_IS_VENDOR, user.isVendor)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): com.example.bunnix.data.model.User? {
        val userId = getUserId() ?: return null
        return com.example.bunnix.data.model.User(
            id = userId,
            name = getUserName() ?: "",
            email = getUserEmail() ?: "",
            phone = getUserPhone() ?: "",
            profileImage = getUserImage(),
            isVendor = isVendor()
        )
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserPhone(): String? = prefs.getString(KEY_USER_PHONE, null)
    fun getUserImage(): String? = prefs.getString(KEY_USER_IMAGE, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
    }

    fun isVendor(): Boolean = prefs.getBoolean(KEY_IS_VENDOR, false)

    fun setVendorMode(isVendor: Boolean) {
        prefs.edit { putBoolean(KEY_IS_VENDOR, isVendor) }
    }

    fun isFirstTime(): Boolean = prefs.getBoolean(KEY_IS_FIRST_TIME, true)

    fun setFirstTime(isFirstTime: Boolean) {
        prefs.edit { putBoolean(KEY_IS_FIRST_TIME, isFirstTime) }
    }

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun clearAuthData() {
        prefs.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_PHONE)
            remove(KEY_USER_IMAGE)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_IS_VENDOR)
            apply()
        }
    }
}