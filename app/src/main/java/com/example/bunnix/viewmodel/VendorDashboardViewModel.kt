package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.usecase.vendor.*
import com.example.bunnix.vendorUI.screens.vendor.dashboard.RecentOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val businessName: String = "",
    val isAvailable: Boolean = true,
    val availableBalance: Double = 2450.0,
    val totalSales: Double = 12450.0,
    val totalOrders: Int = 156,
    val totalBookings: Int = 24,
    val totalCustomers: Int = 89,
    val pendingVerifications: Int = 3,
    val unreadMessages: Int = 5,
    val weeklyPerformance: List<Double> = listOf(12000.0, 19000.0, 15000.0, 25000.0, 22000.0, 30000.0, 28000.0),
    val recentOrders: List<RecentOrder> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorDashboardViewModel @Inject constructor(
    private val getVendorProfileUseCase: GetVendorProfileUseCase,
    private val updateVendorAvailabilityUseCase: UpdateVendorAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun loadDashboard(vendorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getVendorProfileUseCase(vendorId)
                .onSuccess { profile ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            businessName = profile.businessName,
                            isAvailable = profile.isAvailable,
                            totalSales = profile.totalSales.toDouble(),
                            // Map other fields from profile
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun toggleAvailability() {
        val currentState = _uiState.value
        val newAvailability = !currentState.isAvailable

        viewModelScope.launch {
            // Get vendor ID from auth
            val vendorId = "current_vendor_id" // Replace with actual auth
            updateVendorAvailabilityUseCase(vendorId, newAvailability)
                .onSuccess {
                    _uiState.update { it.copy(isAvailable = newAvailability) }
                }
        }
    }
}