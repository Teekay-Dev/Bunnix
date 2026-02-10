package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object ChatStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD CHAT IMAGE
    suspend fun uploadChatImage(
        context: Context,
        chatId: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val messageId = UUID.randomUUID().toString()
            val path = "$chatId/$messageId.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.CHAT_IMAGES)

            // ✅ CORRECT: Using options builder
            bucket.upload(
                path = path,
                data = fileBytes,
                options = {
                    upsert = false
                }
            )

            val publicUrl = bucket.publicUrl(path)
            file.delete()

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open URI")

        val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        return tempFile
    }
}