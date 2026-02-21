package com.example.bunnix.vendorUI.screens.vendor.orders

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.SuccessGreen
import com.example.bunnix.ui.theme.WarningYellow

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
                title = {
                    Text(
                        "Orders & Bookings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ProductOrdersContent(
                    onOrderClick = { orderId ->
                        navController.navigate("order_detail/$orderId")
                    }
                )
                1 -> ServiceBookingsContent(
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    }
                )
            }
        }
    }
}

@Composable
fun ProductOrdersContent(
    onOrderClick: (String) -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }
    val statusFilters = listOf("All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled")

    Column {
        ScrollableFilterChips(
            filters = statusFilters,
            selectedFilter = filterStatus,
            onFilterSelected = { filterStatus = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleOrders.filter {
                filterStatus == "All" || it.status.equals(filterStatus, ignoreCase = true)
            }) { order ->
                OrderListCard(
                    order = order,
                    onClick = { onOrderClick(order.orderId) }
                )
            }
        }
    }
}

@Composable
fun ServiceBookingsContent(
    onBookingClick: (String) -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }
    val statusFilters = listOf("All", "Requested", "Confirmed", "In Progress", "Completed", "Cancelled")

    Column {
        ScrollableFilterChips(
            filters = statusFilters,
            selectedFilter = filterStatus,
            onFilterSelected = { filterStatus = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleBookings.filter {
                filterStatus == "All" || it.status.equals(filterStatus, ignoreCase = true)
            }) { booking ->
                BookingListCard(
                    booking = booking,
                    onClick = { onBookingClick(booking.bookingId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = filters.indexOf(selectedFilter),
        containerColor = MaterialTheme.colorScheme.background,
        edgePadding = 16.dp,
        indicator = { },
        divider = { }
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun OrderListCard(
    order: OrderItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        order.customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "${order.items} item${if (order.items > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        order.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "₦${String.format("%,.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (order.paymentStatus == "awaiting_verification") {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Payment verification required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BookingListCard(
    booking: BookingItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        booking.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        booking.customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BookingStatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${booking.date} at ${booking.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    booking.bookingNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "₦${String.format("%,.2f", booking.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (color, containerColor) = when (status.lowercase()) {
        "pending" -> Pair(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        "processing" -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        "shipped" -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
        "delivered" -> Pair(SuccessGreen, SuccessGreen.copy(alpha = 0.1f))
        "cancelled" -> Pair(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surfaceVariant)
        else -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun BookingStatusBadge(status: String) {
    val (color, containerColor) = when (status.lowercase()) {
        "requested" -> Pair(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        "confirmed" -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        "in progress" -> Pair(WarningYellow, WarningYellow.copy(alpha = 0.1f))
        "completed" -> Pair(SuccessGreen, SuccessGreen.copy(alpha = 0.1f))
        "cancelled" -> Pair(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surfaceVariant)
        else -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ✅ Data Classes
data class OrderItem(
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val totalAmount: Double,
    val status: String,
    val items: Int,
    val date: String,
    val paymentStatus: String,
    val paymentMethod: String,
    val paymentVerified: Boolean,
    val vendorId: String,
    val vendorName: String
)

data class BookingItem(
    val bookingId: String,
    val bookingNumber: String,
    val customerName: String,
    val serviceName: String,
    val price: Double,
    val status: String,
    val date: String,
    val time: String
)

// ✅ Fixed Sample Data
val sampleOrders = listOf(
    OrderItem(
        orderId = "1",
        orderNumber = "BNX-20240221-001",
        customerName = "John Doe",
        totalAmount = 45999.0,
        status = "pending",
        items = 2,
        date = "Today, 2:30 PM",
        paymentStatus = "awaiting_verification",
        paymentMethod = "Bank Transfer",
        paymentVerified = false,
        vendorId = "vendor1",
        vendorName = "My Store"
    ),
    OrderItem(
        orderId = "2",
        orderNumber = "BNX-20240221-002",
        customerName = "Jane Smith",
        totalAmount = 129999.0,
        status = "processing",
        items = 1,
        date = "Today, 11:20 AM",
        paymentStatus = "verified",
        paymentMethod = "Bank Transfer",
        paymentVerified = true,
        vendorId = "vendor1",
        vendorName = "My Store"
    ),
    OrderItem(
        orderId = "3",
        orderNumber = "BNX-20240220-003",
        customerName = "Mike Chen",
        totalAmount = 78500.0,
        status = "shipped",
        items = 3,
        date = "Yesterday",
        paymentStatus = "verified",
        paymentMethod = "Bank Transfer",
        paymentVerified = true,
        vendorId = "vendor1",
        vendorName = "My Store"
    ),
    OrderItem(
        orderId = "4",
        orderNumber = "BNX-20240219-004",
        customerName = "Sarah Johnson",
        totalAmount = 250000.0,
        status = "delivered",
        items = 5,
        date = "Feb 19, 2024",
        paymentStatus = "verified",
        paymentMethod = "Bank Transfer",
        paymentVerified = true,
        vendorId = "vendor1",
        vendorName = "My Store"
    ),
    OrderItem(
        orderId = "5",
        orderNumber = "BNX-20240218-005",
        customerName = "David Wilson",
        totalAmount = 15000.0,
        status = "cancelled",
        items = 1,
        date = "Feb 18, 2024",
        paymentStatus = "refunded",
        paymentMethod = "Bank Transfer",
        paymentVerified = false,
        vendorId = "vendor1",
        vendorName = "My Store"
    )
)

val sampleBookings = listOf(
    BookingItem("1", "BKN-20240221-001", "Alice Brown", "Hair Styling", 5000.0, "requested", "Feb 22, 2024", "10:00 AM"),
    BookingItem("2", "BKN-20240221-002", "Bob Davis", "Home Cleaning", 15000.0, "confirmed", "Feb 23, 2024", "2:00 PM"),
    BookingItem("3", "BKN-20240220-003", "Carol White", "Massage Therapy", 12000.0, "in progress", "Feb 21, 2024", "4:30 PM"),
    BookingItem("4", "BKN-20240219-004", "Dan Miller", "Photography Session", 45000.0, "completed", "Feb 20, 2024", "9:00 AM")
)