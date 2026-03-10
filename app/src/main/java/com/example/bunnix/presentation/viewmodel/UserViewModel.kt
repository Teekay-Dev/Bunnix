package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Add vendor profile state for vendor mode
    private val _vendorProfile = MutableStateFlow<VendorProfile?>(null)
    val vendorProfile: StateFlow<VendorProfile?> = _vendorProfile

    // Track if current user is vendor
    private val _isVendor = MutableStateFlow(false)
    val isVendor: StateFlow<Boolean> = _isVendor

    init {
        loadUserOrVendor()
    }

    private fun loadUserOrVendor() {
        val uid = auth.currentUser?.uid ?: return

        // Check users collection first
        db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    _user.value = user
                    _isVendor.value = user?.isVendor ?: false
                    _vendorProfile.value = null // Clear vendor profile
                } else {
                    // Not in users collection, check vendorProfiles
                    loadVendorProfile(uid)
                }
            }
    }

    // Add these methods to UserViewModel

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

    private fun loadVendorProfile(uid: String) {
        db.collection("vendorProfiles")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val vendor = snapshot.toObject(VendorProfile::class.java)
                    _vendorProfile.value = vendor
                    _isVendor.value = true

                    // Convert vendor to User object for UI compatibility
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

    // Get full vendor profile (call this when you need complete vendor data)
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
    }
}