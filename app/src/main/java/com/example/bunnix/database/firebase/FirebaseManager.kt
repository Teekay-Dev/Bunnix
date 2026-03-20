package com.example.bunnix.database.firebase

import com.example.bunnix.database.config.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth

object FirebaseManager {

    val auth: FirebaseAuth = FirebaseConfig.auth

    /**
     * GET CURRENT USER ID
     * Used everywhere to fetch user-specific data (Chats, Orders, etc.)
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * IS USER LOGGED IN
     * Used to decide whether to show Login screen or Home screen
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * LOGOUT USER
     */
    fun logoutUser() {
        auth.signOut()
    }
}