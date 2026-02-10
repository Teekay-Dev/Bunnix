package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrackOrderScreen(orderId: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0))) {

        // --- SIMULATED MAP BACKGROUND ---
        // In real code, replace this Box with the GoogleMap() composable
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Text("Live Map View", color = Color.Gray)
        }

        // --- TOP NAVIGATION ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.background(Color.White, CircleShape)
            ) { Icon(Icons.Default.ArrowBack, null) }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    "ID: $orderId",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // --- RIDER INFO CARD (Floating at bottom) ---
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rider Avatar
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFD35400))) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.align(Alignment.Center), tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Your delivery partner", color = Color.Gray, fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.background(Color(0xFFF0F0F0), CircleShape)
                    ) { Icon(Icons.Default.Call, null, tint = Color(0xFF1A56BE)) }
                }

                Divider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBike, null, tint = Color(0xFFD35400))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Estimated Arrival", color = Color.Gray, fontSize = 12.sp)
                        Text("10 - 15 Minutes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Full Details", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- PREVIEW BLOCK ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrackOrderPreview() {
    MaterialTheme {
        TrackOrderScreen(orderId = "#G8C5ZZ1OD")
    }
}