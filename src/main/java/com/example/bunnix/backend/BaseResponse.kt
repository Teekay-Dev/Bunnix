package com.example.bunnix.backend

data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)