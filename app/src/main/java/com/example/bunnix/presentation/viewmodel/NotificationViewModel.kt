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
                _notifications.value = notificationList
                _unreadCount.value = notificationList.count { !it.isRead }

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
                    _notifications.value = notificationList
                    _unreadCount.value = notificationList.count { !it.isRead }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load notifications"
                _isLoading.value = false
            }
        }
    }

    private fun observeUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // CHANGE: don't close the flow, just set error state and return empty
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
        viewModelScope.launch {
            try {
                firestore.collection("notifications")
                    .whereEqualTo("notificationId", notificationId)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.reference
                    ?.update("isRead", true)
                    ?.await()

                // Update local state
                _notifications.value = _notifications.value.map {
                    if (it.notificationId == notificationId) it.copy(isRead = true) else it
                }
                _unreadCount.value = _notifications.value.count { !it.isRead }

            } catch (e: Exception) {
                _error.value = "Failed to mark as read"
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
                _unreadCount.value = 0

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
                firestore.collection("notifications")
                    .whereEqualTo("notificationId", notificationId)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.reference
                    ?.delete()
                    ?.await()

                // Update local state
                _notifications.value = _notifications.value.filter {
                    it.notificationId != notificationId
                }
                _unreadCount.value = _notifications.value.count { !it.isRead }

            } catch (e: Exception) {
                _error.value = "Failed to delete notification"
            }
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
}