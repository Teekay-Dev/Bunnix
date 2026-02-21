package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.usecase.vendor.GetVendorProfileUseCase
import com.example.bunnix.domain.usecase.vendor.UpdateVendorAvailabilityUseCase
import com.example.bunnix.domain.usecase.vendor.UpdateVendorProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: VendorProfile? = null,
    val isAvailable: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getVendorProfileUseCase: GetVendorProfileUseCase,
    private val updateVendorProfileUseCase: UpdateVendorProfileUseCase,
    private val updateVendorAvailabilityUseCase: UpdateVendorAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile(vendorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getVendorProfileUseCase(vendorId)
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        profile = profile,
                        isAvailable = profile.isAvailable
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateAvailability(vendorId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            updateVendorAvailabilityUseCase(vendorId, isAvailable)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isAvailable = isAvailable)
                }
        }
    }

    fun updateProfile(vendorId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            updateVendorProfileUseCase(vendorId, updates)
                .onSuccess {
                    loadProfile(vendorId)
                }
        }
    }
}