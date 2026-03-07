package com.example.bunnix.vendorUI.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class VendorBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : VendorBottomNavItem(
        route = "vendor_dashboard",
        title = "Dashboard",
        icon = Icons.Default.Home
    )

    object Orders : VendorBottomNavItem(
        route = "vendor_orders",
        title = "Orders",
        icon = Icons.Default.ShoppingBag
    )

    object Messages : VendorBottomNavItem(
        route = "vendor_messages",
        title = "Messages",
        icon = Icons.Default.Message
    )

    object Profile : VendorBottomNavItem(
        route = "vendor_profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}

// All vendor routes
object VendorRoutes {
    const val MAIN = "vendor_main"
    const val DASHBOARD = "vendor_dashboard"
    const val ORDERS = "vendor_orders"
    const val MESSAGES = "vendor_messages"
    const val PROFILE = "vendor_profile"

    // Child routes
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val BOOKING_DETAIL = "booking_detail/{bookingId}"
    const val PAYMENT_VERIFICATION = "payment_verification/{orderId}"
    const val ADD_PRODUCT = "add_product"
    const val EDIT_PRODUCT = "edit_product/{productId}"
    const val MANAGE_INVENTORY = "manage_inventory"
    const val ADD_SERVICE = "add_service"
    const val EDIT_SERVICE = "edit_service/{serviceId}"
    const val CHAT = "chat/{chatId}"
    const val EDIT_BUSINESS = "edit_business"
    const val NOTIFICATIONS = "notifications"
    const val ANALYTICS = "analytics"
    const val PAYMENT_SETTINGS = "payment_settings"

    fun orderDetail(orderId: String) = "order_detail/$orderId"
    fun bookingDetail(bookingId: String) = "booking_detail/$bookingId"
    fun paymentVerification(orderId: String) = "payment_verification/$orderId"
    fun editProduct(productId: String) = "edit_product/$productId"
    fun editService(serviceId: String) = "edit_service/$serviceId"
    fun chat(chatId: String) = "chat/$chatId"
}