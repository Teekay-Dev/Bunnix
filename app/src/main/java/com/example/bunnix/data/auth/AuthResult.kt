package com.example.bunnix.data.auth


/**
 * Sealed class representing the result of authentication operations.
 * Provides type-safe success and error handling.
 */
sealed class AuthResult<out T> {
    fun onSuccess(function: () -> Unit) {


    }

    /**
     * Successful authentication result with data
     */
    data class Success<T>(val data: T) : AuthResult<T>()

    /**
     * Authentication failure with error details
     */
    data class Error(
        val message: String,
        val exception: Exception? = null
    ) : AuthResult<Nothing>()

    /**
     * Loading state (optional, for UI handling)
     */
    object Loading : AuthResult<Nothing>()
}

/**
 * Extension functions for cleaner result handling
 */
fun <T> AuthResult<T>.isSuccess(): Boolean = this is AuthResult.Success
fun <T> AuthResult<T>.isError(): Boolean = this is AuthResult.Error
fun <T> AuthResult<T>.isLoading(): Boolean = this is AuthResult.Loading

/**
 * Get data or null if not success
 */
fun <T> AuthResult<T>.getOrNull(): T? {
    return when (this) {
        is AuthResult.Success -> data
        else -> null
    }
}

/**
 * Get error message or null
 */
fun <T> AuthResult<T>.getErrorMessage(): String? {
    return when (this) {
        is AuthResult.Error -> message
        else -> null
    }
}
