package com.example.bunnix.database.supabase

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object SupabaseManager {

    private val storage = SupabaseConfig.client.storage

    /**
     * GENERIC UPLOAD - Use this for any bucket
     */
    suspend fun uploadImage(
        context: Context,
        bucketName: String,
        folderPath: String,
        fileName: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val path = "$folderPath/$fileName"

            val bucket = storage.from(bucketName)

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

    /**
     * GENERIC DELETE
     */
    suspend fun deleteImage(bucketName: String, path: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val bucket = storage.from(bucketName)
            bucket.delete(path)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * DELETE MULTIPLE FILES
     */
    suspend fun deleteMultipleImages(bucketName: String, paths: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val bucket = storage.from(bucketName)
            bucket.delete(paths)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * LIST FILES IN FOLDER
     */
    suspend fun listFiles(bucketName: String, folderPath: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val bucket = storage.from(bucketName)
            val files = bucket.list(folderPath)
            val fileNames = files.map { it.name }
            Result.success(fileNames)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * GET PUBLIC URL
     */
    fun getPublicUrl(bucketName: String, path: String): String {
        val bucket = storage.from(bucketName)
        return bucket.publicUrl(path)
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