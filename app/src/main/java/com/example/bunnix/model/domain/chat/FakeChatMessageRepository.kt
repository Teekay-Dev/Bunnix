package com.example.bunnix.model.domain.chat

class FakeChatMessageRepository : ChatMessageRepository {

    private val messages = mutableListOf<ChatMessage>()

    override suspend fun send(message: ChatMessage): ChatMessage {
        messages.add(message)
        return message
    }

    override suspend fun getByConversation(conversationId: String): List<ChatMessage> {
        return messages.filter { it.conversationId == conversationId }
    }
}
