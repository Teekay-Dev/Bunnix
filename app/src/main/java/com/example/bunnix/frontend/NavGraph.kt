package com.example.bunnix.frontend

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.frontend.*
import com.example.bunnix.model.VendorViewModel

@Composable
fun VendorNavGraph(navController: NavHostController) {
    // We initialize the ViewModel here so it can be shared across all screens
    val vendorViewModel: VendorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.VendorDashboard.route
    ) {
        // 1. Main Dashboard
        composable(Screen.VendorDashboard.route) {
            VendorDashboardScreen(navController = navController, viewModel = vendorViewModel)
        }

        // 2. Orders & Bookings (Product/Service Toggles)
        composable(Screen.VendorOrders.route) {
            OrdersAndBookingsScreen(navController = navController, viewModel = vendorViewModel)
        }

        // 3. Conversation List (The "Inbox")
        composable(Screen.VendorMessages.route) {
            VendorMessageScreen(navController = navController, viewModel = vendorViewModel)
        }

        // 4. Real Chat Detail (Opens when a conversation is clicked)
        composable(
            route = "chat_detail/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            ChatDetailScreen(customerId = customerId, viewModel = vendorViewModel)
        }

        // 5. Profile & Settings (Vendor Mode Toggle)
        composable(Screen.VendorProfile.route) {
            // Fetch current vendor data from ViewModel
            val vendor = vendorViewModel.vendorProfile.value
            vendor?.let {
                VendorProfileScreen(navController = navController, vendor = it, viewModel = vendorViewModel)
            }
        }

        // 6. Inventory Management
        composable(Screen.ManageInventory.route) {
            ManageInventoryScreen(navController = navController, viewModel = vendorViewModel)
        }

        // 7. Add/Edit Product Screen
        composable(Screen.AddProduct.route) {
            AddProductScreen(navController = navController)
        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AddProductScreen(navController = navController, productId = productId)
        }

        // 8. Add Booking (For manual service entry)
        composable(Screen.AddBooking.route) {
            AddBookingScreen(navController = navController)
        }

//        composable("business_settings") {
//            // Get current user from your auth state/session
//            val currentUser = viewModel.currentUser.value
//            currentUser?.let {
//                BusinessSettingsScreen(navController, it, viewModel)
//            }
//        }

    }
}