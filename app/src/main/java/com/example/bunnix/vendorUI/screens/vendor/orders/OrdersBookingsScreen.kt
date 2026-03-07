package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.OrangePrimaryModern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersBookingsScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orders & Bookings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangePrimaryModern
                )
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Custom Tab Row
            CustomTabRow(
                selectedTabIndex = selectedTab,
                onTabClick = { selectedTab = it }
            )

            // Content based on selected tab
            when (selectedTab) {
                0 -> ProductOrdersScreen(navController)
                1 -> ServiceBookingsScreen(navController)
            }
        }
    }
}

@Composable
fun CustomTabRow(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CustomTab(
            text = "Product Orders",
            selected = selectedTabIndex == 0,
            onClick = { onTabClick(0) },
            modifier = Modifier.weight(1f)
        )

        CustomTab(
            text = "Service Bookings",
            selected = selectedTabIndex == 1,
            onClick = { onTabClick(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CustomTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) OrangePrimaryModern else Color.White,
            contentColor = if (selected) Color.White else Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 0.dp else 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}