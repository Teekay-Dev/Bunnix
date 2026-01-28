package com.example.bunnix.frontend

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


sealed class Screen(val route: String) {
    // Bottom Nav Screens
    object VendorDashboard : Screen("vendor_dashboard")
    object VendorOrders : Screen("vendor_orders")
    object VendorMessages : Screen("vendor_messages")
    object VendorProfile : Screen("vendor_profile")
    object ManageInventory : Screen("manage_inventory")

    // Sub-screens (Quick Actions)
    object AddProduct : Screen("add_product")
    object Bookings : Screen("bookings")
    object AddBooking : Screen("add_booking")
    object EditProduct : Screen("add_product?productId={productId}")
}

//sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
//    object Dashboard : NavItem("dashboard", Icons.Default.Home, "Dashboard")
//    object Orders : NavItem("orders", Icons.Default.ListAlt, "Orders")
//    object Messages : NavItem("messages", Icons.Default.Chat, "Messages")
//    object Profile : NavItem("profile", Icons.Default.Person, "Profile")
//}

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
@Composable
fun BunnixBottomNavigation(navController: NavController) {
    // Sync these with your Screen object routes
    val items = listOf(
        NavItem(Screen.VendorDashboard.route, Icons.Default.Home, "Dashboard"),
        NavItem(Screen.VendorOrders.route, Icons.Default.ListAlt, "Orders"),
        NavItem(Screen.VendorMessages.route, Icons.Default.Chat, "Messages"),
        NavItem(Screen.VendorProfile.route, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route, // Logic added
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFF2711C),
                    selectedTextColor = Color(0xFFF2711C),
                    indicatorColor = Color(0xFFF2711C).copy(alpha = 0.1f)
                )
            )
        }
    }
}

