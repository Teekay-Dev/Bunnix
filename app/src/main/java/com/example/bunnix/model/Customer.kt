package com.example.bunnix.model

data class Customer(
    val id: Int = 0,
    val role: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val profileImage: String,
    val createdAt: String
)
