package com.example.bunnix.backend

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bunnix.data.model.User
import com.example.bunnix.frontend.AddBookingScreen
import com.example.bunnix.frontend.AddProductScreen
import com.example.bunnix.frontend.ManageInventoryScreen
import com.example.bunnix.frontend.OrdersAndBookingsScreen
import com.example.bunnix.frontend.Screen
import com.example.bunnix.frontend.VendorDashboardScreen
import com.example.bunnix.frontend.VendorProfileScreen

@Composable
fun VendorNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.VendorDashboard.route
    ) {
        composable(Screen.VendorDashboard.route) {
            // Pass the navController so the dashboard can trigger sub-navigation
            VendorDashboardScreen(navController = navController)
        }
        composable(Screen.VendorOrders.route) {
            OrdersAndBookingsScreen(navController = navController)
        }
        composable(Screen.AddBooking.route) {
            AddBookingScreen(navController = navController)
        }
        composable(Screen.VendorMessages.route) {
            Text("Messages Screen")
        }
        // In VendorNavGraph.kt
        val currentUser = User(
            id = "000", // Required
            full_name = "John Doe", // Your model uses full_name, not name
            email = "john@example.com", // Required
            is_vendor = true, // Matches your model's requirement
            role = "Vendor"
        )
        composable(Screen.VendorProfile.route) {
            VendorProfileScreen(navController = navController, user = currentUser)
        }
        composable(Screen.AddProduct.route) {
            // This now calls the actual UI instead of just showing text
            AddProductScreen(navController = navController)
        }
        composable(Screen.Bookings.route) {
            Text("Bookings Screen")
        }
        composable(Screen.ManageInventory.route) {
            ManageInventoryScreen(navController = navController)
        }
        // Add this inside your NavHost block
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            // Pass the productId to the AddProductScreen
            AddProductScreen(navController = navController, productId = productId)
        }
    }
}