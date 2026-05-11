package com.example.bunnix.vendorUI.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bunnix.vendorUI.screens.vendor.dashboard.VendorDashboardScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.OrdersBookingsScreen
import com.example.bunnix.vendorUI.screens.vendor.messages.MessagesListScreen
import com.example.bunnix.vendorUI.screens.vendor.messages.ChatConversationScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.VendorProfileScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.EditBusinessProfileScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.PaymentSettingsScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.NotificationsScreen
import com.example.bunnix.vendorUI.screens.vendor.profile.GetVerifiedScreen
import com.example.bunnix.vendorUI.screens.vendor.products.AddProductScreen
import com.example.bunnix.vendorUI.screens.vendor.products.EditProductScreen
import com.example.bunnix.vendorUI.screens.vendor.products.ManageInventoryScreen
import com.example.bunnix.vendorUI.screens.vendor.services.AddServiceScreen
import com.example.bunnix.vendorUI.screens.vendor.services.EditServiceScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.OrderDetailScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.BookingDetailScreen
import com.example.bunnix.vendorUI.screens.vendor.orders.PaymentVerificationScreen
import com.example.bunnix.vendorUI.screens.vendor.analytics.AnalyticsScreen

@Composable
fun VendorNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = VendorRoutes.DASHBOARD,
        modifier = modifier
    ) {
        // ============= BOTTOM NAV SCREENS =============

        composable(VendorRoutes.DASHBOARD) {
            VendorDashboardScreen(navController = navController)
        }

        composable(VendorRoutes.ORDERS) {
            OrdersBookingsScreen(navController = navController)
        }

        composable(VendorRoutes.MESSAGES) {
            MessagesListScreen(navController = navController)
        }

        composable(VendorRoutes.PROFILE) {
            VendorProfileScreen(
                navController = navController,
                onNavigateToLogin = onNavigateToLogin
            )
        }

        // ============= CHAT =============

        composable(
            route = VendorRoutes.CHAT,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            ChatConversationScreen(
                navController = navController,
                chatId = chatId
            )
        }

        // ============= ORDERS & BOOKINGS DETAILS =============

        composable(
            route = VendorRoutes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                navController = navController,
                orderId = orderId
            )
        }

        composable(
            route = VendorRoutes.BOOKING_DETAIL,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                navController = navController,
                bookingId = bookingId
            )
        }

        composable(
            route = VendorRoutes.PAYMENT_VERIFICATION,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            PaymentVerificationScreen(
                navController = navController,
                orderId = orderId
            )
        }

        // ============= PRODUCTS =============

        composable(VendorRoutes.ADD_PRODUCT) {
            AddProductScreen(navController = navController)
        }

        composable(
            route = VendorRoutes.EDIT_PRODUCT,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            EditProductScreen(
                navController = navController,
                productId = productId
            )
        }

        composable(VendorRoutes.MANAGE_INVENTORY) {
            ManageInventoryScreen(navController = navController)
        }

        // ============= SERVICES =============

        composable(VendorRoutes.ADD_SERVICE) {
            AddServiceScreen(navController = navController)
        }

        composable(
            route = VendorRoutes.EDIT_SERVICE,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            EditServiceScreen(
                navController = navController,
                serviceId = serviceId
            )
        }

        // ============= PROFILE SCREENS =============

        composable(VendorRoutes.EDIT_BUSINESS) {
            EditBusinessProfileScreen(navController = navController)
        }

        composable(VendorRoutes.PAYMENT_SETTINGS) {
            PaymentSettingsScreen(navController = navController)
        }

        composable(VendorRoutes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }

        composable(VendorRoutes.ANALYTICS) {
            AnalyticsScreen(navController = navController)
        }

        // ============= GET VERIFIED (NEW) =============

        composable(VendorRoutes.GET_VERIFIED) {
            GetVerifiedScreen(navController = navController)
        }
    }
}