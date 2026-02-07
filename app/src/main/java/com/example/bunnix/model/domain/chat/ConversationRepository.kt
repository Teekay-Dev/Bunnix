package com.example.bunnix.model.domain.chat

interface ConversationRepository {

    suspend fun create(conversation: Conversation): Conversation

    suspend fun getByReference(
        type: ConversationType,
        referenceId: String
    ): Conversation?
}
