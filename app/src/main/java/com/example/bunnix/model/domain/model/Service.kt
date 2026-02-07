package com.example.bunnix.model.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Service(
    val id: String,
    val vendorId: String,
    val title: String,
    val basePrice: Double,
    val durationMinutes: Int,
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now()
)

