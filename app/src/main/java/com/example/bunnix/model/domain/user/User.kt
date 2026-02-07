package com.example.bunnix.model.domain.user

enum class UserMode {
    CUSTOMER,
    VENDOR
}

data class User(
    val uid: String,
    val email: String,
    val mode: UserMode
)
