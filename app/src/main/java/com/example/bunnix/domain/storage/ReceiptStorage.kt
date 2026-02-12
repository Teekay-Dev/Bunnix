package com.example.bunnix.domain.storage

interface ReceiptStorage {
    suspend fun upload(
        fileBytes: ByteArray,
        fileName: String
    ): String
}
