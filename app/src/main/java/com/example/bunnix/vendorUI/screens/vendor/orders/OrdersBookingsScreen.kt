package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.LightGrayBg
import com.example.bunnix.ui.theme.OrangePrimaryModern

@Composable
fun OrdersBookingsScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // ✅ NO SCAFFOLD - Just Column to avoid nested Scaffold issue
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
    ) {
        // ✅ Simple header (NO back arrow - this is a bottom nav destination)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = OrangePrimaryModern,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orders & Bookings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

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