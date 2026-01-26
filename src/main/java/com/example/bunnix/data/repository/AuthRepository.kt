package com.example.bunnix.data.repository

import com.example.bunnix.data.model.*
import com.example.bunnix.data.remote.RetrofitClient
import com.example.bunnix.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

@Suppress("unused")
class AuthRepository {

    private val api = RetrofitClient.authApi

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): NetworkResult<AuthData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(
                    RegisterRequest(name, email, phone, password, confirmPassword)
                )
                handleAuthResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun login(email: String, password: String): NetworkResult<AuthData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                handleAuthResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun logout(token: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.logout("Bearer $token")
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun forgotPassword(email: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.forgotPassword(ForgotPasswordRequest(email))
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun verifyOtp(email: String, otp: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.verifyOtp(VerifyOtpRequest(email, otp))
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun resetPassword(
        email: String,
        otp: String,
        newPassword: String
    ): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.resetPassword(
                    ResetPasswordRequest(email, otp, newPassword)
                )
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.changePassword(
                    "Bearer $token",
                    ChangePasswordRequest(currentPassword, newPassword, confirmPassword)
                )
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun getProfile(token: String): NetworkResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getProfile("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.data != null) {
                        NetworkResult.Success(body.data.user)
                    } else {
                        NetworkResult.Error(body.message)
                    }
                } else {
                    NetworkResult.Error("Failed to fetch profile")
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun updateProfile(
        token: String,
        name: String? = null,
        phone: String? = null,
        profileImage: String? = null
    ): NetworkResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.updateProfile(
                    "Bearer $token",
                    UpdateProfileRequest(name, phone, profileImage)
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.user != null) {
                        NetworkResult.Success(body.user)
                    } else {
                        NetworkResult.Error(body.message)
                    }
                } else {
                    NetworkResult.Error("Failed to update profile")
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun deleteAccount(token: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteAccount("Bearer $token")
                handleMessageResponse(response)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    private fun handleAuthResponse(response: Response<AuthResponse>): NetworkResult<AuthData> {
        return if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (body.success && body.data != null) {
                NetworkResult.Success(body.data)
            } else {
                NetworkResult.Error(body.message)
            }
        } else {
            NetworkResult.Error("Request failed. Please try again.")
        }
    }

    private fun handleMessageResponse(response: Response<MessageResponse>): NetworkResult<String> {
        return if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (body.success) {
                NetworkResult.Success(body.message)
            } else {
                NetworkResult.Error(body.message)
            }
        } else {
            NetworkResult.Error("Request failed. Please try again.")
        }
    }
}
