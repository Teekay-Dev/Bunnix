package com.example.bunnix.model.domain.payment

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

data class PaymentProof @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val orderId: String,
    val customerId: String,
    val filePath: String // mock storage path
)
