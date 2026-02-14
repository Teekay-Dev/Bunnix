package com.example.bunnix.domain.repository


import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Chat management.
 * Handles chat creation, messaging, and real-time updates.
 */
interface ChatRepository {

    /**
     * Get or create a chat between two users
     * If chat exists, returns existing chat
     * If not, creates new chat
     *
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @param user1Name First user name
     * @param user2Name Second user name
     * @param user1IsVendor Is first user a vendor
     * @param user2IsVendor Is second user a vendor
     * @param relatedOrderId Optional related order ID
     * @param relatedBookingId Optional related booking ID
     * @return AuthResult with Chat
     */
    suspend fun getOrCreateChat(
        userId1: String,
        userId2: String,
        user1Name: String,
        user2Name: String,
        user1IsVendor: Boolean,
        user2IsVendor: Boolean,
        relatedOrderId: String = "",
        relatedBookingId: String = ""
    ): AuthResult<Chat>

    /**
     * Send a text message
     *
     * @param chatId Chat ID
     * @param senderId Sender's user ID
     * @param senderName Sender's name
     * @param text Message text
     * @return AuthResult with Message
     */
    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String
    ): AuthResult<Message>

    /**
     * Send an image message
     *
     * @param chatId Chat ID
     * @param senderId Sender's user ID
     * @param senderName Sender's name
     * @param imageUri Local image URI
     * @return AuthResult with Message
     */
    suspend fun sendImageMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        imageUri: String
    ): AuthResult<Message>

    /**
     * Send an order/booking link message
     *
     * @param chatId Chat ID
     * @param senderId Sender's user ID
     * @param senderName Sender's name
     * @param orderPreview Order/booking preview data
     * @return AuthResult with Message
     */
    suspend fun sendOrderLinkMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        orderPreview: Map<String, Any>
    ): AuthResult<Message>

    /**
     * Mark messages as read
     *
     * @param chatId Chat ID
     * @param userId User ID who is reading
     * @return AuthResult<Unit>
     */
    suspend fun markMessagesAsRead(
        chatId: String,
        userId: String
    ): AuthResult<Unit>

    /**
     * Get all chats for a user
     *
     * @param userId User ID
     * @return AuthResult with list of Chats
     */
    suspend fun getUserChats(userId: String): AuthResult<List<Chat>>

    /**
     * Get messages for a chat
     *
     * @param chatId Chat ID
     * @param limit Maximum messages to fetch
     * @return AuthResult with list of Messages
     */
    suspend fun getChatMessages(
        chatId: String,
        limit: Int = 50
    ): AuthResult<List<Message>>

    /**
     * Observe user's chats in real-time
     *
     * @param userId User ID
     * @return Flow of Chat list
     */
    fun observeUserChats(userId: String): Flow<List<Chat>>

    /**
     * Observe messages in a chat in real-time
     *
     * @param chatId Chat ID
     * @return Flow of Message list
     */
    fun observeChatMessages(chatId: String): Flow<List<Message>>

    /**
     * Delete a message
     *
     * @param chatId Chat ID
     * @param messageId Message ID
     * @param userId User ID (must be sender)
     * @return AuthResult<Unit>
     */
    suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        userId: String
    ): AuthResult<Unit>
}
