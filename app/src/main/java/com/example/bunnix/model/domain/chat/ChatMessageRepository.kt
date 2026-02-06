package com.example.bunnix.model.domain.chat

interface ChatMessageRepository {

    suspend fun send(message: ChatMessage): ChatMessage

    suspend fun getByConversation(conversationId: String): List<ChatMessage>
}
