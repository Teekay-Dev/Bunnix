package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.profile.VendorProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _vendorProfile = MutableStateFlow<VendorProfileData?>(null)
    val vendorProfile: StateFlow<VendorProfileData?> = _vendorProfile.asStateFlow()

    private val _bankDetails = MutableStateFlow<BankDetails?>(null)
    val bankDetails: StateFlow<BankDetails?> = _bankDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadVendorProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Get user basic info
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                // Get vendor profile
                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(userId)
                    .get()
                    .await()

                _vendorProfile.value = VendorProfileData(
                    name = userDoc.getString("name") ?: "",
                    email = userDoc.getString("email") ?: "",
                    imageUrl = userDoc.getString("profilePicUrl") ?: "",
                    businessName = vendorDoc.getString("businessName") ?: "",
                    phone = userDoc.getString("phone") ?: ""
                )

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBankDetails() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(userId)
                    .get()
                    .await()

                _bankDetails.value = BankDetails(
                    bankName = vendorDoc.getString("bankName") ?: "",
                    accountNumber = vendorDoc.getString("accountNumber") ?: "",
                    accountName = vendorDoc.getString("accountName") ?: "",
                    alternativePayment = vendorDoc.getString("alternativePayment") ?: ""
                )

            } catch (e: Exception) {
                _error.value = "Failed to load bank details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBusinessProfile(
        businessName: String,
        description: String,
        address: String,
        phone: String,
        category: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Update vendor profile
                firestore.collection("vendorProfiles")
                    .document(userId)
                    .update(
                        mapOf(
                            "businessName" to businessName,
                            "description" to description,
                            "address" to address,
                            "category" to category,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                // Update user phone
                firestore.collection("users")
                    .document(userId)
                    .update("phone", phone)
                    .await()

                _successMessage.value = "Profile updated successfully"
                loadVendorProfile() // Reload

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBankDetails(
        bankName: String,
        accountNumber: String,
        accountName: String,
        alternativePayment: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                firestore.collection("vendorProfiles")
                    .document(userId)
                    .update(
                        mapOf(
                            "bankName" to bankName,
                            "accountNumber" to accountNumber,
                            "accountName" to accountName,
                            "alternativePayment" to alternativePayment,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                _successMessage.value = "Payment details updated successfully"
                loadBankDetails() // Reload

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update payment details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePhoto(imageUrl: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                firestore.collection("users")
                    .document(userId)
                    .update("profilePicUrl", imageUrl)
                    .await()

                _successMessage.value = "Profile photo updated"
                loadVendorProfile()

            } catch (e: Exception) {
                _error.value = "Failed to update photo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}

// Data Class
data class BankDetails(
    val bankName: String,
    val accountNumber: String,
    val accountName: String,
    val alternativePayment: String
)