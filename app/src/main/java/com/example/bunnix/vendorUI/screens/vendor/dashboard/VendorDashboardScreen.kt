package com.example.bunnix.vendorUI.screens.vendor.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.components.*
import com.example.bunnix.vendorUI.navigation.VendorRoutes
import com.example.bunnix.viewmodel.VendorDashboardViewModel

// ✅ Global flag - shows verification dialog ONCE per app session
private var hasShownVerificationThisSession = false

@Composable
fun VendorDashboardScreen(
    navController: NavController,
    viewModel: VendorDashboardViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val stats by viewModel.dashboardStats.collectAsState()
    val recentOrders by viewModel.recentOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val businessName by viewModel.businessName.collectAsState()
    val isVerified by viewModel.isVerified.collectAsState()
    var showVerificationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()

        // ✅ Show verification ONLY ONCE per app session
        if (!isVerified && !hasShownVerificationThisSession) {
            showVerificationDialog = true
            hasShownVerificationThisSession = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrayBg)
                .verticalScroll(scrollState)
        ) {
            // ✅ BIG TOP BAR (NO SEARCH BAR)
            BigVendorTopBar(
                businessName = businessName ?: "My Business",
                isVerified = isVerified,
                isLoading = isLoading,
                onNotificationClick = { navController.navigate(VendorRoutes.NOTIFICATIONS) }
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
            QuickActions(
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

            // Week Performance
            WeekPerformanceCard()

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Verification Dialog
        if (showVerificationDialog) {
            VerificationPromptDialog(
                onGetVerified = {
                    showVerificationDialog = false
                    navController.navigate(VendorRoutes.GET_VERIFIED)
                },
                onDismiss = {
                    showVerificationDialog = false
                }
            )
        }
    }
}

// ✅ BIG TOP BAR - NO SEARCH BAR
@Composable
fun BigVendorTopBar(
    businessName: String,
    isVerified: Boolean,
    isLoading: Boolean,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF8C42),
                        OrangePrimaryModern
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ✅ TOP ROW: Logo Circle + "Bunnix" + Notification Bell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Logo + "Bunnix"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ✅ WHITE CIRCLE WITH ORANGE INSIDE
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(OrangePrimaryModern, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Bunnix Logo",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // ✅ "Bunnix" Text
                    Column {
                        Text(
                            text = "Bunnix",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Vendor Dashboard",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Right: Notification Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // ✅ WELCOME MESSAGE + BUSINESS NAME
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Welcome To Bunnix! 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else businessName,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ✅ STATS GRID
@SuppressLint("DefaultLocale")
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AttachMoney,
                value = if (isLoading) "..." else "₦${String.format("%,.0f", totalSales)}",
                label = "Total Sales",
                iconBackgroundColor = Color(0xFFE8F5E9),
                iconTint = Color(0xFF4CAF50)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                value = if (isLoading) "..." else "$totalOrders",
                label = "Total Orders",
                iconBackgroundColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                value = if (isLoading) "..." else "$bookings",
                label = "Bookings",
                iconBackgroundColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF9C27B0)
            )

            StatCard(
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
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconBackgroundColor: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier
            .height(140.dp)
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuickActions(
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Add,
                label = "Add Product",
                backgroundColor = Color(0xFF2196F3),
                onClick = onAddProductClick
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                label = "View Orders",
                backgroundColor = Color(0xFF00C853),
                onClick = onViewOrdersClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                label = "Bookings",
                backgroundColor = Color(0xFF9C27B0),
                onClick = onBookingsClick
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.Message,
                label = "Messages",
                backgroundColor = OrangePrimaryModern,
                onClick = onMessagesClick
            )
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = backgroundColor.copy(alpha = 0.3f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

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
                    if (it < 2) Spacer(modifier = Modifier.height(12.dp))
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
                orders.forEachIndexed { index, order ->
                    RecentOrderItem(
                        order = order,
                        onClick = { onOrderClick(order.orderId) }
                    )

                    if (index < orders.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
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
                text = "₦${String.format("%,.2f", order.amount)}",
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
            text = status.replaceFirstChar { it.uppercase() },
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFFFF8F0), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
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

@Composable
fun VerificationPromptDialog(
    onGetVerified: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {}
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Get Verified with Bunnix!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Build trust with customers by becoming a verified vendor. Get the blue checkmark badge today!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = onGetVerified,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimaryModern
                        )
                    ) {
                        Text(
                            text = "Get Verified Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class DashboardStats(
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