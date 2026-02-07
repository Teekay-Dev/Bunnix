package com.example.bunnix.model.domain.chat

class FakeConversationRepository : ConversationRepository {

    private val conversations = mutableMapOf<String, Conversation>()

    override suspend fun create(conversation: Conversation): Conversation {
        conversations[conversation.id] = conversation
        return conversation
    }

    override suspend fun getByReference(
        type: ConversationType,
        referenceId: String
    ): Conversation? {
        return conversations.values.firstOrNull {
            it.type == type && it.referenceId == referenceId
        }
    }
}
