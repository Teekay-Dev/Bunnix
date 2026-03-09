package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun submitVerificationRequest(
        fullName: String,
        phoneNumber: String,
        businessRegistrationNumber: String,
        taxIdentificationNumber: String,
        businessAddress: String,
        businessLicenseUri: Uri,
        governmentIdUri: Uri,
        proofOfAddressUri: Uri
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uploadProgress.value = 0f
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                // Upload documents
                val businessLicenseUrl = uploadDocument(businessLicenseUri, vendorId, "business_license")
                _uploadProgress.value = 0.33f

                val governmentIdUrl = uploadDocument(governmentIdUri, vendorId, "government_id")
                _uploadProgress.value = 0.66f

                val proofOfAddressUrl = uploadDocument(proofOfAddressUri, vendorId, "proof_of_address")
                _uploadProgress.value = 1f

                // Create verification request document
                val verificationData = hashMapOf(
                    "vendorId" to vendorId,
                    "fullName" to fullName,
                    "phoneNumber" to phoneNumber,
                    "businessRegistrationNumber" to businessRegistrationNumber,
                    "taxIdentificationNumber" to taxIdentificationNumber,
                    "businessAddress" to businessAddress,
                    "businessLicenseUrl" to businessLicenseUrl,
                    "governmentIdUrl" to governmentIdUrl,
                    "proofOfAddressUrl" to proofOfAddressUrl,
                    "status" to "pending",
                    "submittedAt" to System.currentTimeMillis(),
                    "reviewedAt" to null,
                    "reviewedBy" to null,
                    "rejectionReason" to null
                )

                firestore.collection("verificationRequests")
                    .document(vendorId)
                    .set(verificationData)
                    .await()

                // Update vendor profile with pending status
                firestore.collection("vendorProfiles")
                    .document(vendorId)
                    .update("verificationStatus", "pending")
                    .await()

                _successMessage.value = "Verification request submitted successfully! We'll review it within 2-3 business days."

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to submit verification request"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadDocument(uri: Uri, vendorId: String, documentType: String): String {
        val storageRef = storage.reference
            .child("verification/$vendorId/${documentType}_${System.currentTimeMillis()}.jpg")

        val uploadTask = storageRef.putFile(uri)
        uploadTask.await()

        return storageRef.downloadUrl.await().toString()
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}