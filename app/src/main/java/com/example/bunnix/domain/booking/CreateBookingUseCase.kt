package com.example.bunnix.domain.booking

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bunnix.domain.events.DomainEvent
import com.example.bunnix.domain.events.EventBus
import com.example.bunnix.model.data.auth.AuthManager
import kotlinx.datetime.Instant
import java.util.UUID

class CreateBookingUseCase(
    private val auth: AuthManager,
    private val bookingRepo: BookingRepository,
    private val eventBus: EventBus
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(serviceId: String, vendorId: String, scheduledTime: Instant): Result<Booking> {
        val uid = auth.currentUserUid()
        val booking = Booking(
            id = UUID.randomUUID().toString(),
            customerId = uid,
            serviceId = serviceId,
            vendorId = vendorId,
            scheduledTime = scheduledTime
        )
        val saved = bookingRepo.create(booking)
        eventBus.emit(DomainEvent.BookingRequested(saved.id, vendorId))
        return Result.success(saved)
    }
}

