package com.example.bunnix.vendorUI.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

// All vendor routes
object VendorRoutes {
    // Main routes
    const val MAIN = "vendor_main"
    const val DASHBOARD = "vendor/dashboard"
    const val ORDERS = "vendor/orders"
    const val MESSAGES = "vendor/messages"
    const val PROFILE = "vendor/profile"

    // Child routes - Orders & Bookings
    const val ORDER_DETAIL = "vendor/order/{orderId}"
    const val BOOKING_DETAIL = "vendor/booking/{bookingId}"
    const val PAYMENT_VERIFICATION = "vendor/payment_verification/{orderId}"

    // Product & Service routes
    const val ADD_PRODUCT = "vendor/product/add"
    const val EDIT_PRODUCT = "vendor/product/edit/{productId}"
    const val MANAGE_INVENTORY = "vendor/inventory"
    const val ADD_SERVICE = "vendor/service/add"
    const val EDIT_SERVICE = "vendor/service/edit/{serviceId}"

    // Chat routes
    const val CHAT = "vendor/chat/{chatId}"

    // Profile routes
    const val EDIT_BUSINESS = "vendor/profile/edit"
    const val PAYMENT_SETTINGS = "vendor/profile/payment"
    const val NOTIFICATIONS = "vendor/profile/notifications"
    const val ANALYTICS = "vendor/analytics"
    const val GET_VERIFIED = "vendor/get_verified"

    // Helper functions for navigation with parameters
    fun orderDetail(orderId: String) = "vendor/order/$orderId"
    fun bookingDetail(bookingId: String) = "vendor/booking/$bookingId"
    fun paymentVerification(orderId: String) = "vendor/payment_verification/$orderId"
    fun editProduct(productId: String) = "vendor/product/edit/$productId"
    fun editService(serviceId: String) = "vendor/service/edit/$serviceId"
    fun chat(chatId: String) = "vendor/chat/$chatId"
}

// ✅ CHANGED TO DATA CLASS (was sealed class)
data class VendorBottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
) {
    companion object {
        val items = listOf(
            VendorBottomNavItem(
                route = VendorRoutes.DASHBOARD,
                selectedIcon = Icons.Filled.Dashboard,
                unselectedIcon = Icons.Outlined.Dashboard,
                label = "Dashboard"
            ),
            VendorBottomNavItem(
                route = VendorRoutes.ORDERS,
                selectedIcon = Icons.Filled.ShoppingBag,
                unselectedIcon = Icons.Outlined.ShoppingBag,
                label = "Orders"
            ),
            VendorBottomNavItem(
                route = VendorRoutes.MESSAGES,
                selectedIcon = Icons.Filled.Message,
                unselectedIcon = Icons.Outlined.Message,
                label = "Messages"
            ),
            VendorBottomNavItem(
                route = VendorRoutes.PROFILE,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                label = "Profile"
            )
        )
    }
}