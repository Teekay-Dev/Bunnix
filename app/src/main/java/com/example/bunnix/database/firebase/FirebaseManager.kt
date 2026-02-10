package com.example.bunnix.database.firebase

import com.example.bunnix.database.config.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    val auth: FirebaseAuth = FirebaseConfig.auth
    val firestore: FirebaseFirestore = FirebaseConfig.firestore

    // AUTH FUNCTIONS

    /**
     * REGISTER USER
     */
    suspend fun registerUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * LOGIN USER
     */
    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * LOGOUT USER
     */
    fun logoutUser() {
        auth.signOut()
    }

    /**
     * GET CURRENT USER ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * IS USER LOGGED IN
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * RESET PASSWORD
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE EMAIL
     */
    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            auth.currentUser?.updateEmail(newEmail)?.await()
                ?: throw Exception("User not logged in")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * UPDATE PASSWORD
     */
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(newPassword)?.await()
                ?: throw Exception("User not logged in")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * DELETE ACCOUNT
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not logged in")

            // Delete user data from Firestore (you should do this first)
            // Example: deleteUserData(userId)

            // Delete Firebase Auth account
            auth.currentUser?.delete()?.await()
                ?: throw Exception("User not logged in")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}