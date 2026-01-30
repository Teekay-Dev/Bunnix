package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bunnix.model.Order
import com.example.bunnix.model.VendorViewModel

@Composable
fun VendorDashboardScreen(navController: NavController, viewModel: VendorViewModel = viewModel()) {
    val orders by viewModel.orders
    val totalSales by remember {derivedStateOf { viewModel.totalSales }} //red on derivedStateOf
    val balance by  remember { derivedStateOf { viewModel.availableBalance } }  //red on derivedStateOf

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).verticalScroll(rememberScrollState())
    ) {
        // --- ORANGE HEADER SECTION ---
        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFF6B00)).padding(20.dp)) {
            Text("Vendor Dashboard", color = Color.White.copy(alpha = 0.8f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("My Business", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Available Balance Card (Physical Cash Tracker)
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Available Balance", color = Color.White)
                        Text("₦${String.format("%,.2f", balance)}", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { /* Withdraw Logic */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))) {
                        Text("Withdraw", color = Color.White)
                    }
                }
            }
        }

        // --- STATS GRID ---
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                StatCard("Total Sales", "₦${totalSales}", Icons.Default.AttachMoney, Color(0xFF4CAF50), Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                StatCard("Total Orders", "${orders.size}", Icons.Default.ShoppingBag, Color(0xFF2196F3), Modifier.weight(1f))
            }
        }

        // --- QUICK ACTIONS ---
        Text("Quick Actions", Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
        Row(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionIcon(Icons.Default.Add, "Add Product", Color(0xFF2196F3)) { navController.navigate(Screen.AddProduct.route) } //red on QuickActionIcon
            QuickActionIcon(Icons.Default.ListAlt, "Inventory", Color(0xFF4CAF50)) { navController.navigate(Screen.ManageInventory.route) } //red on QuickActionIcon
            QuickActionIcon(Icons.Default.CalendarToday, "Bookings", Color(0xFF9C27B0)) { navController.navigate(Screen.VendorOrders.route) } //red on QuickActionIcon
        }
        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                navController.navigate(Screen.VendorOrders.route)
            }) {
                Text("View All", color = Color(0xFFFF6B00))
            }
        }

        if (viewModel.recentOrders.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                Text("No orders yet", color = Color.Gray)
            }
        } else {
            Column(Modifier.padding(bottom = 16.dp)) {
                viewModel.recentOrders.value.forEach { order ->
                    OrderItemRow(order = order)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

enum class OrderStatus(val label: String, val color: Color) {
    PENDING("Pending", Color(0xFFFF9800)),      // Orange
    PROCESSING("Processing", Color(0xFF2196F3)), // Blue
    COMPLETED("Completed", Color(0xFF4CAF50)),   // Green
    CANCELLED("Cancelled", Color(0xFFF44336))    // Red
}

@Composable
fun OrderItemRow(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon or Product Image Placeholder
            Surface(
                shape = CircleShape,
                color = Color(0xFFF5F5F5),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(8.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(text = "Order #${order.id?.takeLast(5)}", fontWeight = FontWeight.Bold)
                Text(text = "Total: $${order.total_price}", fontSize = 12.sp, color = Color.Gray)
            }

            // Status Badge
            val statusObj = OrderStatus.values().find { it.label.lowercase() == order.status.lowercase() }
                ?: OrderStatus.PENDING

            Surface(
                color = statusObj.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = statusObj.label,
                    color = statusObj.color,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



@Composable
fun QuickActionIcon(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(16.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}