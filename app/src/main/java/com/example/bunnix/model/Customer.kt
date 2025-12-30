package com.example.bunnix.model

data class Customer(
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val profileImage: String,
    val createdAt: String
)
