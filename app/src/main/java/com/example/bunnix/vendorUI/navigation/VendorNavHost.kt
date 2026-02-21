package com.example.bunnix.vendorUI.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Screens - make sure these paths are correct and unique
import com.example.bunnix.vendorUI.screens.vendor.analytics.AnalyticsScreen
import com.example.bunnix.vendorUI.screens.vendor.dashboard.VendorDashboardScreen
import com.example.bunnix.vendorUI.screens.vendor.messages.ChatConversationScreen
import com.example.bunnix.vendorUI.screens.vendor.messages.MessagesListScreen

// Orders screens
import com.example.bunnix.vendorUI.screens.vendor.orders.OrdersBookingsScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.ProductOrdersScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.ServiceBookingsScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.OrderDetailScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.BookingDetailScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.PaymentVerificationScreen

// Product screens
import com.example.bunnix.vendorUI.screens.vendor.products.AddProductScreen
import com.example.bunnix.vendorUI.screens.vendor.products.EditProductScreen
import com.example.bunnix.vendorUI.screens.vendor.products.ManageInventoryScreen

// Service screens
import com.example.bunnix.vendorUI.screens.vendor.services.AddServiceScreen
import com.example.bunnix.vendorUI.screens.vendor.services.EditServiceScreen

// Profile screens
import com.example.bunnix.vendorUI.screens.vendor.profile.VendorProfileScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.EditBusinessProfileScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.NotificationsScreen

@Composable
fun VendorNavHost(
    navController: NavHostController,
    onSwitchToCustomerMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        // Dashboard
        composable("dashboard") {
            VendorDashboardScreen(
                onNavigateToOrders = { navController.navigate("orders_bookings") },
                onNavigateToAddProduct = { navController.navigate("add_product") },
                onNavigateToAddService = { navController.navigate("add_service") },
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToAnalytics = { navController.navigate("analytics") }
            )
        }

        // Orders & Bookings (Main Tab)
        composable("orders_bookings") {
            OrdersBookingsScreen(
                navController = navController
            )
        }

        // Product Orders List
        composable("product_orders") {
            ProductOrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate("order_detail/$orderId")
                }
            )
        }

        // Service Bookings List
        composable("service_bookings") {
            ServiceBookingsScreen(
                onBookingClick = { bookingId ->
                    navController.navigate("booking_detail/$bookingId")
                }
            )
        }

        // Order Detail
        composable(
            route = "order_detail/{orderId}",
            arguments = listOf(androidx.navigation.navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onNavigateToPaymentVerification = {
                    navController.navigate("payment_verification/$orderId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Booking Detail
        composable(
            route = "booking_detail/{bookingId}",
            arguments = listOf(androidx.navigation.navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                bookingId = bookingId,
                onBack = { navController.popBackStack() }
            )
        }

        // Payment Verification
        composable(
            route = "payment_verification/{orderId}",
            arguments = listOf(androidx.navigation.navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            PaymentVerificationScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onVerified = { navController.popBackStack() }
            )
        }

        // Products
        composable("add_product") {
            AddProductScreen(
                onBack = { navController.popBackStack() },
                onProductAdded = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_product/{productId}",
            arguments = listOf(androidx.navigation.navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onProductUpdated = { navController.popBackStack() }
            )
        }

        composable("inventory") {
            ManageInventoryScreen(
                onEditProduct = { productId ->
                    navController.navigate("edit_product/$productId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Services
        composable("add_service") {
            AddServiceScreen(
                onBack = { navController.popBackStack() },
                onServiceAdded = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_service/{serviceId}",
            arguments = listOf(androidx.navigation.navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            EditServiceScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onServiceUpdated = { navController.popBackStack() }
            )
        }

        // Messages
        composable("messages") {
            MessagesListScreen(
                onChatClick = { chatId ->
                    navController.navigate("chat/$chatId")
                }
            )
        }

        composable(
            route = "chat/{chatId}",
            arguments = listOf(androidx.navigation.navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatConversationScreen(
                chatId = chatId,
                onBack = { navController.popBackStack() }
            )
        }

        // Analytics
        composable("analytics") {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Profile & Settings
        composable("profile") {
            VendorProfileScreen(
                onEditProfile = { navController.navigate("edit_business_profile") },
                onNotifications = { navController.navigate("notifications") },
                onSwitchMode = onSwitchToCustomerMode,
                onLogout = { /* Handle logout */ }
            )
        }

        composable("edit_business_profile") {
            EditBusinessProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}