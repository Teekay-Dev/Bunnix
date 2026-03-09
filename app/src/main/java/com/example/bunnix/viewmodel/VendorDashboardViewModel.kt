package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.dashboard.DashboardOrder
import com.example.bunnix.vendorUI.screens.vendor.dashboard.DashboardStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.Context
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class VendorDashboardViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats.asStateFlow()

    private val _recentOrders = MutableStateFlow<List<DashboardOrder>>(emptyList())
    val recentOrders: StateFlow<List<DashboardOrder>> = _recentOrders.asStateFlow()

    private val _businessName = MutableStateFlow<String?>(null)
    val businessName: StateFlow<String?> = _businessName.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: return@launch

                // Load business name and verification status
                loadBusinessInfo(userId)

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

    private suspend fun loadBusinessInfo(vendorId: String) {
        try {
            val vendorProfile = firestore.collection("vendorProfiles")
                .document(vendorId)
                .get()
                .await()

            _businessName.value = vendorProfile.getString("businessName") ?: "My Business"
            _isVerified.value = vendorProfile.getBoolean("isVerified") ?: false

        } catch (e: Exception) {
            _businessName.value = "My Business"
            _isVerified.value = false
        }
    }

    private suspend fun loadVendorStats(vendorId: String) {
        try {
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

                    val userDoc = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val customerName = userDoc.getString("name") ?: "Unknown Customer"

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
                    continue
                }
            }

            _recentOrders.value = orders

        } catch (e: Exception) {
            _recentOrders.value = emptyList()
        }
    }

    // Update this function:

    fun shouldShowVerificationPrompt(): Boolean {
        // Always returns true if not verified
        // The screen handles showing it only once per session
        return !_isVerified.value
    }

    fun markVerificationPromptSeen() {
        // Do nothing - handled by global flag in screen
    }

    fun refresh() {
        loadDashboardData()
    }
}