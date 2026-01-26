package com.example.bunnix.utils

@Suppress("unused")
object Constants {
    // Change this to your backend URL
    const val BASE_URL = "http://10.0.2.2:3000/api/" // For emulator
    // const val BASE_URL = "http://192.168.1.100:3000/api/" // For real device
    // const val BASE_URL = "https://api.bunnix.com/api/" // For production

    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 2
    const val MIN_PHONE_LENGTH = 10
    const val OTP_LENGTH = 6

    const val ERROR_EMPTY_NAME = "Name cannot be empty"
    const val ERROR_SHORT_NAME = "Name must be at least 2 characters"
    const val ERROR_EMPTY_EMAIL = "Email cannot be empty"
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address"
    const val ERROR_EMPTY_PASSWORD = "Password cannot be empty"
    const val ERROR_SHORT_PASSWORD = "Password must be at least 6 characters"
    const val ERROR_PASSWORD_MISMATCH = "Passwords do not match"
    const val ERROR_EMPTY_PHONE = "Phone number cannot be empty"
    const val ERROR_INVALID_PHONE = "Please enter a valid phone number"
    const val ERROR_EMPTY_OTP = "OTP cannot be empty"
    const val ERROR_INVALID_OTP = "OTP must be 6 digits"
    const val ERROR_NETWORK = "Network error occurred. Please check your connection."

    const val SUCCESS_REGISTRATION = "Registration successful!"
    const val SUCCESS_LOGIN = "Login successful!"
    const val SUCCESS_LOGOUT = "Logged out successfully"
    const val SUCCESS_PASSWORD_RESET = "Password reset successful!"
    const val SUCCESS_OTP_SENT = "OTP sent to your email"
    const val SUCCESS_PROFILE_UPDATE = "Profile updated successfully"
}

