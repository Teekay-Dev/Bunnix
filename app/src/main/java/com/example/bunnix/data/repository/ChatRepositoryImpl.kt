package com.example.bunnix.data.repository

import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.Message
import com.example.bunnix.database.models.ParticipantInfo
import com.example.bunnix.domain.repository.ChatRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : ChatRepository {

    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_SUBCOLLECTION = "messages"
        private const val CHAT_IMAGES_BUCKET = "chat-images"
    }

    override suspend fun getOrCreateChat(
        userId1: String,
        userId2: String,
        user1Name: String,
        user2Name: String,
        user1IsVendor: Boolean,
        user2IsVendor: Boolean,
        relatedOrderId: String,
        relatedBookingId: String
    ): AuthResult<Chat> {
        return try {
            // Check if chat already exists
            val participants = listOf(userId1, userId2).sorted()

            val existingChat = firestore.collection(CHATS_COLLECTION)
                .whereArrayContains("participants", userId1)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Chat::class.java) }
                .firstOrNull { it.participants.contains(userId2) }

            if (existingChat != null) {
                return AuthResult.Success(existingChat)
            }

            // Create new chat
            val chatRef = firestore.collection(CHATS_COLLECTION).document()
            val chatId = chatRef.id

            val participantDetails = mapOf(
                userId1 to ParticipantInfo(
                    name = user1Name,
                    profilePic = "",
                    isVendor = user1IsVendor
                ),
                userId2 to ParticipantInfo(
                    name = user2Name,
                    profilePic = "",
                    isVendor = user2IsVendor
                )
            )

            val chat = Chat(
                chatId = chatId,
                participants = participants,
                participantDetails = participantDetails,
                lastMessage = "",
                lastMessageTime = Timestamp.now(),
                lastMessageSender = "",
                unreadCount = mapOf(userId1 to 0, userId2 to 0),
                relatedOrderId = relatedOrderId,
                relatedBookingId = relatedBookingId,
                createdAt = Timestamp.now()
            )

            chatRef.set(chat).await()

            AuthResult.Success(chat)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get/create chat",
                exception = e
            )
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String
    ): AuthResult<Message> {
        return try {
            val messageRef = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .document()

            val message = Message(
                messageId = messageRef.id,
                senderId = senderId,
                senderName = senderName,
                text = text,
                timestamp = Timestamp.now(),
            )

            messageRef.set(message).await()

            // Update chat's last message
            updateChatLastMessage(chatId, senderId, text)

            AuthResult.Success(message)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to send message",
                exception = e
            )
        }
    }

    override suspend fun sendImageMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        imageUri: String
    ): AuthResult<Message> {
        return try {
            // Upload image first
            val file = File(Uri.parse(imageUri).path ?: throw Exception("Invalid URI"))
            val fileName = "${chatId}_${System.currentTimeMillis()}.jpg"

            val bucket = supabase.storage.from(CHAT_IMAGES_BUCKET)
            bucket.upload(fileName, file.readBytes())
            val publicUrl = bucket.publicUrl(fileName)

            // Create message
            val messageRef = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .document()

            val message = Message(
                messageId = messageRef.id,
                senderId = senderId,
                senderName = senderName,
                imageUrl = publicUrl,
                messageType = "image",
                timestamp = Timestamp.now(),
            )

            messageRef.set(message).await()

            // Update chat's last message
            updateChatLastMessage(chatId, senderId, "📷 Image")

            AuthResult.Success(message)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to send image",
                exception = e
            )
        }
    }

    override suspend fun sendOrderLinkMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        orderPreview: Map<String, Any>
    ): AuthResult<Message> {
        return try {
            val messageRef = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .document()

            val message = Message(
                messageId = messageRef.id,
                senderId = senderId,
                senderName = senderName,
                messageType = "order_link",
                orderPreview = orderPreview,
                timestamp = Timestamp.now(),
            )

            messageRef.set(message).await()

            // Update chat's last message
            updateChatLastMessage(chatId, senderId, "📦 Order Link")

            AuthResult.Success(message)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to send order link",
                exception = e
            )
        }
    }

    override suspend fun markMessagesAsRead(
        chatId: String,
        userId: String
    ): AuthResult<Unit> {
        return try {
            // Get unread messages
            val unreadMessages = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .whereEqualTo("isRead", false)
                .whereNotEqualTo("senderId", userId)
                .get()
                .await()

            // Batch update
            val batch = firestore.batch()
            unreadMessages.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            // Reset unread count
            firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .update("unreadCount.$userId", 0)
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to mark as read",
                exception = e
            )
        }
    }

    override suspend fun getUserChats(userId: String): AuthResult<List<Chat>> {
        return try {
            val snapshot = firestore.collection(CHATS_COLLECTION)
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val chats = snapshot.toObjects(Chat::class.java)
            AuthResult.Success(chats)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get chats",
                exception = e
            )
        }
    }

    override suspend fun getChatMessages(
        chatId: String,
        limit: Int
    ): AuthResult<List<Message>> {
        return try {
            val snapshot = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val messages = snapshot.toObjects(Message::class.java).reversed()
            AuthResult.Success(messages)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get messages",
                exception = e
            )
        }
    }

    override fun observeUserChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = firestore.collection(CHATS_COLLECTION)
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.toObjects(Chat::class.java) ?: emptyList()
                trySend(chats)
            }

        awaitClose { listener.remove() }
    }

    override fun observeChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection(CHATS_COLLECTION)
            .document(chatId)
            .collection(MESSAGES_SUBCOLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        userId: String
    ): AuthResult<Unit> {
        return try {
            val messageSnapshot = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_SUBCOLLECTION)
                .document(messageId)
                .get()
                .await()

            val message = messageSnapshot.toObject(Message::class.java)
                ?: throw Exception("Message not found")

            if (message.senderId != userId) {
                throw Exception("Unauthorized: Not your message")
            }

            messageSnapshot.reference.delete().await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to delete message",
                exception = e
            )
        }
    }

    // Helper function
    private suspend fun updateChatLastMessage(
        chatId: String,
        senderId: String,
        messageText: String
    ) {
        try {
            val chatSnapshot = firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .get()
                .await()

            val chat = chatSnapshot.toObject(Chat::class.java) ?: return

            val unreadCount = chat.unreadCount.toMutableMap()
            chat.participants.forEach { participantId ->
                if (participantId != senderId) {
                    unreadCount[participantId] = (unreadCount[participantId] ?: 0) + 1
                }
            }

            firestore.collection(CHATS_COLLECTION)
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to messageText,
                        "lastMessageTime" to Timestamp.now(),
                        "lastMessageSender" to senderId,
                        "unreadCount" to unreadCount
                    )
                )
                .await()
        } catch (e: Exception) {
            // Non-critical, log but don't throw
        }
    }
}
