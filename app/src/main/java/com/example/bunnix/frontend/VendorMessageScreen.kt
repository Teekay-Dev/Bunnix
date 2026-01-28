package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MessagesScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                Text("Messages", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search conversations...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF3F3F3),
                        focusedContainerColor = Color(0xFFF3F3F3),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = { VendorBottomNav(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { ChatListItem("Gourmet Bites", "Hello! How can I help you today?", "1h ago", true) }
            item { ChatListItem("Style Hub", "Yes! We have it in stock.", "2h ago", false) }
        }
    }
}

@Composable
fun ChatListItem(name: String, message: String, time: String, hasUnread: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Surface(Modifier.size(50.dp), shape = CircleShape, color = Color.LightGray) {}
            if (hasUnread) {
                Surface(
                    color = Color.Red,
                    shape = CircleShape,
                    modifier = Modifier.size(16.dp).align(Alignment.TopEnd)
                ) {
                    Text("1", color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(message, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(time, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun ProductOrderCardDetailed(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Surface(Modifier.size(40.dp), shape = CircleShape, color = Color.LightGray) {}
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(order.id, fontWeight = FontWeight.Bold)
                    Text(order.customerName, color = Color.Gray, fontSize = 12.sp)
                    Text("2 hours ago", color = Color.LightGray, fontSize = 10.sp)
                }
                StatusBadge(order.status)
            }

            // Items Box
            Surface(
                Modifier.fillMaxWidth().padding(vertical = 12.dp),
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Items:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    order.items.forEach { item ->
                        Text("• $item", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₦${order.price}", color = Color(0xFFF2711C), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))

                // Gray Chat Bubble
                Surface(Modifier.size(36.dp), shape = CircleShape, color = Color(0xFFF3F3F3)) {
                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.padding(8.dp), tint = Color.Gray)
                }

                Spacer(Modifier.width(8.dp))

                if (order.status == "processing") {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.LocalShipping, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ship")
                    }
                }
            }
        }
    }
}