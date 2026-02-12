package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ReviewStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD REVIEW IMAGE
    suspend fun uploadReviewImage(
        context: Context,
        reviewId: String,
        imageUri: Uri,
        imageIndex: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$reviewId/$imageIndex.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.REVIEW_IMAGES)

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

    // UPLOAD MULTIPLE REVIEW IMAGES
    suspend fun uploadMultipleReviewImages(
        context: Context,
        reviewId: String,
        imageUris: List<Uri>
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val uploadedUrls = mutableListOf<String>()

            imageUris.forEachIndexed { index, uri ->
                val result = uploadReviewImage(context, reviewId, uri, index)
                result.onSuccess { url ->
                    uploadedUrls.add(url)
                }
                result.onFailure { error ->
                    throw error
                }
            }

            Result.success(uploadedUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE REVIEW IMAGE
    suspend fun deleteReviewImage(reviewId: String, imageIndex: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val path = "$reviewId/$imageIndex.jpg"
            val bucket = storage.from(SupabaseConfig.Buckets.REVIEW_IMAGES)
            bucket.delete(path)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE ALL REVIEW IMAGES
    suspend fun deleteAllReviewImages(reviewId: String, imageCount: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val paths = (0 until imageCount).map { "$reviewId/$it.jpg" }
            val bucket = storage.from(SupabaseConfig.Buckets.REVIEW_IMAGES)
            bucket.delete(paths)
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