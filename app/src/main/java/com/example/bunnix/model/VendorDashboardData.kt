package com.example.bunnix.model

import android.net.Uri
import io.github.jan.supabase.postgrest.from
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.data.remote.SupabaseClient
import com.example.bunnix.model.data.repository.AuthRepository
import com.example.bunnix.utils.NetworkResult
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.query.Order as SupabaseOrder



class VendorViewModel : ViewModel() {

  private val authRepository = AuthRepository()
    private val _conversations = mutableStateOf<List<ChatSummary>>(emptyList())
    val conversations: State<List<ChatSummary>> = _conversations
    private val _bookings = mutableStateOf<List<Booking>>(emptyList())
    val bookings: State<List<Booking>> = _bookings

    val totalBookingsCount by derivedStateOf { bookings.value.size }

    private val _vendorProducts = mutableStateOf<List<Product>>(emptyList())
    val vendorProducts: State<List<Product>> = _vendorProducts
    var selectedImageUri by mutableStateOf<Uri?>(null)
    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
    }
    val unreadNotificationCount by derivedStateOf {
        notifications.value.count { !it.is_read }
    }
    private val _orders = mutableStateOf<List<Order>>(emptyList())
    val orders: State<List<Order>> = _orders

    var selectedProductForEdit by mutableStateOf<Product?>(null)
        private set
    fun onEditProductSelected(product: Product) {
        selectedProductForEdit = product
    }
    fun clearEditSelection() {
        selectedProductForEdit = null
    }
    init {
        setupRealtimeHooks()
        fetchInitialData()
    }

    private val _recentOrders = mutableStateOf<List<Order>>(emptyList())
    val recentOrders = derivedStateOf {
        orders.value.take(10)
    }




    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        businessName: String?,
        businessAddress: String?,
        isVendor: Boolean
    ): NetworkResult<AuthData> {
        val result = authRepository.register(
            name, email, phone, password, confirmPassword,
            businessName, businessAddress, isVendor
        )

        if (result is NetworkResult.Success) {
            _vendorProfile.value = result.data?.vendor
        }
        return result
    }


    val totalSales by derivedStateOf {
        val productIncome = orders.value
            .filter { it.status == "delivered" }
            .sumOf { it.total_price }

        val serviceIncome = bookings.value
            .filter { it.status == "completed" }
            .sumOf { it.price }

        productIncome + serviceIncome
    }
    val totalOrdersCount by derivedStateOf {
        orders.value.size + bookings.value.size
    }
    val availableBalance by derivedStateOf {
        totalSales
    }
    val uniqueCustomersCount by derivedStateOf {
        val orderCustomers = orders.value.map { it.customer_id }.toSet()
        val bookingCustomers = bookings.value.map { it.customer_id }.toSet()
        (orderCustomers + bookingCustomers).size
    }


    private val _chatMessages = mutableStateOf<List<Message>>(emptyList())
    val chatMessages: State<List<Message>> = _chatMessages

    fun sendMessage(receiverId: String, content: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val message = Message(
                    sender_id = userId,
                    receiver_id = receiverId,
                    content = content
                )
                SupabaseClient.client.from("messages").insert(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Track the toggle state for the UI
    var isVendorModeEnabled by mutableStateOf(false)

    fun toggleVendorMode(enabled: Boolean) {
        isVendorModeEnabled = enabled
        if (enabled) {
            // When they switch to Business Mode, fetch their business data
            fetchVendorProfile()
            fetchDashboardData()
        }
    }

    // Logic for the "Edit Profile" button in your screenshot
    fun updateVendorProfile(businessName: String, phone: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "businessName" to businessName,
                    "phone" to phone
                )
                SupabaseClient.client.from("vendors").update(updates) {
                    filter { eq("id", userId) }
                }
                fetchVendorProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun listenForMessages(receiverId: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            val channel = SupabaseClient.client.realtime.channel("chat-channel")
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "messages"
            }.onEach {
                fetchMessages(receiverId)
            }.launchIn(viewModelScope)
            channel.subscribe()
        }
    }

    fun fetchMessages(contactId: String) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                // This fetches the conversation between the vendor and a specific customer
                _chatMessages.value = SupabaseClient.client.from("messages")
                    .select {
                        filter {
                            and {
                                or {
                                    eq("sender_id", userId)
                                    eq("sender_id", contactId)
                                }
                                or {
                                    eq("receiver_id", userId)
                                    eq("receiver_id", contactId)
                                }
                            }
                        }
                        order("created_at", order = io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }.decodeList<Message>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun updateVendorProfile(vendorId: Int, name: String, address: String, phone: String) {
        viewModelScope.launch {
            try {

                SupabaseClient.client.from("users").update(
                    mapOf(
                        "full_name" to name,
                        "business_address" to address, // Ensure this column exists in DB
                        "phone" to phone
                    )
                ) {
                    filter { eq("id", vendorId) }
                }
                // Optionally refresh local state
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _notifications = mutableStateOf<List<Notification>>(emptyList())
    val notifications: State<List<Notification>> = _notifications

    fun fetchNotifications() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                _notifications.value = SupabaseClient.client.from("notifications")
                    .select {
                        filter { eq("user_id", userId) }
                        order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }.decodeList<Notification>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("notifications").update(
                    { set("is_read", true) }
                ) {
                    filter { eq("id", notificationId) }
                }
                fetchNotifications()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _vendorProfile = mutableStateOf<Vendor?>(null)
    val vendorProfile: State<Vendor?> = _vendorProfile

    fun fetchVendorProfile() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                _vendorProfile.value = SupabaseClient.client.from("vendors")
                    .select {
                        filter { eq("id", userId) }
                    }.decodeSingle<Vendor>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("orders").update(
                    { set("status", newStatus) }
                ) {
                    filter { eq("id", orderId) }
                }
                fetchInitialData()
                fetchDashboardData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("bookings").update(
                    { set("status", newStatus) }
                ) {
                    filter { eq("id", bookingId) }
                }

                fetchInitialData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onAddServiceClicked(name: String, price: Double, duration: String, description: String) {
        val service = Service(
            id = null,
            vendor_id = "",
            name = name,
            price = price,
            duration = duration,
            description = description
        )
        saveService(service)
    }


    fun onAddProductClicked(context: android.content.Context, name: String, price: Double, desc: String, category: String) {
        viewModelScope.launch {
            val imageUrl = selectedImageUri?.let { uri ->
                uploadImage(uri, context)
            } ?: ""

            val product = Product(
                id = null,
                name = name,
                price = price,
                description = desc,
                category = category,
                image_url = imageUrl,
                location = "Store Location",
                quantity = "1",
                vendor_id = "",
                created_at = System.currentTimeMillis()
            )

            saveProduct(product)
        }
    }

    private val _vendorServices = mutableStateOf<List<Service>>(emptyList())
    val vendorServices: State<List<Service>> = _vendorServices

    fun fetchVendorServices() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                _vendorServices.value = SupabaseClient.client.from("services")
                    .select {
                        filter { eq("vendor_id", userId) }
                    }.decodeList<Service>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveService(service: Service) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val finalService = service.copy(vendor_id = userId)
                SupabaseClient.client.from("services").insert(finalService)
                fetchVendorServices()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun loginUser(email: String, password: String): NetworkResult<AuthData> {
        val result = authRepository.login(email, password) // authRepository should be initialized in VM
        if (result is NetworkResult.Success) {
            _vendorProfile.value = result.data?.vendor
        }
        return result
    }


















    fun fetchDashboardData() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                _recentOrders.value = SupabaseClient.client.from("orders")
                    .select {
                        filter { eq("vendor_id", userId) }
                        // Fix: Explicitly use Order.DESCENDING
                        order("created_at", order = SupabaseOrder.DESCENDING)
                        limit(10)
                    }.decodeList<Order>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupRealtimeHooks() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            val myChannel = SupabaseClient.client.realtime.channel("vendor-updates")

            myChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                this.table = "orders"
            }.onEach {
                fetchInitialData()
            }.launchIn(viewModelScope)

            myChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
                this.table = "bookings"
            }.onEach {
                fetchInitialData()
            }.launchIn(viewModelScope)

            myChannel.subscribe()
        }
    }
    fun fetchInitialData() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _orders.value = SupabaseClient.client.from("orders")
                .select {
                    filter {
                        eq("vendor_id", userId)
                    }
                }.decodeList<Order>()
        }
    }


    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("products").delete {
                    filter {
                        eq("id", productId)
                    }
                }
                fetchInitialData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun uploadImage(uri: Uri, context: android.content.Context): String? {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return null
            val fileName = "${System.currentTimeMillis()}.jpg"

            val bucket = SupabaseClient.client.storage.from("product-images")

            // Fix: Use the correct parameter names for the 3.0.2 SDK
            bucket.upload(path = fileName, data = bytes) {
                upsert = true
            }

            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveProduct(product: Product) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val finalProduct = product.copy(vendor_id = userId)
                SupabaseClient.client.from("products").insert(finalProduct)

                fetchVendorProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProduct(productId: String, product: Product) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("products").update(product) {
                    filter {
                        eq("id", product.id ?: 0)
                    }
                }
                fetchVendorProducts()
                fetchInitialData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveBooking(booking: Booking) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val finalBooking = booking.copy(vendor_id = userId)
                SupabaseClient.client.from("bookings").insert(finalBooking)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun fetchVendorProducts() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                _vendorProducts.value = SupabaseClient.client.from("products")
                    .select {
                        filter { eq("vendor_id", userId) }
                    }.decodeList<Product>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}