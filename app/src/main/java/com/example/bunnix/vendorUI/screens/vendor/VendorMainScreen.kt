package com.example.bunnix.vendorUI.screens.vendor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.vendorUI.components.BunnixBottomNav
import com.example.bunnix.vendorUI.navigation.VendorBottomNavItem
import com.example.bunnix.vendorUI.navigation.VendorNavHost
import com.example.bunnix.vendorUI.navigation.VendorRoutes

@Composable
fun VendorMainScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if bottom nav should be shown
    val showBottomBar = when (currentRoute) {
        VendorRoutes.DASHBOARD,
        VendorRoutes.ORDERS,
        VendorRoutes.MESSAGES,
        VendorRoutes.PROFILE -> true
        else -> false
    }

    Scaffold(
        containerColor = LightGrayBg,
        bottomBar = {
            if (showBottomBar) {
                BunnixBottomNav(navController = navController)
            }
        }
    ) { padding ->
        VendorNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
            onNavigateToLogin = onNavigateToLogin
        )
    }
}