package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object UserCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.USERS)

    // CREATE USER
    suspend fun createUser(user: User): Result<String> {
        return try {
            val userId = FirebaseConfig.auth.currentUser?.uid ?: throw Exception("Not logged in")
            collection.document(userId).set(user).await()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET USER
    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val snapshot = collection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET USER FLOW
    fun getUserFlow(userId: String): Flow<User?> = callbackFlow {
        val listener = collection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    // UPDATE USER
    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE PROFILE PICTURE
    suspend fun updateProfilePicture(userId: String, imageUrl: String): Result<Unit> {
        return try {
            val updates = mapOf("profilePicUrl" to imageUrl)
            collection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}