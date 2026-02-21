package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalSales: Double = 12450.0,
    val totalOrders: Int = 156,
    val totalBookings: Int = 24,
    val totalCustomers: Int = 89
) {
    fun isEmpty() {
        TODO("Not yet implemented")
    }
}

data class RecentOrder(
    val orderNumber: String,
    val customerName: String,
    val status: String,
    val price: String,
    val itemCount: String
)

data class DashboardUiState(
    val isLoading: Boolean = false,
    val balance: Double = 2450.0,
    val stats: DashboardStats = DashboardStats(),
    val recentOrders: List<RecentOrder> = emptyList(),
    val weeklyPerformance: List<Double> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorDashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Simulate network delay
                delay(1000)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        balance = 2450.0,
                        stats = DashboardStats(),
                        recentOrders = listOf(
                            RecentOrder("#AB12C", "John Doe", "pending", "$45.99", "2 items"),
                            RecentOrder("#AB12D", "Jane Smith", "processing", "$129.99", "1 items"),
                            RecentOrder("#AB12E", "Mike Chen", "completed", "$78.50", "3 items")
                        ),
                        weeklyPerformance = listOf(12000.0, 19000.0, 15000.0, 25000.0, 22000.0, 30000.0, 28000.0)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refreshData() {
        loadData()
    }
}