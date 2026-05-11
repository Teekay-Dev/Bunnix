package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// UI Models
data class ProductOrder(
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val customerImageUrl: String,
    val timeAgo: String,
    val items: List<String>,
    val amount: Double,
    val status: String
)

data class ServiceBooking(
    val bookingId: String,
    val bookingNumber: String,
    val customerName: String,
    val customerImageUrl: String,
    val bookingDate: String,
    val serviceName: String,
    val amount: Double,
    val status: String
)

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

    init {
        loadProductOrders()
        loadServiceBookings()
    }

    // ✅ REAL-TIME LISTENER FOR ORDERS
    fun loadProductOrders() {
        val vendorId = auth.currentUser?.uid ?: return

        firestore.collection("orders")
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val orders = snapshot.documents.mapNotNull { doc ->
                    try {
                        val items = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val itemNames = items.mapNotNull { it["name"] as? String }
                        val createdAt = doc.getTimestamp("createdAt")

                        // ✅ FETCH IMAGE: Get customerId, then fetch user doc
                        val customerId = doc.getString("customerId") ?: ""
                        // Note: Fetching user doc inside a loop is not ideal for performance,
                        // but necessary if 'customerImageUrl' is not saved on the Order document.
                        // For now, we rely on the 'customerName' stored on the Order.

                        ProductOrder(
                            orderId = doc.id,
                            orderNumber = doc.getString("orderNumber") ?: "#${doc.id.take(6)}",
                            customerName = doc.getString("customerName") ?: "Customer", // Use value from Order
                            customerImageUrl = "", // Set to empty if not stored on Order.
                            // Ideally, you should save 'customerImageUrl' on the Order document when placing order.
                            // For now, we will show a placeholder icon.
                            timeAgo = createdAt?.let { calculateTimeAgo(it.toDate()) } ?: "Just now",
                            items = itemNames,
                            amount = doc.getDouble("totalAmount") ?: 0.0,
                            status = doc.getString("status") ?: "Processing"
                        )
                    } catch (e: Exception) { null }
                }
                _productOrders.value = orders
            }
    }

    // ✅ REAL-TIME LISTENER FOR BOOKINGS (UPDATED)
    fun loadServiceBookings() {
        val vendorId = auth.currentUser?.uid ?: return

        firestore.collection("bookings")
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val bookings = snapshot.documents.mapNotNull { doc ->
                    try {
                        val bookingDate = doc.getTimestamp("scheduledDate")
                        val time = doc.getString("scheduledTime") ?: ""
                        val formattedDate = bookingDate?.let {
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(it.toDate())
                        } ?: ""

                        ServiceBooking(
                            bookingId = doc.id,
                            bookingNumber = doc.getString("bookingNumber") ?: "#${doc.id.take(6)}",
                            customerName = doc.getString("customerName") ?: "Customer", // Use value from Booking
                            customerImageUrl = "",
                            bookingDate = "$formattedDate $time",
                            serviceName = doc.getString("serviceName") ?: "Service",
                            amount = doc.getDouble("servicePrice") ?: 0.0,
                            status = doc.getString("status") ?: "Requested"
                        )
                    } catch (e: Exception) { null }
                }
                _serviceBookings.value = bookings
            }
    }

    fun acceptOrder(orderId: String) {
        updateStatus("orders", orderId, "Processing", "Order Accepted")
    }

    fun declineOrder(orderId: String) {
        updateStatus("orders", orderId, "Cancelled", "Order Declined")
    }

    fun markDelivered(orderId: String) {
        updateStatus("orders", orderId, "Delivered", "Order Delivered")
    }

    fun acceptBooking(bookingId: String) {
        updateStatus("bookings", bookingId, "Confirmed", "Booking Confirmed")
    }

    fun declineBooking(bookingId: String) {
        updateStatus("bookings", bookingId, "Cancelled", "Booking Declined")
    }

    fun markCompleted(bookingId: String) {
        updateStatus("bookings", bookingId, "Completed", "Service Completed")
    }

    private fun updateStatus(collection: String, docId: String, status: String, msg: String) {
        viewModelScope.launch {
            try {
                firestore.collection(collection).document(docId)
                    .update(mapOf("status" to status, "updatedAt" to FieldValue.serverTimestamp()))
                    .await()

                // Notify Customer
                val doc = firestore.collection(collection).document(docId).get().await()
                val custId = doc.getString("customerId") ?: ""
                if (custId.isNotEmpty()) {
                    sendNotification(custId, msg, "Your $collection status updated to $status")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun sendNotification(userId: String, title: String, message: String) {
        try {
            val data = hashMapOf(
                "userId" to userId,
                "title" to title,
                "message" to message,
                "type" to "ORDER",
                "isRead" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("notifications").add(data).await()
        } catch (e: Exception) { }
    }

    private fun calculateTimeAgo(date: Date): String {
        val now = Date()
        val diff = (now.time - date.time) / 60000 // minutes
        return when {
            diff < 1 -> "Just now"
            diff < 60 -> "$diff min ago"
            diff < 1440 -> "${diff / 60} hr ago"
            else -> "${diff / 1440} days ago"
        }
    }
}