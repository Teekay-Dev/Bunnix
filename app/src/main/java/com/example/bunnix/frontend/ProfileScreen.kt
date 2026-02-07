package com.example.bunnix.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👤 Profile", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))

        Text(
            "Customer account settings will live here.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
