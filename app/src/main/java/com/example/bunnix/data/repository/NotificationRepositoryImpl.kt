package com.example.bunnix.data.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Notification
import com.example.bunnix.domain.repository.NotificationRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * VERIFIED CORRECT - Matches database guide
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    companion object {
        private const val NOTIFICATIONS_COLLECTION = "notifications"
        private const val NOTIFICATION_EXPIRY_DAYS = 30L
    }

    override suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        relatedId: String,
        relatedType: String,
        actionUrl: String,
        imageUrl: String
    ): AuthResult<Notification> {
        return try {
            val notificationRef = firestore.collection(NOTIFICATIONS_COLLECTION).document()
            val notificationId = notificationRef.id

            val expiresAt = Timestamp(
                System.currentTimeMillis() / 1000 + TimeUnit.DAYS.toSeconds(NOTIFICATION_EXPIRY_DAYS),
                0
            )

            val notification = Notification(
                notificationId = notificationId,
                userId = userId,
                type = type,
                title = title,
                message = message,
                relatedId = relatedId,
                relatedType = relatedType,
                actionUrl = actionUrl,
                imageUrl = imageUrl,
                isRead = false,
                createdAt = Timestamp.now(),
                expiresAt = expiresAt
            )

            notificationRef.set(notification).await()

            AuthResult.Success(notification)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to create notification",
                exception = e
            )
        }
    }

    override suspend fun getUserNotifications(
        userId: String,
        limit: Int
    ): AuthResult<List<Notification>> {
        return try {
            val snapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val notifications = snapshot.toObjects(Notification::class.java)
            AuthResult.Success(notifications)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get notifications",
                exception = e
            )
        }
    }

    override suspend fun getUnreadCount(userId: String): AuthResult<Int> {
        return try {
            val snapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            AuthResult.Success(snapshot.size())

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get unread count",
                exception = e
            )
        }
    }

    override suspend fun markAsRead(notificationId: String): AuthResult<Unit> {
        return try {
            firestore.collection(NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .update("isRead", true)
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to mark as read",
                exception = e
            )
        }
    }

    override suspend fun markAllAsRead(userId: String): AuthResult<Unit> {
        return try {
            val unreadNotifications = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            unreadNotifications.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to mark all as read",
                exception = e
            )
        }
    }

    override suspend fun deleteNotification(
        notificationId: String,
        userId: String
    ): AuthResult<Unit> {
        return try {
            val notificationSnapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .get()
                .await()

            val notification = notificationSnapshot.toObject(Notification::class.java)
                ?: throw Exception("Notification not found")

            if (notification.userId != userId) {
                throw Exception("Unauthorized")
            }

            notificationSnapshot.reference.delete().await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to delete notification",
                exception = e
            )
        }
    }

    override suspend fun deleteAllNotifications(userId: String): AuthResult<Unit> {
        return try {
            val notifications = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = firestore.batch()
            notifications.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to delete all notifications",
                exception = e
            )
        }
    }

    override fun observeUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
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

    override fun observeUnreadCount(userId: String): Flow<Int> = callbackFlow {
        val listener = firestore.collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun notifyPaymentVerified(
        customerId: String,
        orderId: String,
        orderNumber: String
    ): AuthResult<Unit> {
        return createNotification(
            userId = customerId,
            type = "payment_verified",
            title = "Payment Confirmed! 🎉",
            message = "Your payment for order $orderNumber has been verified by the vendor.",
            relatedId = orderId,
            relatedType = "order",
            actionUrl = "bunnix://order/$orderId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }

    override suspend fun notifyOrderStatusChanged(
        customerId: String,
        orderId: String,
        orderNumber: String,
        newStatus: String
    ): AuthResult<Unit> {
        return createNotification(
            userId = customerId,
            type = "order_status_update",
            title = "Order Update 📦",
            message = "Your order $orderNumber is now: $newStatus",
            relatedId = orderId,
            relatedType = "order",
            actionUrl = "bunnix://order/$orderId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }

    override suspend fun notifyVendorNewOrder(
        vendorId: String,
        orderId: String,
        orderNumber: String,
        customerName: String
    ): AuthResult<Unit> {
        return createNotification(
            userId = vendorId,
            type = "new_order",
            title = "New Order! 🛒",
            message = "$customerName placed order $orderNumber. Awaiting payment verification.",
            relatedId = orderId,
            relatedType = "order",
            actionUrl = "bunnix://vendor/orders/$orderId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }

    override suspend fun notifyBookingResponse(
        customerId: String,
        bookingId: String,
        bookingNumber: String,
        isAccepted: Boolean
    ): AuthResult<Unit> {
        return createNotification(
            userId = customerId,
            type = if (isAccepted) "booking_accepted" else "booking_declined",
            title = if (isAccepted) "Booking Accepted! ✅" else "Booking Declined ❌",
            message = if (isAccepted)
                "Your booking $bookingNumber has been accepted by the vendor."
            else
                "Your booking $bookingNumber was declined by the vendor.",
            relatedId = bookingId,
            relatedType = "booking",
            actionUrl = "bunnix://booking/$bookingId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }

    override suspend fun notifyVendorNewBooking(
        vendorId: String,
        bookingId: String,
        bookingNumber: String,
        customerName: String
    ): AuthResult<Unit> {
        return createNotification(
            userId = vendorId,
            type = "new_booking",
            title = "New Booking Request! 📅",
            message = "$customerName requested booking $bookingNumber. Review and accept/decline.",
            relatedId = bookingId,
            relatedType = "booking",
            actionUrl = "bunnix://vendor/bookings/$bookingId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }

    override suspend fun notifyNewMessage(
        userId: String,
        senderName: String,
        messagePreview: String,
        chatId: String
    ): AuthResult<Unit> {
        return createNotification(
            userId = userId,
            type = "new_message",
            title = "New Message from $senderName 💬",
            message = messagePreview.take(50) + if (messagePreview.length > 50) "..." else "",
            relatedId = chatId,
            relatedType = "chat",
            actionUrl = "bunnix://chat/$chatId"
        ).let {
            when (it) {
                is AuthResult.Success -> AuthResult.Success(Unit)
                is AuthResult.Error -> AuthResult.Error(it.message, it.exception)
                else -> AuthResult.Error("Unknown error")
            }
        }
    }
}
