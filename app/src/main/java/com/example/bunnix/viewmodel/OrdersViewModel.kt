package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.vendorUI.screens.vendor.orders.ProductOrder
import com.example.bunnix.vendorUI.screens.vendor.orders.ServiceBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
class OrdersViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _productOrders = MutableStateFlow<List<ProductOrder>>(emptyList())
    val productOrders: StateFlow<List<ProductOrder>> = _productOrders.asStateFlow()

    private val _serviceBookings = MutableStateFlow<List<ServiceBooking>>(emptyList())
    val serviceBookings: StateFlow<List<ServiceBooking>> = _serviceBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadProductOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                val ordersSnapshot = firestore.collection("orders")
                    .whereEqualTo("vendorId", vendorId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val orders = mutableListOf<ProductOrder>()

                for (doc in ordersSnapshot.documents) {
                    try {
                        val userId = doc.getString("userId") ?: continue

                        val userDoc = firestore.collection("users")
                            .document(userId)
                            .get()
                            .await()

                        val customerName = userDoc.getString("name") ?: "Unknown Customer"
                        val customerImageUrl = userDoc.getString("profilePicUrl") ?: ""

                        val items = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val itemNames = items.mapNotNull { it["productName"] as? String }

                        val createdAt = doc.getTimestamp("createdAt")
                        val timeAgo = createdAt?.let { calculateTimeAgo(it.toDate()) } ?: "Just now"

                        orders.add(
                            ProductOrder(
                                orderId = doc.id,
                                orderNumber = doc.getString("orderNumber") ?: "#${doc.id.take(6).uppercase()}",
                                customerName = customerName,
                                customerImageUrl = customerImageUrl,
                                timeAgo = timeAgo,
                                items = itemNames,
                                amount = doc.getDouble("totalAmount") ?: 0.0,
                                status = doc.getString("status") ?: "pending"
                            )
                        )
                    } catch (e: Exception) {
                        continue
                    }
                }

                _productOrders.value = orders

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load orders"
                _productOrders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadServiceBookings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                val bookingsSnapshot = firestore.collection("bookings")
                    .whereEqualTo("vendorId", vendorId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val bookings = mutableListOf<ServiceBooking>()

                for (doc in bookingsSnapshot.documents) {
                    try {
                        val userId = doc.getString("userId") ?: continue

                        val userDoc = firestore.collection("users")
                            .document(userId)
                            .get()
                            .await()

                        val customerName = userDoc.getString("name") ?: "Unknown Customer"
                        val customerImageUrl = userDoc.getString("profilePicUrl") ?: ""

                        val serviceId = doc.getString("serviceId") ?: ""
                        val serviceDoc = firestore.collection("services")
                            .document(serviceId)
                            .get()
                            .await()

                        val serviceName = serviceDoc.getString("name") ?: "Unknown Service"

                        val bookingDate = doc.getTimestamp("bookingDate")
                        val formattedDate = bookingDate?.let {
                            SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(it.toDate())
                        } ?: "Date not set"

                        bookings.add(
                            ServiceBooking(
                                bookingId = doc.id,
                                bookingNumber = doc.getString("bookingNumber") ?: "#${doc.id.take(6).uppercase()}",
                                customerName = customerName,
                                customerImageUrl = customerImageUrl,
                                bookingDate = formattedDate,
                                serviceName = serviceName,
                                amount = doc.getDouble("totalAmount") ?: 0.0,
                                status = doc.getString("status") ?: "pending"
                            )
                        )
                    } catch (e: Exception) {
                        continue
                    }
                }

                _serviceBookings.value = bookings

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load bookings"
                _serviceBookings.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ ACCEPT ORDER - FULLY IMPLEMENTED
    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Update order status
                firestore.collection("orders")
                    .document(orderId)
                    .update(
                        mapOf(
                            "status" to "processing",
                            "acceptedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                // Send notification to customer
                val orderDoc = firestore.collection("orders").document(orderId).get().await()
                val customerId = orderDoc.getString("userId") ?: ""

                sendNotificationToCustomer(
                    customerId = customerId,
                    title = "Order Accepted",
                    message = "Your order has been accepted and is being processed!"
                )

                _successMessage.value = "Order accepted successfully!"
                loadProductOrders()

            } catch (e: Exception) {
                _error.value = "Failed to accept order: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ DECLINE ORDER - FULLY IMPLEMENTED
    fun declineOrder(orderId: String, reason: String = "Vendor declined") {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                firestore.collection("orders")
                    .document(orderId)
                    .update(
                        mapOf(
                            "status" to "declined",
                            "declineReason" to reason,
                            "declinedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                // Notify customer
                val orderDoc = firestore.collection("orders").document(orderId).get().await()
                val customerId = orderDoc.getString("userId") ?: ""

                sendNotificationToCustomer(
                    customerId = customerId,
                    title = "Order Declined",
                    message = "Unfortunately, your order could not be processed."
                )

                _successMessage.value = "Order declined"
                loadProductOrders()

            } catch (e: Exception) {
                _error.value = "Failed to decline order"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ ACCEPT BOOKING - FULLY IMPLEMENTED
    fun acceptBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                firestore.collection("bookings")
                    .document(bookingId)
                    .update(
                        mapOf(
                            "status" to "confirmed",
                            "confirmedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                val bookingDoc = firestore.collection("bookings").document(bookingId).get().await()
                val customerId = bookingDoc.getString("userId") ?: ""

                sendNotificationToCustomer(
                    customerId = customerId,
                    title = "Booking Confirmed",
                    message = "Your service booking has been confirmed!"
                )

                _successMessage.value = "Booking accepted successfully!"
                loadServiceBookings()

            } catch (e: Exception) {
                _error.value = "Failed to accept booking"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ DECLINE BOOKING - FULLY IMPLEMENTED
    fun declineBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                firestore.collection("bookings")
                    .document(bookingId)
                    .update(
                        mapOf(
                            "status" to "declined",
                            "declinedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                val bookingDoc = firestore.collection("bookings").document(bookingId).get().await()
                val customerId = bookingDoc.getString("userId") ?: ""

                sendNotificationToCustomer(
                    customerId = customerId,
                    title = "Booking Declined",
                    message = "Your booking request could not be accepted."
                )

                _successMessage.value = "Booking declined"
                loadServiceBookings()

            } catch (e: Exception) {
                _error.value = "Failed to decline booking"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ UPDATE ORDER STATUS (Processing → Shipped → Delivered)
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                firestore.collection("orders")
                    .document(orderId)
                    .update("status", newStatus)
                    .await()

                _successMessage.value = "Order status updated to $newStatus"
                loadProductOrders()
            } catch (e: Exception) {
                _error.value = "Failed to update status"
            }
        }
    }

    // ✅ SEND NOTIFICATION TO CUSTOMER
    private suspend fun sendNotificationToCustomer(
        customerId: String,
        title: String,
        message: String
    ) {
        try {
            val notificationData = hashMapOf(
                "userId" to customerId,
                "title" to title,
                "message" to message,
                "type" to "order",
                "isRead" to false,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("notifications")
                .add(notificationData)
                .await()
        } catch (e: Exception) {
            // Silent fail
        }
    }

    private fun calculateTimeAgo(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time

        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            minutes > 0 -> "$minutes min${if (minutes > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }

    fun refresh() {
        loadProductOrders()
        loadServiceBookings()
    }
}