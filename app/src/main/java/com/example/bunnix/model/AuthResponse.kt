package com.example.bunnix.model

import com.example.bunnix.model.Vendor

data class AuthResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: AuthData? = null
)

data class AuthData(
    val token: String = "",
    val vendor: Vendor = Vendor(0, "", "", "", "", "", "", "vendor", "")
)

data class MessageResponse(
    val success: Boolean = false,
    val message: String = ""
)

data class VendorResponse(
    val success: Boolean = false,
    val message: String = "",
    val vendor: Vendor? = null
)
