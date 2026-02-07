package com.example.bunnix.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔔 Notifications", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))

        Text(
            "Order updates and booking alerts will show here.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


