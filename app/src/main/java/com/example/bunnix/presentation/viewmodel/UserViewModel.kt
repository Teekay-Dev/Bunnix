package com.example.bunnix.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _vendorProfile = MutableStateFlow<VendorProfile?>(null)
    val vendorProfile: StateFlow<VendorProfile?> = _vendorProfile

    private val _isVendor = MutableStateFlow(false)
    val isVendor: StateFlow<Boolean> = _isVendor

    // Google/Firebase Auth photo URL (from Google Sign-In)
    private val _authPhotoUrl = MutableStateFlow<String?>(null)
    val authPhotoUrl: StateFlow<String?> = _authPhotoUrl

    // Upload progress (0f = idle, 0f..1f = uploading)
    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    init {
        // Load Google photo URL from FirebaseAuth immediately
        _authPhotoUrl.value = auth.currentUser?.photoUrl?.toString()
        loadUserOrVendor()
    }

    private fun loadUserOrVendor() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    _user.value = user
                    _isVendor.value = user?.isVendor ?: false
                    _vendorProfile.value = null
                } else {
                    loadVendorProfile(uid)
                }
            }
    }

    private fun loadVendorProfile(uid: String) {
        db.collection("vendorProfiles")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val vendor = snapshot.toObject(VendorProfile::class.java)
                    _vendorProfile.value = vendor
                    _isVendor.value = true
                    _user.value = User(
                        userId = vendor?.vendorId ?: uid,
                        name = vendor?.businessName ?: "",
                        email = vendor?.email ?: "",
                        phone = vendor?.phone ?: "",
                        isVendor = true,
                        address = vendor?.address ?: ""
                    )
                }
            }
    }

    /**
     * Uploads a new profile photo to Firebase Storage,
     * then saves the download URL to the users Firestore document.
     */
    /**
     * Uploads a new profile photo to Firebase Storage,
     * then saves the download URL to the correct Firestore document (Users OR Vendors).
     */
    fun uploadProfilePhoto(context: Context, imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                _uploadProgress.value = 0.01f // Show progress started

                // 1. Read bytes from URI
                val inputStream = context.contentResolver.openInputStream(imageUri) ?: return@launch
                val bytes = inputStream.readBytes()
                inputStream.close()

                // 2. Upload to Storage
                val storageRef = storage.reference
                    .child("profile_photos/$uid/profile.jpg")

                val uploadTask = storageRef.putBytes(bytes)

                uploadTask.addOnProgressListener { snapshot ->
                    val progress = snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount.toFloat()
                    _uploadProgress.value = progress
                }

                uploadTask.await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // 3. Update Firestore - Check if Vendor or Customer
                if (_isVendor.value) {
                    // ✅ VENDOR LOGIC: Update 'vendorProfiles'
                    db.collection("vendorProfiles")
                        .document(uid)
                        .update("profilePhotoUrl", downloadUrl) // Check your VendorProfile model for field name
                        .await()

                    // Update local Vendor state
                    _vendorProfile.value = _vendorProfile.value?.copy(profilePhotoUrl = downloadUrl)

                    // Also update the combined User state if used in UI
                    _user.value = _user.value?.copy(profilePicUrl = downloadUrl)

                } else {
                    // ✅ CUSTOMER LOGIC: Update 'users'
                    db.collection("users")
                        .document(uid)
                        .update("profilePicUrl", downloadUrl)
                        .await()

                    // Update local User state
                    _user.value = _user.value?.copy(profilePicUrl = downloadUrl)
                }

                _uploadProgress.value = 0f

            } catch (e: Exception) {
                _uploadProgress.value = 0f
                // Handle error (e.g., print stack trace)
                e.printStackTrace()
            }
        }
    }

    fun updateUserProfile(name: String, email: String, phone: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update(
                        mapOf(
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "lastActive" to Timestamp.now()
                        )
                    )
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateVendorProfile(
        businessName: String,
        email: String,
        phone: String,
        address: String,
        description: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val updates = mutableMapOf(
                    "businessName" to businessName,
                    "email" to email,
                    "phone" to phone,
                    "address" to address,
                    "updatedAt" to Timestamp.now()
                )
                description?.let { updates["description"] = it }

                db.collection("vendorProfiles")
                    .document(uid)
                    .update(updates as Map<String, Any>)
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshVendorProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("vendorProfiles")
                    .document(uid)
                    .get()
                    .await()

                if (doc.exists()) {
                    _vendorProfile.value = doc.toObject(VendorProfile::class.java)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _vendorProfile.value = null
        _isVendor.value = false
        _authPhotoUrl.value = null
    }
}
