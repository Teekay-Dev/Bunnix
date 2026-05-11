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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bunnix.OrangeLight
import com.example.bunnix.OrangeSoft
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
            // ✅ BEAUTIFUL GRADIENT TOP BAR
            ModernVendorTopBar(
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

            // ✅ QUICK ACTIONS (NOW WITH INVENTORY!)
            QuickActions(
                onInventoryClick = { navController.navigate("vendor/inventory") }, // ⭐ NEW!
                onAddProductClick = { navController.navigate(VendorRoutes.ADD_PRODUCT) },
                onViewOrdersClick = { navController.navigate(VendorRoutes.ORDERS) },
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

// ✅ MODERN GRADIENT TOP BAR
@Composable
fun ModernVendorTopBar(
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
                        OrangePrimaryModern,
                        OrangeLight
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // TOP ROW: Logo + Notification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Logo + Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Elevated White Circle with Orange Inside
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(8.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            OrangePrimaryModern,
                                            OrangeLight
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Bunnix Logo",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Bunnix",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Vendor Portal",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right: Notification Bell
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .clickable(onClick = onNotificationClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // WELCOME MESSAGE + BUSINESS NAME
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome! 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else businessName,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isVerified) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Verified",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ BEAUTIFIED STATS GRID
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
        Text(
            text = "Today's Overview",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AttachMoney,
                value = if (isLoading) "..." else "₦${String.format("%,.0f", totalSales)}",
                label = "Total Sales",
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF4CAF50)),
                iconBackgroundColor = Color(0xFFE8F5E9)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                value = if (isLoading) "..." else "$totalOrders",
                label = "Orders",
                gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF2196F3)),
                iconBackgroundColor = Color(0xFFE3F2FD)
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
                gradientColors = listOf(Color(0xFFAB47BC), Color(0xFF9C27B0)),
                iconBackgroundColor = Color(0xFFF3E5F5)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                value = if (isLoading) "..." else "$customers",
                label = "Customers",
                gradientColors = listOf(OrangeLight, OrangePrimaryModern),
                iconBackgroundColor = OrangeSoft
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
    gradientColors: List<Color>,
    iconBackgroundColor: Color
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = gradientColors.first().copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient Background (subtle)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                iconBackgroundColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

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
                        tint = gradientColors.last(),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ✅ QUICK ACTIONS (NOW 6 BUTTONS WITH INVENTORY!)
@Composable
fun QuickActions(
    onInventoryClick: () -> Unit, // ⭐ NEW!
    onAddProductClick: () -> Unit,
    onViewOrdersClick: () -> Unit,
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

        // ROW 1: Inventory + Add Product
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inventory,
                label = "Inventory",
                backgroundColor = Color(0xFFFF6B35), // Orange
                onClick = onInventoryClick
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Add,
                label = "Add Product",
                backgroundColor = Color(0xFF2196F3), // Blue
                onClick = onAddProductClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ROW 2: Orders + Messages
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ShoppingBag,
                label = "Orders",
                backgroundColor = Color(0xFF00C853), // Green
                onClick = onViewOrdersClick
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.Message,
                label = "Messages",
                backgroundColor = Color(0xFF9C27B0), // Purple
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
            .height(100.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = backgroundColor.copy(alpha = 0.4f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

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
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ✅ BEAUTIFIED RECENT ORDERS
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
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
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
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OrangePrimaryModern
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
        "pending" -> OrangeSoft to OrangePrimaryModern
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
            fontWeight = FontWeight.Bold
        )
    }
}

// ✅ BEAUTIFIED WEEK PERFORMANCE
@Composable
fun WeekPerformanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
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
                    text = "Week Performance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    color = Color(0xFF00C853).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF00C853),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "+12.5%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00C853)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Simple bar chart placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                OrangeSoft,
                                Color.White
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Chart Coming Soon",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// ✅ VERIFICATION DIALOG
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
                .align(Alignment.Center)
                .shadow(16.dp, RoundedCornerShape(28.dp)),
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
                            .size(90.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFE3F2FD),
                                        Color(0xFF90CAF9)
                                    )
                                ),
                                CircleShape
                            ),
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
                        text = "Get Verified!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Build trust with customers by becoming a verified vendor. Get the blue checkmark badge today!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
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
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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

// Data classes
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

// ===== PREVIEWS =====
// ===== PREVIEWS =====

@Preview(showBackground = true, showSystemUi = true, name = "Dashboard - Full")
@Composable
fun VendorDashboardScreenStaticPreview() {
    BunnixTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGrayBg)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar
                ModernVendorTopBar(
                    businessName = "Tech Store Pro",
                    isVerified = true,
                    isLoading = false,
                    onNotificationClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                StatsGrid(
                    totalSales = 125000.0,
                    totalOrders = 45,
                    bookings = 23,
                    customers = 156,
                    isLoading = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions
                QuickActions(
                    onInventoryClick = {},
                    onAddProductClick = {},
                    onViewOrdersClick = {},
                    onMessagesClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Orders with sample data
                RecentOrdersSection(
                    orders = listOf(
                        DashboardOrder(
                            orderId = "1",
                            orderNumber = "#ORD-001",
                            customerName = "John Doe",
                            amount = 45000.0,
                            itemCount = 3,
                            status = "pending"
                        ),
                        DashboardOrder(
                            orderId = "2",
                            orderNumber = "#ORD-002",
                            customerName = "Jane Smith",
                            amount = 23000.0,
                            itemCount = 1,
                            status = "completed"
                        )
                    ),
                    isLoading = false,
                    onViewAllClick = {},
                    onOrderClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                WeekPerformanceCard()

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Top Bar")
@Composable
fun TopBarPreview() {
    BunnixTheme {
        ModernVendorTopBar(
            businessName = "Tech Store Pro",
            isVerified = true,
            isLoading = false,
            onNotificationClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Stats Grid")
@Composable
fun StatsGridPreview() {
    BunnixTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            StatsGrid(
                totalSales = 125000.0,
                totalOrders = 45,
                bookings = 23,
                customers = 156,
                isLoading = false
            )
        }
    }
}

@Preview(showBackground = true, name = "Quick Actions")
@Composable
fun QuickActionsPreview() {
    BunnixTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrayBg)
                .padding(16.dp)
        ) {
            QuickActions(
                onInventoryClick = {},
                onAddProductClick = {},
                onViewOrdersClick = {},
                onMessagesClick = {}
            )
        }
    }
}