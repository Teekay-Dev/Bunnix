package com.example.bunnix.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String? = null,
    val isVendor: Boolean = false,
    val createdAt: String = ""
)
