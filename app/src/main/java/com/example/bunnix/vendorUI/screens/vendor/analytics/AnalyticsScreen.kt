package com.example.bunnix.vendorUI.screens.vendor.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.vendorUI.components.BunnixTopBar

@Composable
fun AnalyticsScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            BunnixTopBar(
                title = "Analytics",
                onBackClick = { navController.navigateUp() }
            )
        },
        containerColor = LightGrayBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Analytics - Coming Soon")
        }
    }
}