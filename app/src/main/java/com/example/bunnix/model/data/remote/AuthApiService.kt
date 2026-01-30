package com.example.bunnix.model.data.remote

import com.example.bunnix.model.AuthResponse
import com.example.bunnix.model.ChangePasswordRequest
import com.example.bunnix.model.ForgotPasswordRequest
import com.example.bunnix.model.LoginRequest
import com.example.bunnix.model.MessageResponse
import com.example.bunnix.model.RegisterRequest
import com.example.bunnix.model.ResetPasswordRequest
import com.example.bunnix.model.UpdateProfileRequest
import com.example.bunnix.model.VendorResponse
import com.example.bunnix.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<MessageResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    @POST("auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<MessageResponse>

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<AuthResponse>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<VendorResponse>


    @DELETE("auth/account")
    suspend fun deleteAccount(@Header("Authorization") token: String): Response<MessageResponse>
}
