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

    private val _businessName = MutableStateFlow<String?>(null)
    val businessName: StateFlow<String?> = _businessName.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // ✅ BUG 5 FIX: Proper error handling for auth
                val userId = auth.currentUser?.uid ?: run {
                    _isLoading.value = false
                    _error.value = "Session expired. Please log in again."
                    return@launch
                }

                // Load vendor stats
                loadVendorStats(userId)

                // Load recent orders
                loadRecentOrders(userId)

                // Load business name and verification status
                loadVendorProfile(userId)

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load dashboard data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadVendorProfile(vendorId: String) {
        try {
            val vendorDoc = firestore.collection("vendorProfiles")
                .document(vendorId)
                .get()
                .await()

            _businessName.value = vendorDoc.getString("businessName") ?: "My Business"
            _isVerified.value = vendorDoc.getBoolean("isVerified") ?: false
        } catch (e: Exception) {
            _businessName.value = "My Business"
            _isVerified.value = false
        }
    }

    private suspend fun loadVendorStats(vendorId: String) {
        try {
            // Get vendor profile
            val vendorProfile = firestore.collection("vendorProfiles")
                .document(vendorId)
                .get()
                .await()

            val totalRevenue = vendorProfile.getDouble("totalRevenue") ?: 0.0

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

            // Get total order count
            val totalOrders = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()
                .size()

            // Get bookings count
            val bookings = firestore.collection("bookings")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()
                .size()

            // Get unique customers count
            val customerIds = mutableSetOf<String>()
            val allOrders = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()

            allOrders.documents.forEach { doc ->
                doc.getString("customerId")?.let { customerIds.add(it) }
            }

            _dashboardStats.value = DashboardStats(
                totalSales = totalSales,
                totalOrders = totalOrders,
                bookings = bookings,
                customers = customerIds.size
            )

        } catch (e: Exception) {
            _error.value = "Failed to load stats: ${e.message}"
        }
    }

    private suspend fun loadRecentOrders(vendorId: String) {
        try {
            val ordersSnapshot = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()

            val orders = ordersSnapshot.documents.mapNotNull { doc ->
                try {
                    DashboardOrder(
                        orderId = doc.id,
                        orderNumber = doc.getString("orderNumber") ?: "#${doc.id.take(8)}",
                        customerName = doc.getString("customerName") ?: "Unknown Customer",
                        amount = doc.getDouble("totalAmount") ?: 0.0,
                        itemCount = (doc.get("items") as? List<*>)?.size ?: 1,
                        status = doc.getString("status") ?: "pending"
                    )
                } catch (e: Exception) {
                    null
                }
            }

            _recentOrders.value = orders

        } catch (e: Exception) {
            _error.value = "Failed to load orders: ${e.message}"
        }
    }

    fun shouldShowVerificationPrompt(): Boolean {
        // Always returns true if not verified
        // The screen handles showing it only once per session
        return !_isVerified.value
    }

    fun markVerificationPromptSeen() {
        // Do nothing - handled by global flag in screen
    }
}