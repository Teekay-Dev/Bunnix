package com.example.bunnix.database.supabase.storage

import android.content.Context
import android.net.Uri
import com.example.bunnix.database.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object PaymentStorage {

    private val storage = SupabaseConfig.client.storage

    // UPLOAD PAYMENT RECEIPT
    suspend fun uploadPaymentReceipt(
        context: Context,
        orderId: String,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri)
            val fileBytes = file.readBytes()
            val timestamp = System.currentTimeMillis()
            val path = "$orderId/receipt_$timestamp.jpg"

            val bucket = storage.from(SupabaseConfig.Buckets.PAYMENT_RECEIPTS)

            // ✅ CORRECT: Using options builder
            bucket.upload(
                path = path,
                data = fileBytes,
                upsert = false
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