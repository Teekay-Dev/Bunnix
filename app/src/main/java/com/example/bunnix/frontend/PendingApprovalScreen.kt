package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PendingApprovalScreen(
    isApproved: Boolean = false, // This should come from observing the status field
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isApproved) {
            // PENDING STATE
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF9800) // Orange
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Application Pending",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Our admin team is reviewing your business details. You'll be notified once approved!",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            // APPROVED STATE
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF4CAF50) // Green for success
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Business Approved!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Proceed to Dashboard", color = Color.White)
            }
        }
    }
}