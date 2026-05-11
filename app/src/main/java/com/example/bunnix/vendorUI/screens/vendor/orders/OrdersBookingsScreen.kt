package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.OrangePrimaryModern
import com.example.bunnix.ui.theme.TextPrimary
import com.example.bunnix.viewmodel.OrdersViewModel
import com.example.bunnix.viewmodel.ProductOrder
import com.example.bunnix.viewmodel.ServiceBooking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersBookingsScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders & Bookings", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimaryModern)
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        Column(Modifier.padding(padding)) {
            CustomTabRow(selectedTabIndex = selectedTab, onTabClick = { selectedTab = it })

            when (selectedTab) {
                0 -> ProductOrdersScreen(viewModel) { navController.navigate("order_detail/$it") }
                1 -> ServiceBookingsScreen(viewModel) { navController.navigate("booking_detail/$it") }
            }
        }
    }
}

@Composable
fun CustomTabRow(selectedTabIndex: Int, onTabClick: (Int) -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Correct placement
    ) {
        CustomTab("Product Orders", selectedTabIndex == 0, { onTabClick(0) }, Modifier.weight(1f))
        CustomTab("Service Bookings", selectedTabIndex == 1, { onTabClick(1) }, Modifier.weight(1f))
    }
}

@Composable
fun CustomTab(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) OrangePrimaryModern else Color.White,
            contentColor = if (selected) Color.White else Color.Gray
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ✅ PRODUCT ORDERS LIST
@Composable
fun ProductOrdersScreen(viewModel: OrdersViewModel, onItemClick: (String) -> Unit) {
    val orders by viewModel.productOrders.collectAsStateWithLifecycle()

    if (orders.isEmpty()) {
        EmptyState("No Product Orders Yet")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                // ✅ FIX: Call the function inside the lambda
                OrderCard(order = order, onClick = { onItemClick(order.orderId) })
            }
        }
    }
}

// ✅ SERVICE BOOKINGS LIST
@Composable
fun ServiceBookingsScreen(viewModel: OrdersViewModel, onItemClick: (String) -> Unit) {
    val bookings by viewModel.serviceBookings.collectAsStateWithLifecycle()

    if (bookings.isEmpty()) {
        EmptyState("No Service Bookings Yet")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                // ✅ FIX: Call the function inside the lambda
                BookingCard(booking = booking, onClick = { onItemClick(booking.bookingId) })
            }
        }
    }
}

// ✅ UI COMPONENTS
@Composable
fun OrderCard(order: ProductOrder, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        // ✅ FIX: Use named parameter 'verticalAlignment' or place 'Arrangement' correctly
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                if (order.customerImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = order.customerImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(order.timeAgo, fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = order.items.joinToString(", "),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₦${order.amount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryModern
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = order.status)
            }
        }
    }
}

@Composable
fun BookingCard(booking: ServiceBooking, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        // ✅ FIX: Use named parameter
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(booking.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(booking.serviceName, fontSize = 13.sp, color = Color.DarkGray)
                Text(booking.bookingDate, fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₦${booking.amount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryModern
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = booking.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Processing", "Confirmed" -> Color(0xFFFFA726)
        "Delivered", "Completed" -> Color(0xFF66BB6A)
        "Cancelled" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(message, color = Color.Gray)
        }
    }
}