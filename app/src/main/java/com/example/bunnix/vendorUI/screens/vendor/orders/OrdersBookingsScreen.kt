package com.example.bunnix.vendorUI.screens.vendor.orders

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.*

data class OrderItem(
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val customerImage: String,
    val timeAgo: String,
    val items: List<String>,
    val total: Double,
    val status: String
) {
    val paymentStatus: Any = TODO()
    val totalAmount: Double = 0.0
    val date: String
}

val sampleOrders = listOf(
    OrderItem(
        orderId = "1",
        orderNumber = "#AB12C",
        customerName = "John Doe",
        customerImage = "",
        timeAgo = "2 hours ago",
        items = listOf("Gourmet Burger", "Caesar Salad"),
        total = 45.99,
        status = "pending"
    ),
    OrderItem(
        orderId = "2",
        orderNumber = "#AB12D",
        customerName = "Jane Smith",
        customerImage = "",
        timeAgo = "5 hours ago",
        items = listOf("Summer Dress"),
        total = 129.99,
        status = "processing"
    ),
    OrderItem(
        orderId = "3",
        orderNumber = "#AB12E",
        customerName = "Mike Chen",
        customerImage = "",
        timeAgo = "1 day ago",
        items = listOf("Table Lamp", "Throw Pillow"),
        total = 89.99,
        status = "shipped"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersBookingsScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Product Orders", "Service Bookings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders & Bookings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size)
                    ) {
                        Text(title)
                    }
                }
            }

            // Orders List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleOrders) { order ->
                    OrderCard(
                        order = order,
                        onAccept = { /* Accept order */ },
                        onDecline = { /* Decline order */ },
                        onShip = { /* Ship order */ }
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun OrderCard(
    order: OrderItem,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onShip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Customer Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.customerName.first().toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = order.orderNumber,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = order.customerName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.timeAgo,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                // Status Badge
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items List
            Column {
                Text(
                    text = "Items:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                order.items.forEach { item ->
                    Text(
                        text = "• $item",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", order.total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )

                // Action Buttons based on status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (order.status.lowercase()) {
                        "pending" -> {
                            OutlinedButton(
                                onClick = onDecline,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ErrorRed
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.5f))
                                )
                            ) {
                                Text("Decline")
                            }
                            Button(
                                onClick = onAccept,
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text("Accept")
                            }
                        }
                        "processing" -> {
                            IconButton(onClick = { /* Chat */ }) {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat")
                            }
                            Button(
                                onClick = onShip,
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ship")
                            }
                        }
                        else -> {
                            IconButton(onClick = { /* Chat */ }) {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(StatusPendingBg, StatusPendingText, "Pending")
        "processing" -> Triple(StatusProcessingBg, StatusProcessingText, "Processing")
        "shipped" -> Triple(StatusShippedBg, StatusShippedText, "Shipped")
        else -> Triple(SurfaceVariantLight, Color.Gray, status.replaceFirstChar { it.uppercase() })
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}