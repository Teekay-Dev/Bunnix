package com.example.bunnix.vendorUI.screens.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bunnix.ui.theme.BunnixTheme
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.ui.theme.OrangePrimaryModern
import com.example.bunnix.vendorUI.components.BunnixBottomNav
import com.example.bunnix.vendorUI.navigation.VendorNavHost
import com.example.bunnix.vendorUI.navigation.VendorRoutes

@Composable
fun VendorMainScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // ✅ USE THE PROPER BOTTOM NAV COMPONENT
            BunnixBottomNav(navController = navController)
        },
        containerColor = LightGrayBg
    ) { paddingValues ->
        // Navigation content with proper padding
        VendorNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onNavigateToLogin = onNavigateToLogin
        )
    }
}


@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun VendorMainScreenPreview() {
    BunnixTheme {
        VendorMainScreen()
    }
}