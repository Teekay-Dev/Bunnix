package com.example.bunnix.domain.payment

import com.example.bunnix.domain.order.FakeOrderRepository

class FakePaymentRepository(
    private val orderRepo: FakeOrderRepository
) : PaymentRepository {

    private val proofs = mutableListOf<PaymentProof>()

    override suspend fun uploadProof(proof: PaymentProof): PaymentProof {
        proofs.add(proof)
        return proof
    }

    override suspend fun verifyPayment(orderId: String, vendorId: String): Boolean {
        // Delegates to FakeOrderRepository for state change
        return orderRepo.verifyPayment(orderId, vendorId)
    }

    fun allProofs(): List<PaymentProof> = proofs.toList()
}
