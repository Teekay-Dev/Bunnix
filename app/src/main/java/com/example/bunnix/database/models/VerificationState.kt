package com.example.bunnix.database.models

data class VerificationState(
    val currentStep: VerificationStep = VerificationStep.IDLE,
    val selectedMethod: VerificationMethod? = null,
    val isPhoneVerified: Boolean = false,
    val isEmailVerified: Boolean = false,
    val emailVerificationSent: Boolean = false,
    val isPendingApproval: Boolean = false
)

enum class VerificationStep {
    IDLE,           // Show signup form
    SELECT_METHOD,  // Show MethodSelectionScreen
    EMAIL_INSTRUCTIONS, // Show EmailInstructionScreen
    COMPLETED       // Finalize creation
}

enum class VerificationMethod {
    EMAIL
}