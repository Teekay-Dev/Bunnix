package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Notification
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object NotificationCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.NOTIFICATIONS)

    // GET USER NOTIFICATIONS
    fun getUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    // CREATE NOTIFICATION
    suspend fun createNotification(notification: Notification): Result<String> {
        return try {
            val docRef = collection.add(notification).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK AS READ
    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            val updates = mapOf("isRead" to true)
            collection.document(notificationId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK ALL AS READ
    suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            val unreadNotifications = collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = FirebaseConfig.firestore.batch()
            unreadNotifications.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}