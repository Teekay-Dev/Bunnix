package com.example.bunnix.model.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Product(
    val id: String,
    val vendorId: String,
    val name: String,
    val price: Double,
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now()
)

