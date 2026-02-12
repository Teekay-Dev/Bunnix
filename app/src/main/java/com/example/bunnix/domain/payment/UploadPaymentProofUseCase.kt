package com.example.bunnix.domain.payment

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.model.data.auth.AuthManager
import java.util.UUID

class UploadPaymentProofUseCase(
private val auth: AuthManager,
private val paymentRepo: PaymentRepository,
private val eventBus: EventBus
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(orderId: String, filePath: String): Result<PaymentProof> {
        val uid = auth.currentUserUid()
        val proof = PaymentProof(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            customerId = uid,
            filePath = filePath
        )

        val saved = paymentRepo.uploadProof(proof)
        eventBus.emit(DomainEvent.PaymentProofUploaded(orderId, uid))
        return Result.success(saved)
    }
}
