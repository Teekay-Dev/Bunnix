package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.vendorUI.components.BunnixTopBar

@Composable
fun PaymentVerificationScreen(
    navController: NavController,
    orderId: String
) {
    Scaffold(
        topBar = {
            BunnixTopBar(
                title = "Payment Verification",
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
            Text("Payment Verification: $orderId")
        }
    }
}