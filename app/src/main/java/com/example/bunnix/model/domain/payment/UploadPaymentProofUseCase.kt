package com.example.bunnix.model.domain.payment

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.model.data.auth.AuthManager
import com.example.bunnix.model.domain.events.DomainEvent
import com.example.bunnix.model.domain.events.EventBus
import com.example.bunnix.model.domain.storage.ReceiptStorage
import com.example.bunnix.model.domain.user.UserMode
import com.example.bunnix.model.domain.user.UserModeManager
import java.time.Instant
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
