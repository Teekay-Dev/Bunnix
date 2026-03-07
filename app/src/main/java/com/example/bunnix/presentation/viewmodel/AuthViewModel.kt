package com.example.bunnix.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.database.models.VerificationStep
import com.example.bunnix.database.models.VerificationState
import com.example.bunnix.domain.repository.AuthRepository
import com.example.bunnix.domain.usecase.auth.SignUpUseCase
import com.example.bunnix.domain.usecase.auth.SignInUseCase
import com.example.bunnix.domain.usecase.auth.SignOutUseCase
import com.example.bunnix.domain.usecase.auth.GetCurrentUserUseCase
import com.example.bunnix.domain.usecase.auth.SignInWithGoogleUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _verificationState = MutableStateFlow(VerificationState())
    val verificationState: StateFlow<VerificationState> = _verificationState.asStateFlow()

    // Store data temporarily until verified
    var tempUserData by mutableStateOf<User?>(null)
    var tempPassword by mutableStateOf<String?>(null)
    var tempVendorData by mutableStateOf<VendorProfile?>(null)

    init {
        getCurrentUser()
    }

    /**
     * Initiate signup - stores user data and moves to verification
     */
    fun initiateSignup(userData: User, password: String, vendorData: VendorProfile? = null) {
        Log.d("AuthViewModel", "initiateSignup called for ${userData.email}")
        this.tempUserData = userData
        this.tempPassword = password
        this.tempVendorData = vendorData

        _verificationState.update {
            Log.d("AuthViewModel", "Moving to SELECT_METHOD")
            it.copy(currentStep = VerificationStep.SELECT_METHOD)
        }
    }

    /**
     * Start email verification process
     */
    fun startEmailVerification() {
        Log.d("AuthViewModel", "startEmailVerification called")
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            createAccountAndSendEmailVerification()
        }
    }

    /**
     * Create account and send standard Firebase email verification
     */
    private suspend fun createAccountAndSendEmailVerification() {
        val user = tempUserData ?: run {
            Log.e("AuthViewModel", "tempUserData is null!")
            _uiState.value = AuthUiState.Error("User data not found")
            return
        }
        val password = tempPassword ?: run {
            Log.e("AuthViewModel", "tempPassword is null!")
            _uiState.value = AuthUiState.Error("Password not found")
            return
        }

        Log.d("AuthViewModel", "Creating account for ${user.email}")

        try {
            val authResult = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.email, password)
                .await()

            val firebaseUser = authResult.user
                ?: throw Exception("Account creation failed - no user returned")

            Log.d("AuthViewModel", "Account created, sending verification email")

            // STANDARD FIREBASE EMAIL VERIFICATION
            firebaseUser.sendEmailVerification().await()

            Log.d("AuthViewModel", "Verification email sent, moving to EMAIL_INSTRUCTIONS")

            _verificationState.update {
                it.copy(currentStep = VerificationStep.EMAIL_INSTRUCTIONS)
            }
            _uiState.value = AuthUiState.Idle

        } catch (e: Exception) {
            Log.e("AuthViewModel", "Email verification setup failed", e)
            _uiState.value = AuthUiState.Error(e.message ?: "Failed to send verification email")
        }
    }

    /**
     * Reset to idle step
     */
    fun resetStep() {
        _verificationState.update {
            it.copy(currentStep = VerificationStep.IDLE)
        }
    }

    /**
     * Check if email is verified
     */
    fun checkEmailVerification() {
        viewModelScope.launch {
            try {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser == null) {
                    Log.e("AuthViewModel", "No current user found")
                    _uiState.value = AuthUiState.Error("User not found. Please try again.")
                    return@launch
                }

                Log.d("AuthViewModel", "Checking verification status for ${firebaseUser.email}")
                firebaseUser.reload().await()

                if (firebaseUser.isEmailVerified) {
                    Log.d("AuthViewModel", "Email is verified, completing signup")
                    completeSignup(firebaseUser.uid)
                } else {
                    Log.d("AuthViewModel", "Email not yet verified")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "checkEmailVerification failed", e)
            }
        }
    }

    /**
     * Complete signup after email verification
     */
    private fun completeSignup(uid: String) {
        viewModelScope.launch {
            val user = tempUserData?.copy(userId = uid) ?: return@launch
            val vendorData = tempVendorData

            _uiState.value = AuthUiState.Loading

            try {
                if (vendorData != null || user.isVendor) {
                    // VENDOR SIGNUP
                    val finalVendorData = vendorData?.copy(
                        vendorId = uid,
                        userId = uid,
                        status = "approved"
                    ) ?: VendorProfile(
                        vendorId = uid,
                        userId = uid,
                        businessName = user.name,
                        category = "",
                        address = user.address,
                        phone = user.phone,
                        email = user.email,
                        status = "approved"
                    )

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .set(user)
                        .await()

                    FirebaseFirestore.getInstance()
                        .collection("vendorProfiles")
                        .document(uid)
                        .set(finalVendorData)
                        .await()

                } else {
                    // CUSTOMER SIGNUP
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .set(user)
                        .await()
                }

                _verificationState.update {
                    it.copy(
                        isEmailVerified = true,
                        currentStep = VerificationStep.COMPLETED
                    )
                }
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(user)

            } catch (e: Exception) {
                Log.e("AuthViewModel", "completeSignup failed", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to complete signup")
            }
        }
    }

    // ===== LEGACY METHODS =====

    fun isVerificationComplete(): Boolean {
        return _verificationState.value.isEmailVerified
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        _isLoading.value = true
        val result = signInUseCase(email, password)
        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }
        _isLoading.value = false
        return result
    }

    suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        _isLoading.value = true
        val result = signInWithGoogleUseCase(idToken)
        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }
        _isLoading.value = false
        return result
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            _verificationState.value = VerificationState()
            tempUserData = null
            tempPassword = null
            tempVendorData = null
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result is AuthResult.Success) {
                _currentUser.value = result.data
            }
        }
    }

    fun clearTempData() {
        tempUserData = null
        tempPassword = null
        tempVendorData = null
        _verificationState.value = VerificationState()
    }
}