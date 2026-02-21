package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.domain.usecase.auth.SignUpUseCase
import com.example.bunnix.domain.usecase.auth.SignInUseCase
import com.example.bunnix.domain.usecase.auth.SignOutUseCase
import com.example.bunnix.domain.usecase.auth.GetCurrentUserUseCase
import com.example.bunnix.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Authentication
 * Connects your UI to the backend use cases
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    // State flows for UI
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Load current user on init
        getCurrentUser()
    }

    /**
     * Sign up with email
     * Called from SignupActivity
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        phone: String,
        isBusinessAccount: Boolean,
        businessName: String,
        businessAddress: String
    ): AuthResult<User> {
        _isLoading.value = true

        val result = signUpUseCase(
            email = email,
            password = password,
            displayName = displayName,
            phone = phone,
            isBusinessAccount = isBusinessAccount,
            businessName = businessName,
            businessAddress = businessAddress
        )

        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }

        _isLoading.value = false
        return result
    }

    /**
     * Sign in with email
     * Called from LoginActivity
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User> {
        _isLoading.value = true

        val result = signInUseCase(email, password)

        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }

        _isLoading.value = false
        return result
    }

    /**
     * Sign out
     */
    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
        }
    }

    /**
     * Get current user
     */
    fun getCurrentUser() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result is AuthResult.Success) {
                _currentUser.value = result.data
            }
        }
    }

    /**
     * Sign in with Google
     * Called from LoginActivity when Google button is clicked
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        _isLoading.value = true

        // You need to create this use case
        val result = signInWithGoogleUseCase(idToken)

        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }

        _isLoading.value = false
        return result
    }
}
