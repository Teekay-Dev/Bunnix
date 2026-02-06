package com.example.bunnix.model.domain.storage

interface ReceiptStorage {
    suspend fun upload(
        fileBytes: ByteArray,
        fileName: String
    ): String
}
