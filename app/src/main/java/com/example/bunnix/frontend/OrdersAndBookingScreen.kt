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
import androidx.compose.material3.HorizontalDivider // In M3, 'Divider' is now 'HorizontalDivider'
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.model.Order // Import your existing models
import com.example.bunnix.model.VendorViewModel


val productOrders = listOf(
    Order("#AB12C", "John Doe", listOf("Gourmet Burger", "Caesar Salad"), "45.99", "pending"),
    Order("#AB12D", "Jane Smith", listOf("Summer Dress"), "129.99", "processing"),
    Order("#AB12E", "Mike Chen", listOf("Table Lamp", "Wall Art"), "78.50", "shipped")
)

val serviceBookings = listOf(
    Booking("#SV001", "Emily Davis", "Hair Styling", "Today, 3:00 PM", "45", "confirmed"),
    Booking("#SV002", "Tom Wilson", "Computer Repair", "Tomorrow, 10:00 AM", "80", "confirmed"),
    Booking("#SV003", "Lisa Brown", "Spa Treatment", "Dec 7, 2:00 PM", "120", "pending")
)


@Composable
fun OrdersAndBookingsScreen(
    navController: NavController,
    viewModel: VendorViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Product, 1 = Service
    val orders by viewModel.allOrders
    val bookings by viewModel.allBookings

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
                // Use the real 'orders' list from ViewModel
                items(orders) { order -> ProductOrderCard(order) }
            } else {
                // Use the real 'bookings' list from ViewModel
                items(bookings) { booking -> ServiceBookingCard(booking) }
            }
        }
    }
}

@Composable
fun ProductOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User Avatar
                Surface(shape = CircleShape, modifier = Modifier.size(40.dp), color = Color.LightGray) {
                    // AsyncImage here for real user photo
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.id, fontWeight = FontWeight.Bold)
                    Text(order.customerName, color = Color.Gray, fontSize = 12.sp)
                }
                // Status Badge
                StatusBadge(order.status)
            }

            Text("Items:", modifier = Modifier.padding(top = 12.dp), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            order.items.forEach { item ->
                Text("• $item", color = Color.Gray, fontSize = 12.sp)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatNaira(order.price), color = Color(0xFFF2711C), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))

                // Chat Icon
                IconButton(onClick = {}) { Icon(Icons.Default.Chat, contentDescription = null, tint = Color.Gray) }

                if (order.status == "pending") {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Decline", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accept", color = Color.White, fontSize = 12.sp)
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
fun ServiceBookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Customer Avatar
                Surface(shape = CircleShape, modifier = Modifier.size(44.dp), color = Color.LightGray) {
                    // AsyncImage(model = booking.userImage...)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.id, fontWeight = FontWeight.Bold)
                    Text(booking.customerName, color = Color.Gray, fontSize = 14.sp)
                }
                StatusBadge(booking.status)
            }

            Spacer(Modifier.height(16.dp))
            Text(booking.serviceType, fontWeight = FontWeight.Medium)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(8.dp))
                Text(booking.dateTime, color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₦${booking.price}", color = Color(0xFFEA4335), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))

                IconButton(onClick = {}) { Icon(Icons.Default.Chat, contentDescription = null, tint = Color.Gray) }

                // Red Action Button as seen in image
                Button(
                    onClick = { /* Action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (booking.status == "pending") "Accept" else "Start Service", color = Color.White)
                }
            }
        }
    }
}