package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.dashboard.DashboardOrder
import com.example.bunnix.vendorUI.screens.vendor.dashboard.DashboardStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class VendorDashboardViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats.asStateFlow()

    private val _recentOrders = MutableStateFlow<List<DashboardOrder>>(emptyList())
    val recentOrders: StateFlow<List<DashboardOrder>> = _recentOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Load vendor stats
                loadVendorStats(userId)

                // Load recent orders
                loadRecentOrders(userId)

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load dashboard data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadVendorStats(vendorId: String) {
        try {
            // Get vendor profile
            val vendorProfile = firestore.collection("vendorProfiles")
                .document(vendorId)
                .get()
                .await()

            val availableBalance = vendorProfile.getDouble("totalRevenue") ?: 0.0

            // Get all completed orders
            val completedOrders = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("status", "completed")
                .get()
                .await()

            var totalSales = 0.0
            completedOrders.documents.forEach { doc ->
                totalSales += doc.getDouble("totalAmount") ?: 0.0
            }

            // Get all orders count
            val allOrders = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()

            val uniqueCustomers = mutableSetOf<String>()
            allOrders.documents.forEach { doc ->
                doc.getString("userId")?.let { uniqueCustomers.add(it) }
            }

            // Get bookings
            val bookings = firestore.collection("bookings")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()

            _dashboardStats.value = DashboardStats(
                availableBalance = availableBalance,
                totalSales = totalSales,
                totalOrders = allOrders.size(),
                bookings = bookings.size(),
                customers = uniqueCustomers.size
            )

        } catch (e: Exception) {
            _dashboardStats.value = DashboardStats()
        }
    }

    private suspend fun loadRecentOrders(vendorId: String) {
        try {
            val ordersSnapshot = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .await()

            val orders = mutableListOf<DashboardOrder>()

            for (doc in ordersSnapshot.documents) {
                try {
                    val userId = doc.getString("userId") ?: continue

                    // Get customer name
                    val userDoc = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val customerName = userDoc.getString("name") ?: "Unknown Customer"

                    // Get items count
                    val items = doc.get("items") as? List<*>
                    val itemCount = items?.size ?: 0

                    orders.add(
                        DashboardOrder(
                            orderId = doc.id,
                            orderNumber = doc.getString("orderNumber") ?: "#${doc.id.take(6).uppercase()}",
                            customerName = customerName,
                            amount = doc.getDouble("totalAmount") ?: 0.0,
                            itemCount = itemCount,
                            status = doc.getString("status") ?: "pending"
                        )
                    )
                } catch (e: Exception) {
                    // Skip this order on error
                    continue
                }
            }

            _recentOrders.value = orders

        } catch (e: Exception) {
            _recentOrders.value = emptyList()
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}