package com.example.bunnix.frontend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.VendorRepository
import com.example.bunnix.frontend.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel  // ✅ ADD THIS
class VendorViewModel @Inject constructor(
    private val vendorRepository: VendorRepository
) : ViewModel() {

    private val _vendorProfile = MutableStateFlow<VendorProfile?>(null)
    val vendorProfile: StateFlow<VendorProfile?> = _vendorProfile

    // ✅ NEW: List of all vendors for HomeScreen
    private val _vendorList = MutableStateFlow<List<VendorProfile>>(emptyList())
    val vendorList: StateFlow<List<VendorProfile>> = _vendorList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ✅ NEW: Auto-load all vendors when ViewModel is created
    init {
        loadAllVendors()
    }

    // ✅ NEW: Fetch ALL vendors for HomeScreen
    fun loadAllVendors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = vendorRepository.getAllVendors()) {
                    is AuthResult.Success -> {
                        _vendorList.value = result.data ?: emptyList()
                        _error.value = null
                    }
                    is AuthResult.Error -> {
                        _error.value = result.message
                        _vendorList.value = emptyList()
                    }
                    else -> {
                        _vendorList.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _vendorList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ EXISTING: Fetch single vendor profile
    fun fetchVendor(vendorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = vendorRepository.getVendorProfile(vendorId)) {
                is AuthResult.Success -> {
                    _vendorProfile.value = result.data
                }
                is AuthResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    // ✅ NEW: Refresh vendor list
    fun refresh() {
        loadAllVendors()
    }

    // ✅ EXISTING: Register User Function
    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        businessName: String? = null,
        businessAddress: String? = null,
        isVendor: Boolean
    ): NetworkResult<String> {

        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            return NetworkResult.Error("Please fill all required fields")
        }

        if (password.length < 6) {
            return NetworkResult.Error("Password must be at least 6 characters")
        }

        if (password != confirmPassword) {
            return NetworkResult.Error("Passwords do not match")
        }

        if (isVendor) {
            if (businessName.isNullOrBlank() || businessAddress.isNullOrBlank()) {
                return NetworkResult.Error("Please fill business details")
            }
        }

        delay(1500)
        return NetworkResult.Success("Account Created Successfully")
    }
}