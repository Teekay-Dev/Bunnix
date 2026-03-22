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

data class VendorProfileData(
    val vendorId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val businessName: String = "",
    val description: String = "",
    val category: String = "",
    val address: String = "",
    val imageUrl: String = "",
    val isVerified: Boolean = false
)

data class BankDetails(
    val bankName: String = "",
    val accountNumber: String = "",
    val accountName: String = "",
    val alternativePayment: String = ""
)

@HiltViewModel
class VendorProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun loadVendorProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Load user data (for email, phone, profile pic)
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                // ✅ Load vendor profile data from "vendors" collection
                val vendorDoc = firestore.collection("vendors")
                    .document(userId)
                    .get()
                    .await()

                _vendorProfile.value = VendorProfileData(
                    vendorId = userId,
                    name = userDoc.getString("name") ?: "",
                    email = userDoc.getString("email") ?: "",
                    phone = userDoc.getString("phone") ?: "",
                    imageUrl = userDoc.getString("profilePicUrl") ?: "",
                    businessName = vendorDoc.getString("businessName") ?: "",
                    description = vendorDoc.getString("description") ?: "",
                    category = vendorDoc.getString("category") ?: "",
                    address = vendorDoc.getString("address") ?: "",
                    isVerified = vendorDoc.getBoolean("isVerified") ?: false
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
                val userId = auth.currentUser?.uid ?: return@launch

                // ✅ Load from "vendors" collection
                val vendorDoc = firestore.collection("vendors")
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
                _error.value = e.message
            }
        }
    }

    fun updateBusinessProfile(
        businessName: String,
        description: String,
        category: String,
        address: String,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // ✅ Update vendor profile in "vendors" collection
                val vendorUpdate = hashMapOf(
                    "businessName" to businessName,
                    "description" to description,
                    "category" to category,
                    "address" to address,
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("vendors")
                    .document(userId)
                    .update(vendorUpdate as Map<String, Any>)
                    .await()

                // Update user phone in "users" collection
                firestore.collection("users")
                    .document(userId)
                    .update("phone", phone)
                    .await()

                _successMessage.value = "Profile updated successfully"
                loadVendorProfile()

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

                val bankUpdate = hashMapOf(
                    "bankName" to bankName,
                    "accountNumber" to accountNumber,
                    "accountName" to accountName,
                    "alternativePayment" to alternativePayment,
                    "updatedAt" to System.currentTimeMillis()
                )

                // ✅ Update in "vendors" collection
                firestore.collection("vendors")
                    .document(userId)
                    .update(bankUpdate as Map<String, Any>)
                    .await()

                _successMessage.value = "Bank details updated successfully"
                loadBankDetails()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update bank details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfilePhoto(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uploadProgress.value = 0f
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Create storage reference
                val storageRef = storage.reference
                    .child("profiles/$userId/profile_${System.currentTimeMillis()}.jpg")

                // Upload image
                val uploadTask = storageRef.putFile(imageUri)

                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                    _uploadProgress.value = progress / 100f
                }

                uploadTask.await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update Firestore (users collection for profile pic)
                firestore.collection("users")
                    .document(userId)
                    .update("profilePicUrl", downloadUrl)
                    .await()

                _successMessage.value = "Profile photo updated successfully"
                loadVendorProfile()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to upload photo"
            } finally {
                _isLoading.value = false
                _uploadProgress.value = 0f
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