package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import java.util.UUID

object ChatStorage {

    private val storage = SupabaseConfig.client.storage

    suspend fun uploadChatImage(context: Context, uri: Uri): Result<String> {
        return try {
            val bucket = storage["chat-images"]
            val fileName = "images/${UUID.randomUUID()}.jpg"

            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Could not read file")

            // ✅ FIX: Pass upsert as a direct parameter, not in a lambda
            bucket.upload(fileName, bytes, upsert = false)

            val publicUrl = bucket.publicUrl(fileName)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVoiceNote(context: Context, uri: Uri): Result<String> {
        return try {
            val bucket = storage["chat-audio"]
            val fileName = "voice/${UUID.randomUUID()}.m4a"

            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Could not read audio file")

            // ✅ FIX: Pass upsert as a direct parameter, not in a lambda
            bucket.upload(fileName, bytes, upsert = false)

            val publicUrl = bucket.publicUrl(fileName)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}