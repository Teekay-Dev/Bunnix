package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _weeklyData = MutableStateFlow<List<DailySales>>(emptyList())
    val weeklyData: StateFlow<List<DailySales>> = _weeklyData.asStateFlow()

    private val _monthlyRevenue = MutableStateFlow(0.0)
    val monthlyRevenue: StateFlow<Double> = _monthlyRevenue.asStateFlow()

    private val _topProducts = MutableStateFlow<List<TopProduct>>(emptyList())
    val topProducts: StateFlow<List<TopProduct>> = _topProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAnalytics() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                loadWeeklyData(vendorId)
                loadMonthlyRevenue(vendorId)
                loadTopProducts(vendorId)

            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadWeeklyData(vendorId: String) {
        val calendar = Calendar.getInstance()
        val dailySales = mutableListOf<DailySales>()

        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, if (i == 6) 0 else 1)
            val dayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }.time

            val dayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }.time

            val orders = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("status", "completed")
                .whereGreaterThanOrEqualTo("createdAt", dayStart)
                .whereLessThanOrEqualTo("createdAt", dayEnd)
                .get()
                .await()

            val revenue = orders.documents.sumOf { it.getDouble("totalAmount") ?: 0.0 }

            dailySales.add(
                DailySales(
                    day = SimpleDateFormat("EEE", Locale.getDefault()).format(dayStart),
                    revenue = revenue
                )
            )
        }

        _weeklyData.value = dailySales
    }

    private suspend fun loadMonthlyRevenue(vendorId: String) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = calendar.time

        val orders = firestore.collection("orders")
            .whereEqualTo("vendorId", vendorId)
            .whereEqualTo("status", "completed")
            .whereGreaterThanOrEqualTo("createdAt", monthStart)
            .get()
            .await()

        _monthlyRevenue.value = orders.documents.sumOf { it.getDouble("totalAmount") ?: 0.0 }
    }

    private suspend fun loadTopProducts(vendorId: String) {
        // Get all completed orders
        val orders = firestore.collection("orders")
            .whereEqualTo("vendorId", vendorId)
            .whereEqualTo("status", "completed")
            .get()
            .await()

        val productSales = mutableMapOf<String, Int>()

        orders.documents.forEach { doc ->
            val items = doc.get("items") as? List<Map<String, Any>> ?: return@forEach
            items.forEach { item ->
                val productId = item["productId"] as? String ?: return@forEach
                productSales[productId] = (productSales[productId] ?: 0) + 1
            }
        }

        val topProductsList = productSales.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapNotNull { entry ->
                val productDoc = firestore.collection("products")
                    .document(entry.key)
                    .get()
                    .await()

                TopProduct(
                    name = productDoc.getString("name") ?: "Unknown",
                    sales = entry.value
                )
            }

        _topProducts.value = topProductsList
    }
}

data class DailySales(
    val day: String,
    val revenue: Double
)

data class TopProduct(
    val name: String,
    val sales: Int
)