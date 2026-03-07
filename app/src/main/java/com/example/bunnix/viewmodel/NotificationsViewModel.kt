package com.example.bunnix.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.profile.NotificationItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                val notificationsSnapshot = firestore.collection("notifications")
                    .whereEqualTo("vendorId", vendorId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                val notifications = notificationsSnapshot.documents.mapNotNull { doc ->
                    try {
                        val type = doc.getString("type") ?: "general"
                        val title = doc.getString("title") ?: "Notification"
                        val message = doc.getString("message") ?: ""
                        val isRead = doc.getBoolean("isRead") ?: false
                        val timestamp = doc.getTimestamp("timestamp")

                        val timeAgo = timestamp?.let { calculateTimeAgo(it.toDate()) } ?: "Just now"

                        val (icon, iconTint, iconBg) = getNotificationIcon(type)

                        NotificationItem(
                            id = doc.id,
                            type = type,
                            title = title,
                            message = message,
                            timeAgo = timeAgo,
                            isRead = isRead,
                            icon = icon,
                            iconTint = iconTint,
                            iconBackgroundColor = iconBg
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                _notifications.value = notifications

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load notifications"
                _notifications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("notifications")
                    .document(notificationId)
                    .update("isRead", true)
                    .await()

                // Update local state
                _notifications.value = _notifications.value.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
            } catch (e: Exception) {
                // Ignore error
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val vendorId = auth.currentUser?.uid ?: return@launch

                val batch = firestore.batch()

                val unreadDocs = firestore.collection("notifications")
                    .whereEqualTo("vendorId", vendorId)
                    .whereEqualTo("isRead", false)
                    .get()
                    .await()

                unreadDocs.documents.forEach { doc ->
                    batch.update(doc.reference, "isRead", true)
                }

                batch.commit().await()

                // Update local state
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }

            } catch (e: Exception) {
                _error.value = "Failed to mark all as read"
            }
        }
    }

    private fun getNotificationIcon(type: String): Triple<ImageVector, Color, Color> {
        return when (type.lowercase()) {
            "order" -> Triple(
                Icons.Default.ShoppingBag,
                Color(0xFF2196F3),
                Color(0xFFE3F2FD)
            )
            "message" -> Triple(
                Icons.Default.Message,
                Color(0xFFFF6B35),
                Color(0xFFFFF3E0)
            )
            "payment" -> Triple(
                Icons.Default.Payment,
                Color(0xFF4CAF50),
                Color(0xFFE8F5E9)
            )
            "booking" -> Triple(
                Icons.Default.CalendarToday,
                Color(0xFF9C27B0),
                Color(0xFFF3E5F5)
            )
            else -> Triple(
                Icons.Default.Notifications,
                Color.Gray,
                Color(0xFFF5F5F5)
            )
        }
    }

    private fun calculateTimeAgo(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time

        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 7 -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            else -> "Just now"
        }
    }

    fun refresh() {
        loadNotifications()
    }
}