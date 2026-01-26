package com.example.bunnix.utils

import android.util.Patterns

@Suppress("unused")
object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    fun isValidName(name: String): Boolean {
        return name.length >= Constants.MIN_NAME_LENGTH
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.length >= Constants.MIN_PHONE_LENGTH && phone.all { it.isDigit() }
    }

    fun isValidOtp(otp: String): Boolean {
        return otp.length == Constants.OTP_LENGTH && otp.all { it.isDigit() }
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotEmpty()
    }

    fun getEmailError(email: String): String? {
        return when {
            email.isEmpty() -> Constants.ERROR_EMPTY_EMAIL
            !isValidEmail(email) -> Constants.ERROR_INVALID_EMAIL
            else -> null
        }
    }

    fun getPasswordError(password: String): String? {
        return when {
            password.isEmpty() -> Constants.ERROR_EMPTY_PASSWORD
            !isValidPassword(password) -> Constants.ERROR_SHORT_PASSWORD
            else -> null
        }
    }

    fun getNameError(name: String): String? {
        return when {
            name.isEmpty() -> Constants.ERROR_EMPTY_NAME
            !isValidName(name) -> Constants.ERROR_SHORT_NAME
            else -> null
        }
    }

    fun getPhoneError(phone: String): String? {
        return when {
            phone.isEmpty() -> Constants.ERROR_EMPTY_PHONE
            !isValidPhone(phone) -> Constants.ERROR_INVALID_PHONE
            else -> null
        }
    }

    fun getOtpError(otp: String): String? {
        return when {
            otp.isEmpty() -> Constants.ERROR_EMPTY_OTP
            !isValidOtp(otp) -> Constants.ERROR_INVALID_OTP
            else -> null
        }
    }
}

