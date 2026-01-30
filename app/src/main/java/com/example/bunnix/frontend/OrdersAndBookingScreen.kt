package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.items // Essential for LazyColumn items
import androidx.compose.material.icons.filled.Chat // For the Chat icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bunnix.model.Booking
import androidx.compose.foundation.lazy.items // Essential
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider // In M3, 'Divider' is now 'HorizontalDivider'
import androidx.compose.material3.OutlinedButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.model.Order // Import your existing models
import com.example.bunnix.model.VendorViewModel

@Composable
fun OrdersAndBookingsScreen(
    navController: NavController,
    viewModel: VendorViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) }
    val orders by viewModel.orders
    val bookings by viewModel.bookings

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Header with Back Arrow
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text("Orders & Bookings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                // The Figma Toggle (Red and Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFFF3F3F3), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Product Orders", "Service Bookings")
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    if (selectedTab == index) Color(0xFFF2711C) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8)).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                items(orders) { order -> ProductOrderCard(order, viewModel = viewModel) }
            } else {
                items(bookings) { booking -> ServiceBookingCard(booking, viewModel = viewModel) }
            }
        }
    }
}

@Composable
fun ProductOrderCard(order: Order, viewModel: VendorViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ... Header and Items section ...
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₦${order.total_price}", color = Color(0xFFF2711C), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))

                // Physical Payment Flow Buttons
                when (order.status) {
                    "pending" -> {
                        Button(onClick = { viewModel.updateOrderStatus(order.id!!, "processing") }) { Text("Accept") }
                    }
                    "shipped" -> {
                        Button(onClick = { viewModel.updateOrderStatus(order.id!!, "delivered") }) {
                            Text("Confirm Cash Received")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFE0B2) to Color(0xFFF57C00) // Peach/Orange
        "processing" -> Color(0xFFE3F2FD) to Color(0xFF1976D2) // Blue
        "confirmed" -> Color(0xFFE3F2FD) to Color(0xFF1976D2) // Blue for Bookings
        "shipped", "confirmed" -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2) // Purple
        "delivered", "completed" -> Color(0xFFE8F5E9) to Color(0xFF388E3C) // Green
        else -> Color.LightGray to Color.DarkGray
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun ServiceBookingCard(booking: Booking, viewModel: VendorViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, modifier = Modifier.size(44.dp), color = Color(0xFFF5F5F5)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Fix: id is Int?, so we use toString() or take a slice
                    Text("Booking #${booking.id.toString().takeLast(5)}", fontWeight = FontWeight.Bold)
                    // Note: You might need to fetch the customer name via their ID later
                    Text("Customer ID: ${booking.customer_id.take(8)}", color = Color.Gray, fontSize = 14.sp)
                }
                StatusBadge(booking.status)
            }

            Spacer(Modifier.height(16.dp))
            // Fix: Your model uses 'name' for the service name
            Text(
                booking.service_name ?: "Service Appointment", //red on name
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.CalendarToday, null, Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                // Note: If you don't have a dateTime string yet, we can use a placeholder
                Text("Scheduled Service", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₦${booking.price}", color = Color(0xFFF2711C), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))

                // Action Button logic for Physical Payment
                BookingActionButtons(booking = booking, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BookingList(bookings: List<Booking>, viewModel: VendorViewModel) {
    if (bookings.isEmpty()) {
        EmptyStateView("No service bookings yet") //red on EmptyStateView
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
            items(bookings) { booking ->
                ServiceBookingCard(booking = booking, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OrderActionButtons(order: Order, viewModel: VendorViewModel) {
    Row {
        if (order.status == "pending") {
            Button(onClick = { viewModel.updateOrderStatus(order.id!!, "processing") }) {
                Text("Accept")
            }
            OutlinedButton(onClick = { viewModel.updateOrderStatus(order.id!!, "declined") }) {
                Text("Decline")
            }
        }

        if (order.status == "processing") {
            Button(onClick = { viewModel.updateOrderStatus(order.id!!, "shipped") }) {
                Text("Mark as Shipped")
            }
        }

        if (order.status == "shipped") {
            Button(onClick = { viewModel.updateOrderStatus(order.id!!, "delivered") }) {
                Text("Confirm Delivery & Payment")
            }
        }
    }
}

@Composable
fun BookingActionButtons(booking: Booking, viewModel: VendorViewModel) {
    Row {
        when (booking.status) {
            "pending" -> {
                Button(onClick = { viewModel.updateBookingStatus(booking.id!!, "confirmed") }) {
                    Text("Accept Booking")
                }
            }
            "confirmed" -> {
                Button(onClick = { viewModel.updateBookingStatus(booking.id!!, "started") }) {
                    Text("Start Service")
                }
            }
            "started" -> {
                Button(onClick = { viewModel.updateBookingStatus(booking.id!!, "completed") }) {
                    Text("Mark as Paid & Finished")
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventBusy, // Or Icons.Default.ShoppingBag
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}