package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ServiceStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD SERVICE IMAGE
    suspend fun uploadServiceImage(
        context: Context,
        serviceId: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$serviceId/image.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.SERVICE_IMAGES)

            bucket.upload(
                path = path,
                data = fileBytes,
                upsert = true
            )

            val publicUrl = bucket.publicUrl(path)
            file.delete()

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE SERVICE IMAGE
    suspend fun deleteServiceImage(serviceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val path = "$serviceId/image.jpg"
            val bucket = storage.from(SupabaseConfig.Buckets.SERVICE_IMAGES)
            bucket.delete(path)
            Result.success(Unit)
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