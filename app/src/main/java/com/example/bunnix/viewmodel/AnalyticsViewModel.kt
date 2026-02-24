package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val weeklyData: List<Double> = emptyList(),
    val topProducts: List<TopProduct> = emptyList(),
    val error: String? = null,
    val vendorId: String = "",
    val period: String = "week"
)

data class TopProduct(
    val id: String,
    val name: String,
    val sales: Int,
    val revenue: Double
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        // Initialize with default values or load saved state
        loadAnalytics("default_vendor", "week")
    }

    fun loadAnalytics(vendorId: String, period: String = "week") {
        viewModelScope.launch {
            // Use vendorId and period in state
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                vendorId = vendorId,
                period = period
            )

            // Simulate different data based on period
            val weeklyData = when (period.lowercase()) {
                "day" -> listOf(12000.0, 15000.0, 8000.0, 20000.0)
                "week" -> listOf(12000.0, 19000.0, 15000.0, 25000.0, 22000.0, 30000.0, 28000.0)
                "month" -> listOf(45000.0, 52000.0, 48000.0, 61000.0)
                "year" -> listOf(520000.0, 480000.0, 610000.0, 720000.0)
                else -> listOf(12000.0, 19000.0, 15000.0, 25000.0, 22000.0, 30000.0, 28000.0)
            }

            // Calculate different revenue based on vendorId length (just to use it)
            val baseRevenue = 245000.0
            val adjustedRevenue = baseRevenue + (vendorId.length * 1000)

            _uiState.value = AnalyticsUiState(
                isLoading = false,
                vendorId = vendorId,
                period = period,
                totalRevenue = adjustedRevenue,
                totalOrders = 45 + vendorId.length,
                weeklyData = weeklyData,
                topProducts = listOf(
                    TopProduct("1", "iPhone 15 Pro", 12, 14400000.0),
                    TopProduct("2", "Samsung Galaxy S24", 8, 7600000.0),
                    TopProduct("3", "AirPods Pro 2", 25, 6250000.0)
                )
            )
        }
    }

    fun refreshAnalytics() {
        // Use current state values to reload
        val currentState = _uiState.value
        loadAnalytics(currentState.vendorId, currentState.period)
    }

    fun updatePeriod(newPeriod: String) {
        val currentState = _uiState.value
        loadAnalytics(currentState.vendorId, newPeriod)
    }
}