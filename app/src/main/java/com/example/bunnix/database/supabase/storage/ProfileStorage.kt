package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ProfileStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD PROFILE PICTURE
    suspend fun uploadProfilePicture(
        context: Context,
        userId: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$userId/profile.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.USER_PROFILES)

            // ✅ CORRECT: Using options builder
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

    // UPLOAD VENDOR COVER
    suspend fun uploadVendorCover(
        context: Context,
        vendorId: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$vendorId/cover.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.VENDOR_PHOTOS)

            // ✅ CORRECT: Using options builder
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