package com.example.bunnix.data.model

data class AuthResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: AuthData? = null
)

data class AuthData(
    val token: String = "",
    val user: User = User()
)

data class MessageResponse(
    val success: Boolean = false,
    val message: String = ""
)

data class UserResponse(
    val success: Boolean = false,
    val message: String = "",
    val user: User? = null
)
