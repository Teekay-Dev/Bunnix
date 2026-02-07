//package com.example.bunnix.backend
//
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.example.bunnix.data.model.User
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.bunnix.frontend.* // This ensures all screens in frontend are imported
//
//@Composable
//fun VendorNavGraph(navController: NavHostController) {
//    // 1. Move the user data outside the NavHost but inside the function
//    val currentUser = User(
//        id = "000",
//        name = "John Doe",
//        email = "john@example.com"
//    )
//
//    NavHost(
//        navController = navController,
//        startDestination = Screen.VendorDashboard.route
//    ) {
//        // --- Dashboard ---
//        composable(Screen.VendorDashboard.route) {
//            VendorDashboardScreen(navController = navController)
//        }
//
//        // --- Orders ---
//        composable(Screen.VendorOrders.route) {
//            OrdersAndBookingsScreen(navController = navController)
//        }
//
//        // --- Booking Actions ---
//        composable(Screen.AddBooking.route) {
//            AddBookingScreen(navController = navController)
//        }
//
//        composable(Screen.Bookings.route) {
//            Text("Bookings Screen")
//        }
//
//        // --- Messages ---
//        composable(Screen.VendorMessages.route) {
//            Text("Messages Screen")
//        }
//
//        // --- Profile ---
//        composable(Screen.VendorProfile.route) {
//            // 1. Initialize the ViewModel here
//            val profileViewModel: VendorProfileViewModel = viewModel()
//
//            // 2. Pass ONLY the navController and the viewModel
//            // Remove "user = currentUser" because the screen doesn't ask for it anymore
//            VendorProfileScreen(
//                navController = navController,
//                viewModel = profileViewModel
//            )
//        }
//
//        // --- Inventory & Products ---
//        composable(Screen.ManageInventory.route) {
//            ManageInventoryScreen(navController = navController)
//        }
//
//        composable(Screen.AddProduct.route) {
//            AddProductScreen(navController = navController)
//        }
//
//        composable(
//            route = Screen.EditProduct.route,
//            arguments = listOf(navArgument("productId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val productId = backStackEntry.arguments?.getString("productId")
//            AddProductScreen(navController = navController, productId = productId)
//        }
//    }
//}