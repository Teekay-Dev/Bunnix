package com.example.bunnix.model.domain.booking

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

data class Booking @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String,
    val customerId: String,
    val serviceId: String,
    val vendorId: String,
    val scheduledTime: kotlinx.datetime.Instant,
    val status: BookingStatus = BookingStatus.REQUESTED,
    val createdAt: Instant = Instant.now()
)
