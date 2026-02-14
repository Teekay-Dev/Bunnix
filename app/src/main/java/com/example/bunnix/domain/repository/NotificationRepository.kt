package com.example.bunnix.domain.repository


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Notification management.
 * Handles in-app notifications and push notification triggers.
 */
interface NotificationRepository {

    /**
     * Create a notification
     *
     * @param userId User ID to receive notification
     * @param type Notification type (order_update, booking_update, message, etc.)
     * @param title Notification title
     * @param message Notification message
     * @param relatedId Related entity ID (order/booking/message)
     * @param relatedType Related entity type (order/booking/message)
     * @param actionUrl Optional deep link URL
     * @param imageUrl Optional image URL
     * @return AuthResult with created Notification
     */
    suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        relatedId: String = "",
        relatedType: String = "",
        actionUrl: String = "",
        imageUrl: String = ""
    ): AuthResult<Notification>

    /**
     * Get user's notifications
     *
     * @param userId User ID
     * @param limit Maximum notifications to fetch
     * @return AuthResult with list of Notifications
     */
    suspend fun getUserNotifications(
        userId: String,
        limit: Int = 50
    ): AuthResult<List<Notification>>

    /**
     * Get unread notifications count
     *
     * @param userId User ID
     * @return AuthResult with count
     */
    suspend fun getUnreadCount(userId: String): AuthResult<Int>

    /**
     * Mark notification as read
     *
     * @param notificationId Notification ID
     * @return AuthResult<Unit>
     */
    suspend fun markAsRead(notificationId: String): AuthResult<Unit>

    /**
     * Mark all notifications as read for user
     *
     * @param userId User ID
     * @return AuthResult<Unit>
     */
    suspend fun markAllAsRead(userId: String): AuthResult<Unit>

    /**
     * Delete a notification
     *
     * @param notificationId Notification ID
     * @param userId User ID (for authorization)
     * @return AuthResult<Unit>
     */
    suspend fun deleteNotification(
        notificationId: String,
        userId: String
    ): AuthResult<Unit>

    /**
     * Delete all notifications for user
     *
     * @param userId User ID
     * @return AuthResult<Unit>
     */
    suspend fun deleteAllNotifications(userId: String): AuthResult<Unit>

    /**
     * Observe user's notifications in real-time
     *
     * @param userId User ID
     * @return Flow of Notification list
     */
    fun observeUserNotifications(userId: String): Flow<List<Notification>>

    /**
     * Observe unread count in real-time
     *
     * @param userId User ID
     * @return Flow of unread count
     */
    fun observeUnreadCount(userId: String): Flow<Int>

    // ==================== NOTIFICATION TRIGGERS ====================
    // These methods create notifications for specific events

    /**
     * Notify customer when vendor verifies payment
     */
    suspend fun notifyPaymentVerified(
        customerId: String,
        orderId: String,
        orderNumber: String
    ): AuthResult<Unit>

    /**
     * Notify customer when order status changes
     */
    suspend fun notifyOrderStatusChanged(
        customerId: String,
        orderId: String,
        orderNumber: String,
        newStatus: String
    ): AuthResult<Unit>

    /**
     * Notify vendor when new order is placed
     */
    suspend fun notifyVendorNewOrder(
        vendorId: String,
        orderId: String,
        orderNumber: String,
        customerName: String
    ): AuthResult<Unit>

    /**
     * Notify customer when booking is accepted/declined
     */
    suspend fun notifyBookingResponse(
        customerId: String,
        bookingId: String,
        bookingNumber: String,
        isAccepted: Boolean
    ): AuthResult<Unit>

    /**
     * Notify vendor when new booking is requested
     */
    suspend fun notifyVendorNewBooking(
        vendorId: String,
        bookingId: String,
        bookingNumber: String,
        customerName: String
    ): AuthResult<Unit>

    /**
     * Notify user when they receive a new message
     */
    suspend fun notifyNewMessage(
        userId: String,
        senderName: String,
        messagePreview: String,
        chatId: String
    ): AuthResult<Unit>
}
