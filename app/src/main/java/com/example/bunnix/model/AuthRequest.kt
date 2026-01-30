package com.example.bunnix.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val confirmPassword: String,
    val businessName: String? = null,
    val businessAddress: String? = null,
    val role: String = "customer" // default role
)

data class ForgotPasswordRequest(
    val email: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val profileImage: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
