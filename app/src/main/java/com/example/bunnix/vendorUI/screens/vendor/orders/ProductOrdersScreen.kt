package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bunnix.vendorUI.components.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductOrdersScreen(
    onOrderClick: (String) -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }
    val statusFilters = listOf("All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Orders") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // Filter Chips
            ScrollableTabRow(
                selectedTabIndex = statusFilters.indexOf(filterStatus),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                indicator = { }
            ) {
                statusFilters.forEach { filter ->
                    FilterChip(
                        selected = filterStatus == filter,
                        onClick = { filterStatus = filter },
                        label = { Text(filter) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Orders List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleOrders.filter {
                    filterStatus == "All" || it.status.equals(filterStatus, ignoreCase = true)
                }) { order ->
                    OrderCard(
                        orderNumber = order.orderNumber,
                        customerName = order.customerName,
                        amount = order.totalAmount,
                        status = order.status,
                        items = order.items,
                        date = order.date,
                        onClick = { onOrderClick(order.orderId) }
                    )
                }
            }
        }
    }
}