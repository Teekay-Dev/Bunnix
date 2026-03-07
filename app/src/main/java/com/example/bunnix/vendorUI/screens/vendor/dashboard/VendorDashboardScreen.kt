package com.example.bunnix.vendorUI.screens.vendor.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.components.*
import com.example.bunnix.vendorUI.navigation.VendorRoutes
import com.example.bunnix.viewmodel.VendorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    navController: NavController,
    viewModel: VendorDashboardViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val stats by viewModel.dashboardStats.collectAsState()
    val recentOrders by viewModel.recentOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = LightGrayBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Orange Header with Gradient
            DashboardHeader(
                availableBalance = stats?.availableBalance ?: 0.0,
                onWithdrawClick = { /* TODO: Implement withdraw */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Grid
            StatsGrid(
                totalSales = stats?.totalSales ?: 0.0,
                totalOrders = stats?.totalOrders ?: 0,
                bookings = stats?.bookings ?: 0,
                customers = stats?.customers ?: 0,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            QuickActionsSection(
                onAddProductClick = { navController.navigate(VendorRoutes.ADD_PRODUCT) },
                onViewOrdersClick = { navController.navigate(VendorRoutes.ORDERS) },
                onBookingsClick = { navController.navigate(VendorRoutes.ORDERS) },
                onMessagesClick = { navController.navigate(VendorRoutes.MESSAGES) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Orders
            RecentOrdersSection(
                orders = recentOrders,
                isLoading = isLoading,
                onViewAllClick = { navController.navigate(VendorRoutes.ORDERS) },
                onOrderClick = { orderId ->
                    navController.navigate(VendorRoutes.orderDetail(orderId))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // This Week's Performance
            WeekPerformanceCard()

            Spacer(modifier = Modifier.height(100.dp)) // Bottom nav space
        }
    }
}

// ============= DASHBOARD HEADER =============
@Composable
fun DashboardHeader(
    availableBalance: Double,
    onWithdrawClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF8C42),
                        Color(0xFFFF6B35)
                    )
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vendor Dashboard",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "My Business",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Trending Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .clickable { /* TODO: Navigate to Analytics */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Analytics",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available Balance",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%,.2f", availableBalance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onWithdrawClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(50.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Withdraw",
                            color = OrangePrimaryModern,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ============= STATS GRID =============
@Composable
fun StatsGrid(
    totalSales: Double,
    totalOrders: Int,
    bookings: Int,
    customers: Int,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AttachMoney,
                value = if (isLoading) "..." else "$${String.format("%,.0f", totalSales)}",
                label = "Total Sales",
                iconBackgroundColor = Color(0xFFE8F5E9),
                iconTint = Color(0xFF4CAF50)
            )

            ModernStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                value = if (isLoading) "..." else "$totalOrders",
                label = "Total Orders",
                iconBackgroundColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                value = if (isLoading) "..." else "$bookings",
                label = "Bookings",
                iconBackgroundColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF9C27B0)
            )

            ModernStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                value = if (isLoading) "..." else "$customers",
                label = "Customers",
                iconBackgroundColor = Color(0xFFFFF3E0),
                iconTint = OrangePrimaryModern
            )
        }
    }
}

@Composable
fun ModernStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconBackgroundColor: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// ============= QUICK ACTIONS =============
@Composable
fun QuickActionsSection(
    onAddProductClick: () -> Unit,
    onViewOrdersClick: () -> Unit,
    onBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Quick Actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Add,
                label = "Add Product",
                backgroundColor = Color(0xFF2196F3),
                onClick = onAddProductClick
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                label = "View Orders",
                backgroundColor = Color(0xFF00C853),
                onClick = onViewOrdersClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                label = "Bookings",
                backgroundColor = Color(0xFF9C27B0),
                onClick = onBookingsClick
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Message,
                label = "Messages",
                backgroundColor = OrangePrimaryModern,
                onClick = onMessagesClick,
                showBadge = true,
                badgeCount = 1
            )
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    showBadge: Boolean = false,
    badgeCount: Int = 0
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Badge
            if (showBadge && badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(Color(0xFFF44336), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ============= RECENT ORDERS =============
@Composable
fun RecentOrdersSection(
    orders: List<DashboardOrder>,
    isLoading: Boolean,
    onViewAllClick: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "View All",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryModern
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                repeat(3) {
                    ShimmerOrderCard()
                }
            } else if (orders.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ShoppingBag,
                    title = "No Orders Yet",
                    message = "Your recent orders will appear here",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                orders.forEach { order ->
                    RecentOrderItem(
                        order = order,
                        onClick = { onOrderClick(order.orderId) }
                    )

                    if (order != orders.last()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentOrderItem(
    order: DashboardOrder,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.orderNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.customerName,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            StatusBadge(status = order.status)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${String.format("%,.2f", order.amount)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${order.itemCount} items",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0) to OrangePrimaryModern
        "processing" -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        "shipped" -> Color(0xFFF3E5F5) to Color(0xFF9C27B0)
        "completed" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        else -> Color.LightGray to Color.DarkGray
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============= WEEK PERFORMANCE =============
@Composable
fun WeekPerformanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "This Week's Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Color(0xFFFFF8F0),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Weekday labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ============= DATA CLASSES =============
data class DashboardStats(
    val availableBalance: Double = 0.0,
    val totalSales: Double = 0.0,
    val totalOrders: Int = 0,
    val bookings: Int = 0,
    val customers: Int = 0
)

data class DashboardOrder(
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val amount: Double,
    val itemCount: Int,
    val status: String
)

