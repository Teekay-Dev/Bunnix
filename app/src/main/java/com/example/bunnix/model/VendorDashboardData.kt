package com.example.bunnix.model

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.remote.SupabaseClient
import com.example.bunnix.database.BunnixDatabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.gotrue.auth


// Data model for the top stats
data class VendorDashboardData(
    val availableBalance: Double,
    val totalSales: Double,
    val totalOrders: Int,
    val totalBookings: Int,
    val totalCustomers: Int,
    val unreadMessages: Int
)

// Data model for the recent orders list
data class VendorOrder(
    val id: String,
    val customerName: String,
    val status: String, // e.g., "pending", "processing", "completed"
    val price: Double,
    val itemCount: Int
)

class VendorViewModel (application: android.app.Application) : AndroidViewModel(application) {
    private val productDao = BunnixDatabase.getDatabase(application).productDao()
    private val currentUserId: String?
        get() = SupabaseClient.client.auth.currentUserOrNull()?.id

    var isLoading by mutableStateOf(false)
        private set // This allows UI to read it, but only ViewModel to change it

    val allProducts: StateFlow<List<Product>> = productDao.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _dashboardStats = mutableStateOf(
        VendorDashboardData(2450.00, 12450.0, 156, 24, 89,unreadMessages = 1)
    )
    val dashboardStats: State<VendorDashboardData> = _dashboardStats

    private val _recentOrders = mutableStateListOf(
        VendorOrder("#AB12C", "John Doe", "pending", 45.99, 2),
        VendorOrder("#AB12D", "Jane Smith", "processing", 129.99, 1),
        VendorOrder("#AB12E", "Mike Chen", "completed", 78.50, 3)
    )
    val recentOrders: List<VendorOrder> = _recentOrders


    fun saveProduct(product: Product) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            // Create a copy of the product with the current user's ID tagged on it
            val productWithVendor = product.copy(vendor_id = userId)

            // Now save this tagged product to Supabase
            SupabaseClient.client.from("products").insert(productWithVendor)
        }
    }

    // 2. UPDATE SAVE: Tag the product with MY ID
    fun updateProduct(productId: String, product: Product) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                // Ensure the product being saved carries the vendor_id
                val productWithId = product.copy(vendor_id = userId)

                SupabaseClient.client.from("products").update(productWithId) {
                    filter { eq("id", productId) }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }


    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("products").delete {
                    filter {
                        eq("id", product.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun uploadImage(uri: Uri, context: Context): String? {
        return try {
            val bucket = SupabaseClient.client.storage.from("product_images")
            val fileName = "${System.currentTimeMillis()}.jpg"
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()

            if (bytes != null) {
                bucket.upload(fileName, bytes)
                bucket.publicUrl(fileName) // This returns the https:// link
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateVendorProfile(userId: String, businessName: String, address: String, phone: String) {
        viewModelScope.launch {
            try {
                isLoading = true // Assuming you add a val isLoading = mutableStateOf(false)
                SupabaseClient.client.from("users").update(
                    mapOf(
                        "business_name" to businessName,
                        "business_address" to address,
                        "phone" to phone
                    )
                ) {
                    filter { eq("id", userId) }
                }
                // Optional: Toast "Profile Updated Successfully"
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("orders").update(
                    mapOf("status" to newStatus)
                ) {
                    filter { eq("id", orderId) }
                }
                fetchOrdersAndBookings() // Refresh the list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDashboardStats() {
        viewModelScope.launch {
            try {
                // Use the current state value instead of the undefined 'stats' variable
                _dashboardStats.value = _dashboardStats.value.copy(
                    totalOrders = 0,
                    totalSales = 0.0 // Changed from "0.00" string to Double
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private val _allOrders = mutableStateOf<List<Order>>(emptyList())
    val allOrders: State<List<Order>> = _allOrders

    private val _allBookings = mutableStateOf<List<Booking>>(emptyList())
    val allBookings: State<List<Booking>> = _allBookings

    init {
        fetchOrdersAndBookings()
        // 1. Start listening to database changes
        setupRealtimeHooks()
    }

    private fun setupRealtimeHooks() {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                val realtime = SupabaseClient.client.realtime
                realtime.connect()

                val myChannel = realtime.channel("vendor-changes")

                myChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "orders"
                }.onEach { action ->
                    // Handle the action based on its type (Insert or Update)
                    val data = when (action) {
                        is PostgresAction.Insert -> action.record
                        is PostgresAction.Update -> action.record
                        else -> null // Ignore Deletes or others for now
                    }

                    // Check if the vendor_id in the record matches the current user
                    val orderVendorId = data?.get("vendor_id")?.toString()?.replace("\"", "")

                    if (orderVendorId == userId) {
                        fetchOrdersAndBookings()
                        fetchDashboardStats()
                    }
                }.launchIn(this)

                myChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "bookings"
                }.onEach { action ->
                    val data = when (action) {
                        is PostgresAction.Insert -> action.record
                        is PostgresAction.Update -> action.record
                        else -> null
                    }

                    val bookingVendorId = data?.get("vendor_id")?.toString()?.replace("\"", "")

                    if (bookingVendorId == userId) {
                        fetchOrdersAndBookings()
                    }
                }.launchIn(this)

                myChannel.subscribe()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





    // 1. UPDATE FETCH: Only get MY products/orders
    fun fetchOrdersAndBookings() {
        val userId = currentUserId ?: return // Guard clause

        viewModelScope.launch {
            try {
                // Fetch only Orders belonging to THIS vendor
                val ordersData = SupabaseClient.client.from("orders")
                    .select {
                        filter { eq("vendor_id", userId) }
                    }.decodeList<Order>()
                _allOrders.value = ordersData

                // Fetch only Bookings belonging to THIS vendor
                val bookingsData = SupabaseClient.client.from("bookings")
                    .select {
                        filter { eq("vendor_id", userId) }
                    }.decodeList<Booking>()
                _allBookings.value = bookingsData
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

}