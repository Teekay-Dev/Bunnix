package com.example.bunnix.domain.payment

interface PaymentRepository {
    suspend fun uploadProof(proof: PaymentProof): PaymentProof
    suspend fun verifyPayment(orderId: String, vendorId: String): Boolean
}
