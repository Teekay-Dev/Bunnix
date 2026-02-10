package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ChatCollection {

    private val chatsCollection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.CHATS)

    // GET OR CREATE CHAT
    suspend fun getOrCreateChat(customerId: String, vendorId: String): Result<String> {
        return try {
            // Check if chat exists
            val existingChat = chatsCollection
                .whereArrayContains("participants", customerId)
                .get()
                .await()
                .documents
                .firstOrNull { doc ->
                    val participants = doc.get("participants") as? List<*>
                    participants?.contains(vendorId) == true
                }

            if (existingChat != null) {
                return Result.success(existingChat.id)
            }

            // Create new chat
            val chatData = mapOf(
                "participants" to listOf(customerId, vendorId),
                "participantDetails" to emptyMap<String, Any>(),
                "lastMessage" to "",
                "lastMessageTime" to null,
                "lastMessageSender" to "",
                "unreadCount" to mapOf(
                    customerId to 0,
                    vendorId to 0
                ),
                "relatedOrderId" to "",
                "relatedBookingId" to "",
                "createdAt" to Timestamp.now()
            )

            val docRef = chatsCollection.add(chatData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET USER CHATS (Real-time)
    fun getUserChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = chatsCollection
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

    // GET MESSAGES (Real-time)
    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = chatsCollection
            .document(chatId)
            .collection(FirebaseConfig.Collections.MESSAGES)
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

    // SEND MESSAGE
    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String,
        imageUrl: String = "",
        messageType: String = "text"
    ): Result<String> {
        return try {
            val messageData = mapOf(
                "senderId" to senderId,
                "senderName" to senderName,
                "text" to text,
                "imageUrl" to imageUrl,
                "messageType" to messageType,
                "orderPreview" to emptyMap<String, Any>(),
                "isRead" to false,
                "timestamp" to Timestamp.now()
            )

            // Add message to subcollection
            val messageRef = chatsCollection
                .document(chatId)
                .collection(FirebaseConfig.Collections.MESSAGES)
                .add(messageData)
                .await()

            // Update chat's last message
            updateLastMessage(chatId, senderId, text)

            Result.success(messageRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SEND MESSAGE WITH ORDER PREVIEW
    suspend fun sendMessageWithOrderPreview(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String,
        orderPreview: Map<String, Any>
    ): Result<String> {
        return try {
            val messageData = mapOf(
                "senderId" to senderId,
                "senderName" to senderName,
                "text" to text,
                "imageUrl" to "",
                "messageType" to "order_link",
                "orderPreview" to orderPreview,
                "isRead" to false,
                "timestamp" to Timestamp.now()
            )

            val messageRef = chatsCollection
                .document(chatId)
                .collection(FirebaseConfig.Collections.MESSAGES)
                .add(messageData)
                .await()

            updateLastMessage(chatId, senderId, text)

            Result.success(messageRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK MESSAGES AS READ
    suspend fun markMessagesAsRead(chatId: String, userId: String): Result<Unit> {
        return try {
            // Reset unread count for user
            val updates = mapOf(
                "unreadCount.$userId" to 0
            )
            chatsCollection.document(chatId).update(updates).await()

            // Mark individual messages as read
            val messagesSnapshot = chatsCollection
                .document(chatId)
                .collection(FirebaseConfig.Collections.MESSAGES)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = FirebaseConfig.firestore.batch()
            messagesSnapshot.documents.forEach { doc ->
                if (doc.getString("senderId") != userId) {
                    batch.update(doc.reference, "isRead", true)
                }
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE LAST MESSAGE
    private suspend fun updateLastMessage(chatId: String, senderId: String, text: String) {
        try {
            val chatDoc = chatsCollection.document(chatId).get().await()
            val participants = chatDoc.get("participants") as? List<*>
            val otherUserId = participants?.firstOrNull { it != senderId } as? String

            val updates = mutableMapOf<String, Any>(
                "lastMessage" to text,
                "lastMessageTime" to Timestamp.now(),
                "lastMessageSender" to senderId
            )

            // Increment unread count for other user
            if (otherUserId != null) {
                val currentUnread = (chatDoc.get("unreadCount") as? Map<*, *>)?.get(otherUserId) as? Long ?: 0
                updates["unreadCount.$otherUserId"] = currentUnread + 1
            }

            chatsCollection.document(chatId).update(updates).await()
        } catch (e: Exception) {
            // Ignore errors in updating last message
        }
    }
}