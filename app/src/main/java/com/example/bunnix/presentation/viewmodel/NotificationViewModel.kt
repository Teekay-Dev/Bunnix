package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // ===== STATE FLOWS =====

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _transactionalUnreadCount = MutableStateFlow(0)
    val transactionalUnreadCount: StateFlow<Int> = _transactionalUnreadCount.asStateFlow()

    // ===== FUNCTIONS =====

    /**
     * Load notifications for a user
     */
    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val snapshot = firestore.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val notificationList = snapshot.toObjects(Notification::class.java)
                updateState(notificationList)

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load notifications"
                _notifications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Observe notifications in real-time
     */
    fun observeNotifications(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            try {
                observeUserNotifications(userId).collect { notificationList ->
                    updateState(notificationList)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load notifications"
                _isLoading.value = false
            }
        }
    }

    private fun updateState(list: List<Notification>) {
        _notifications.value = list
        _unreadCount.value = list.count { !it.isRead }
        
        // Transactional ones for bottom nav badge
        _transactionalUnreadCount.value = list.count { 
            !it.isRead && it.type in listOf("ORDER", "BOOKING", "PAYMENT", "MESSAGE") 
        }
    }

    private fun observeUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = error.message
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) {
        // 🌟 FIX: Use .update to guarantee that Compose catches the state change instantly!
        _notifications.update { currentList ->
            currentList.map { notification ->
                if (notification.notificationId == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }

        // Explicitly recalculate counts for the bottom navigation bar immediately
        updateState(_notifications.value)

        // Now securely sync the change to the Firebase Firestore Database in the background
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("notifications")
                    .whereEqualTo("notificationId", notificationId)
                    .get()
                    .await()

                for (document in snapshot.documents) {
                    document.reference.update("isRead", true).await()
                }
            } catch (e: Exception) {
                _error.value = "Failed to sync read status to server"

                // Optional: Rollback if the database update fails completely
                // loadNotifications(userId)
            }
        }
    }

    /**
     * Mark all notifications as read
     */
    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                val batch = firestore.batch()

                val snapshot = firestore.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("isRead", false)
                    .get()
                    .await()

                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "isRead", true)
                }

                batch.commit().await()

                // Update local state
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }
                updateState(_notifications.value)

            } catch (e: Exception) {
                _error.value = "Failed to mark all as read"
            }
        }
    }

    /**
     * Delete a notification
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                // Option A: If your Firestore Document ID is the notificationId:
                firestore.collection("notifications")
                    .document(notificationId)
                    .delete()
                    .await()

                // Option B: If it's a custom field, make sure it matches exactly:
                val snapshot = firestore.collection("notifications")
                    .whereEqualTo("notificationId", notificationId)
                    .get()
                    .await()

                for (document in snapshot.documents) {
                    document.reference.delete().await()
                }

                // Update local state fallback
                _notifications.value = _notifications.value.filter {
                    it.notificationId != notificationId
                }
                updateState(_notifications.value)

            } catch (e: Exception) {
                _error.value = "Failed to delete notification from server"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
