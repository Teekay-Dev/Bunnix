package com.example.bunnix.model.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Review(
    val id: String,
    val reviewerId: String,
    val vendorId: String,
    val orderId: String,
    val rating: Int,
    val comment: String,
    val createdAt: Instant = Clock.System.now()
)
