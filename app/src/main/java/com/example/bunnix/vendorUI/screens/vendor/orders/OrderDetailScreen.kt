package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.OrangePrimaryModern
import com.example.bunnix.vendorUI.components.BunnixTopBar
import com.example.bunnix.viewmodel.OrdersViewModel

@Composable
fun OrderDetailScreen(navController: NavController, orderId: String, viewModel: OrdersViewModel = hiltViewModel()) {
    val orders by viewModel.productOrders.collectAsState()
    val order = orders.find { it.orderId == orderId }

    Scaffold(topBar = { BunnixTopBar("Order Details", { navController.navigateUp() }) }, containerColor = Color(0xFFF8F9FE)) { padding ->
        if (order == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), Arrangement.spacedBy(16.dp)) {
                Card(colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(Modifier.height(8.dp))
                        Text("Customer: ${order.customerName}", color = Color.DarkGray)
                        Text("Total: ₦${order.amount.toInt()}", color = Color.DarkGray)
                        Text("Items: ${order.items.joinToString()}", color = Color.DarkGray)
                        Text("Status: ${order.status}", color = Color.DarkGray)
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    if (order.status == "Processing") {
                        Button({ viewModel.markDelivered(orderId); navController.navigateUp() }, Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(OrangePrimaryModern)) { Text("Mark Delivered") }
                    }
                    if (order.status != "Cancelled" && order.status != "Delivered") {
                        OutlinedButton({ viewModel.declineOrder(orderId); navController.navigateUp() }, Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Decline") }
                    }
                }
            }
        }
    }
}