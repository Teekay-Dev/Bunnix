package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ProductStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD PRODUCT IMAGE
    suspend fun uploadProductImage(
        context: Context,
        productId: String,
        imageUri: Uri,
        imageIndex: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$productId/$imageIndex.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.PRODUCT_IMAGES)

            // ✅ CORRECT: Using options builder
            bucket.upload(
                path = path,
                data = fileBytes,
                options = {
                    upsert = true
                }
            )

            val publicUrl = bucket.publicUrl(path)
            file.delete()

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE PRODUCT IMAGE
    suspend fun deleteProductImage(productId: String, imageIndex: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val path = "$productId/$imageIndex.jpg"
            val bucket = storage.from(SupabaseConfig.Buckets.PRODUCT_IMAGES)
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