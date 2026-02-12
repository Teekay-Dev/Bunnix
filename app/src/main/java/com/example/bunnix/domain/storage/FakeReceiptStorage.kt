package com.example.bunnix.domain.storage

class FakeReceiptStorage : ReceiptStorage {

    override suspend fun upload(
        fileBytes: ByteArray,
        fileName: String
    ): String {
        // Simulate a cloud URL
        return "https://fake-storage.bunnix.app/receipts/$fileName"
    }
}
