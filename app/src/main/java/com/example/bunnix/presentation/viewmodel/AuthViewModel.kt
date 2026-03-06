package com.example.bunnix.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.database.models.VerificationMethod
import com.example.bunnix.database.models.VerificationStep
import com.example.bunnix.database.models.VerificationState
import com.example.bunnix.domain.repository.AuthRepository
import com.example.bunnix.domain.usecase.auth.SignUpUseCase
import com.example.bunnix.domain.usecase.auth.SignInUseCase
import com.example.bunnix.domain.usecase.auth.SignOutUseCase
import com.example.bunnix.domain.usecase.auth.GetCurrentUserUseCase
import com.example.bunnix.domain.usecase.auth.SignInWithGoogleUseCase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
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

    private val _phoneVerificationId = MutableStateFlow<String?>(null)
    val phoneVerificationId: StateFlow<String?> = _phoneVerificationId.asStateFlow()

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
     * Initiate signup - stores user data and moves to verification method selection
     */
    fun initiateSignup(userData: User, password: String, vendorData: VendorProfile? = null) {
        this.tempUserData = userData
        this.tempPassword = password
        this.tempVendorData = vendorData

        _verificationState.update { it.copy(currentStep = VerificationStep.SELECT_METHOD) }
    }

    /**
     * Set verification method and advance to appropriate step
     */
    fun setMethod(method: VerificationMethod) {
        _verificationState.update {
            it.copy(
                selectedMethod = method,
                currentStep = when (method) {
                    VerificationMethod.PHONE -> VerificationStep.PHONE_OTP
                    VerificationMethod.EMAIL -> VerificationStep.EMAIL_INSTRUCTIONS
                }
            )
        }

        if (method == VerificationMethod.EMAIL) {
            // For email, we need to create the account first, then send verification
            // This will be handled in finalizeUserCreation
            _uiState.value = AuthUiState.Loading
            viewModelScope.launch {
                createAccountAndSendEmailVerification()
            }
        }
    }

    /**
     * Create account and send email verification
     */
    private suspend fun createAccountAndSendEmailVerification() {
        val user = tempUserData ?: return
        val password = tempPassword ?: return

        try {
            // Create Firebase Auth account first
            val authResult = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.email, password)
                .await()

            val firebaseUser = authResult.user
                ?: throw Exception("Account creation failed")

            // Send email verification
            firebaseUser.sendEmailVerification().await()

            // Save user to Firestore (unverified)
            val result = repository.createFinalUser(user.copy(userId = firebaseUser.uid))

            if (result is AuthResult.Success) {
                _verificationState.update {
                    it.copy(
                        isEmailVerified = false,
                        emailVerificationSent = true,
                        currentStep = VerificationStep.EMAIL_INSTRUCTIONS
                    )
                }
            } else {
                _uiState.value = AuthUiState.Error("Failed to save user data")
            }

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
     * Complete phone verification - creates account after OTP verification
     */
    fun completePhoneVerification(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                // Sign in with phone credential - this creates the account
                val authResult = FirebaseAuth.getInstance()
                    .signInWithCredential(credential)
                    .await()

                val firebaseUser = authResult.user
                    ?: throw Exception("Phone verification failed")

                // Now save user data to Firestore
                val user = tempUserData?.copy(userId = firebaseUser.uid)
                    ?: throw Exception("User data not found")

                val result = if (user.isVendor) {
                    repository.signUpWithEmail(
                        email = user.email,
                        password = tempPassword ?: throw Exception("Password not found"),
                        displayName = user.name,
                        phone = user.phone,
                        isBusinessAccount = true,
                        businessName = tempVendorData?.businessName ?: user.name,
                        businessAddress = user.address,
                        category = tempVendorData?.category ?: ""
                    )
                } else {
                    repository.createFinalUser(user)
                }

                if (result is AuthResult.Success) {
                    _verificationState.update {
                        it.copy(
                            isPhoneVerified = true,
                            currentStep = VerificationStep.COMPLETED
                        )
                    }
                    _currentUser.value = result.data
                    _uiState.value = AuthUiState.Success(result.data)
                } else if (result is AuthResult.Error) {
                    _uiState.value = AuthUiState.Error(result.message)
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Phone verification failed", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Verification failed")
            }
        }
    }

    /**
     * Verify phone OTP
     */
    fun verifyPhone(activity: Activity, verificationId: String, otp: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            completePhoneVerification(credential)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Invalid verification code")
        }
    }

    /**
     * Send phone verification code
     */
    fun sendVerificationCode(activity: Activity, phoneNumber: String) {
        _uiState.value = AuthUiState.Loading
        Log.d("AuthViewModel", "Sending verification code to: $phoneNumber")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("AuthViewModel", "onVerificationCompleted - instant verification")
                completePhoneVerification(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("AuthViewModel", "onVerificationFailed", e)
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
                    is FirebaseTooManyRequestsException -> "Too many requests. Try again later."
                    else -> e.message ?: "Verification failed"
                }
                _uiState.value = AuthUiState.Error(message)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("AuthViewModel", "onCodeSent - verificationId: $verificationId")
                _phoneVerificationId.value = verificationId
                _verificationState.update {
                    it.copy(currentStep = VerificationStep.PHONE_OTP)
                }
                _uiState.value = AuthUiState.Idle // Clear loading
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d("AuthViewModel", "onCodeAutoRetrievalTimeOut")
                // Don't treat this as error, just let user enter manually
            }
        }

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Check if email is verified
     */
    fun checkEmailVerification() {
        viewModelScope.launch {
            try {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser == null) {
                    _uiState.value = AuthUiState.Error("User not found. Please try again.")
                    return@launch
                }

                // Reload to get latest verification status
                firebaseUser.reload().await()

                if (firebaseUser.isEmailVerified) {
                    _verificationState.update {
                        it.copy(
                            isEmailVerified = true,
                            currentStep = VerificationStep.COMPLETED
                        )
                    }
                    // Complete signup
                    finalizeEmailVerification()
                } else {
                    // Not verified yet, stay on current screen
                    // Optionally show a toast
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "checkEmailVerification failed", e)
                _uiState.value = AuthUiState.Error("Failed to check verification status")
            }
        }
    }

    /**
     * Finalize email verification and save user
     */
    private fun finalizeEmailVerification() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return@launch
            val user = tempUserData?.copy(userId = firebaseUser.uid) ?: return@launch

            val result = if (user.isVendor) {
                // For vendors, we already created the account in createAccountAndSendEmailVerification
                // Just update the state to pending approval
                _verificationState.update {
                    it.copy(isPendingApproval = true)
                }
                AuthResult.Success(user)
            } else {
                repository.createFinalUser(user)
            }

            if (result is AuthResult.Success) {
                _currentUser.value = result.data
                _uiState.value = AuthUiState.Success(result.data)
            }
        }
    }
    /**
     * Finalize user creation after verification
     * This is called from the Signup screen after verification is complete
     */
    /**
     * Finalize user creation after verification
     */
    fun finalizeUserCreation() {
        viewModelScope.launch {
            val user = tempUserData ?: return@launch
            val password = tempPassword ?: return@launch
            val vendorData = tempVendorData  // May be null for customers

            _uiState.value = AuthUiState.Loading

            try {
                // Create Firebase Auth account
                val authResult = FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(user.email, password)
                    .await()

                val firebaseUser = authResult.user
                    ?: throw Exception("Account creation failed")

                // Send email verification (optional - can skip if already verified)
                // firebaseUser.sendEmailVerification().await()

                val newUser = user.copy(userId = firebaseUser.uid)

                if (vendorData != null || user.isVendor) {
                    // ===== VENDOR SIGNUP =====
                    val finalVendorData = vendorData?.copy(
                        vendorId = firebaseUser.uid,
                        userId = firebaseUser.uid
                    ) ?: VendorProfile(
                        vendorId = firebaseUser.uid,
                        userId = firebaseUser.uid,
                        businessName = user.name,
                        category = "",
                        address = user.address,
                        phone = user.phone,
                        email = user.email,
                        status = "pending"  // Requires admin approval
                    )

                    // Save to Firestore
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()

                    FirebaseFirestore.getInstance()
                        .collection("vendorProfiles")
                        .document(firebaseUser.uid)
                        .set(finalVendorData)
                        .await()

                    // Set pending approval state
                    _verificationState.update {
                        it.copy(
                            isPendingApproval = true,
                            currentStep = VerificationStep.COMPLETED
                        )
                    }
                    _currentUser.value = newUser
                    _uiState.value = AuthUiState.Success(newUser)

                } else {
                    // ===== CUSTOMER SIGNUP =====
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()

                    _verificationState.update {
                        it.copy(currentStep = VerificationStep.COMPLETED)
                    }
                    _currentUser.value = newUser
                    _uiState.value = AuthUiState.Success(newUser)
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "finalizeUserCreation failed", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Account creation failed")
            }
        }
    }
    /**
     * Check if user is verified and ready for final creation
     */
    fun isVerificationComplete(): Boolean {
        val state = _verificationState.value
        return when (state.selectedMethod) {
            VerificationMethod.PHONE -> state.isPhoneVerified
            VerificationMethod.EMAIL -> state.isEmailVerified
            else -> false
        }
    }

    /**
     * Sign up with email (legacy method - use initiateSignup + finalizeUserCreation instead)
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
     * Set verification error (for UI display)
     */
    fun setVerificationError(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }

    /**
     * Sign in with email
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
            _verificationState.value = VerificationState()
            tempUserData = null
            tempPassword = null
            tempVendorData = null
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
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        _isLoading.value = true

        val result = signInWithGoogleUseCase(idToken)

        if (result is AuthResult.Success) {
            _currentUser.value = result.data
        }

        _isLoading.value = false
        return result
    }

    /**
     * Send verification email using Firebase
     */
    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Email sent successfully
                _verificationState.update {
                    it.copy(emailVerificationSent = true)
                }
            } else {
                _uiState.value = AuthUiState.Error("Failed to send verification email.")
            }
        }
    }





    /**
     * Check vendor approval status
     */
    fun checkVendorApprovalStatus(vendorId: String) {
        viewModelScope.launch {
            repository.checkBusinessApprovalStatus(vendorId).collect { status ->
                if (status == "approved") {
                    _verificationState.update {
                        it.copy(isPendingApproval = false)
                    }
                    getCurrentUser()
                }
            }
        }
    }

    /**
     * Clear all temp data (call after successful signup)
     */
    fun clearTempData() {
        tempUserData = null
        tempPassword = null
        tempVendorData = null
        _verificationState.value = VerificationState()
    }
}