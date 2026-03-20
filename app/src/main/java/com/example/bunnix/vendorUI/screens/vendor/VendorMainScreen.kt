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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.ui.theme.OrangePrimaryModern
import com.example.bunnix.vendorUI.navigation.VendorNavHost
import com.example.bunnix.vendorUI.navigation.VendorRoutes

@Composable
fun VendorMainScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
    ) {
        // Main Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            VendorNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                onNavigateToLogin = onNavigateToLogin
            )
        }

        // ✅ HARDCODED BOTTOM NAV - NO EXTERNAL COMPOSABLE
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dashboard
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(VendorRoutes.DASHBOARD) {
                                popUpTo(VendorRoutes.DASHBOARD) { inclusive = true }
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (currentRoute == VendorRoutes.DASHBOARD)
                            Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                        contentDescription = "Dashboard",
                        tint = if (currentRoute == VendorRoutes.DASHBOARD)
                            OrangePrimaryModern else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dashboard",
                        fontSize = 12.sp,
                        color = if (currentRoute == VendorRoutes.DASHBOARD)
                            OrangePrimaryModern else Color.Gray,
                        fontWeight = if (currentRoute == VendorRoutes.DASHBOARD)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Orders
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(VendorRoutes.ORDERS) {
                                popUpTo(VendorRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (currentRoute == VendorRoutes.ORDERS)
                            Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                        contentDescription = "Orders",
                        tint = if (currentRoute == VendorRoutes.ORDERS)
                            OrangePrimaryModern else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Orders",
                        fontSize = 12.sp,
                        color = if (currentRoute == VendorRoutes.ORDERS)
                            OrangePrimaryModern else Color.Gray,
                        fontWeight = if (currentRoute == VendorRoutes.ORDERS)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Messages
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(VendorRoutes.MESSAGES) {
                                popUpTo(VendorRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (currentRoute == VendorRoutes.MESSAGES)
                            Icons.AutoMirrored.Filled.Message else Icons.AutoMirrored.Outlined.Message,
                        contentDescription = "Messages",
                        tint = if (currentRoute == VendorRoutes.MESSAGES)
                            OrangePrimaryModern else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Messages",
                        fontSize = 12.sp,
                        color = if (currentRoute == VendorRoutes.MESSAGES)
                            OrangePrimaryModern else Color.Gray,
                        fontWeight = if (currentRoute == VendorRoutes.MESSAGES)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Profile
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(VendorRoutes.PROFILE) {
                                popUpTo(VendorRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (currentRoute == VendorRoutes.PROFILE)
                            Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = if (currentRoute == VendorRoutes.PROFILE)
                            OrangePrimaryModern else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Profile",
                        fontSize = 12.sp,
                        color = if (currentRoute == VendorRoutes.PROFILE)
                            OrangePrimaryModern else Color.Gray,
                        fontWeight = if (currentRoute == VendorRoutes.PROFILE)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}