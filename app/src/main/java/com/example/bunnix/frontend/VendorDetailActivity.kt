package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.models.VendorProfile // FIXED: Correct import
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay

// Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val StarYellow = Color(0xFFFFB800)

enum class VendorTab {
    Products, Services, Reviews, About
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendor: VendorProfile, // FIXED: Use VendorProfile instead of separate params
    onBack: () -> Unit = {},
    onChat: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onServiceClick: (String) -> Unit = {},
    onBookService: () -> Unit = {},
    onBuyProduct: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(VendorTab.Products) }
    var isLiked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        vendor.businessName,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isLiked = !isLiked }) {
                        val scale by animateFloatAsState(
                            targetValue = if (isLiked) 1.3f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "like"
                        )

                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Icon(
                                if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) Color(0xFFEF4444) else TextPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .scale(scale)
                            )
                        }
                    }

                    IconButton(onClick = { }) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.White
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (selectedTab == VendorTab.Services) {
                ExtendedFloatingActionButton(
                    onClick = onBookService,
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    text = { Text("Book Service") },
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                VendorHeader(vendor = vendor)

                QuickStatsRow(
                    productCount = 156,
                    serviceCount = 12,
                    responseTime = "< 1hr"
                )

                ActionButtonsRow(onChat = onChat)

                TabSelection(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                when (selectedTab) {
                    VendorTab.Products -> ProductsTab(onProductClick)
                    VendorTab.Services -> ServicesTab(onServiceClick)
                    VendorTab.Reviews -> ReviewsTab()
                    VendorTab.About -> AboutTab(vendor = vendor)
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun VendorHeader(vendor: VendorProfile) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(OrangePrimary, OrangeLight)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(100.dp)
                        .offset(y = (-70).dp),
                    border = BorderStroke(4.dp, Color.White),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(OrangeSoft)
                    ) {
                        Text(
                            vendor.businessName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = OrangePrimary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-60).dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            vendor.businessName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )

                        if (vendor.rating > 4.0) {
                            Surface(
                                color = TealAccent.copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = TealAccent,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        color = OrangeSoft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            vendor.category,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = OrangePrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            vendor.rating.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            "(${vendor.totalReviews} reviews)",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    productCount: Int = 0,
    serviceCount: Int = 0,
    responseTime: String = "< 1hr"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Products", productCount.toString(), Icons.Default.Inventory, Modifier.weight(1f))
        StatCard("Services", serviceCount.toString(), Icons.Default.Build, Modifier.weight(1f))
        StatCard("Response", responseTime, Icons.Default.Schedule, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Text(
                label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(onChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onChat,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chat", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f))
        ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Call", fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun TabSelection(
    selectedTab: VendorTab,
    onTabSelected: (VendorTab) -> Unit
) {
    val tabs = VendorTab.values()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "tab"
                )

                Surface(
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.scale(scale),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) OrangePrimary else Color.Transparent
                ) {
                    Text(
                        tab.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsTab(onProductClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Featured Products",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) { index ->
                FeaturedProductCard(
                    name = "Product ${index + 1}",
                    price = 15000.0 + (index * 5000),
                    onClick = { onProductClick("prod_$index") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "All Products",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        repeat(4) { index ->
            ProductListItem(
                name = "iPhone 15 Pro Case ${index + 1}",
                price = 15000.0,
                rating = 4.5,
                sold = 120,
                onClick = { onProductClick("prod_list_$index") }
            )
            if (index < 3) {
                Divider(color = TextTertiary.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
private fun FeaturedProductCard(name: String, price: Double, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(OrangeSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Inventory,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatCurrency(price),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun ProductListItem(
    name: String,
    price: Double,
    rating: Double,
    sold: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = OrangeSoft,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.Inventory,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "$rating",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "• $sold sold",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    formatCurrency(price),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = OrangePrimary
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .background(OrangePrimary, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to cart",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ServicesTab(onServiceClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Available Services",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        repeat(4) { index ->
            ServiceCard(
                name = "Phone Repair Service ${index + 1}",
                price = 5000.0 + (index * 2000),
                duration = "${30 + (index * 15)} mins",
                description = "Professional repair service with warranty",
                onClick = { onServiceClick("service_$index") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ServiceCard(
    name: String,
    price: Double,
    duration: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = TealAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(70.dp)
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = TealAccent,
                    modifier = Modifier.padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text(
                    description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(duration, fontSize = 13.sp, color = TextSecondary)
                    }

                    Text(
                        "•",
                        color = TextTertiary
                    )

                    Text(
                        formatCurrency(price),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = TealAccent
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}

@Composable
private fun ReviewsTab() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "4.8",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        color = OrangePrimary
                    )
                    Row {
                        repeat(5) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = StarYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        "128 reviews",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(modifier = Modifier.weight(1f)) {
                    RatingBar(5, 85)
                    RatingBar(4, 30)
                    RatingBar(3, 8)
                    RatingBar(2, 3)
                    RatingBar(1, 2)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Recent Reviews",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        repeat(3) {
            ReviewItem()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RatingBar(stars: Int, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            stars.toString(),
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.width(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        LinearProgressIndicator(
            progress = count / 100f,
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (stars >= 4) StarYellow else OrangePrimary,
            trackColor = TextTertiary.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            count.toString(),
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
private fun ReviewItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = OrangeSoft,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "JD",
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = StarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "2 days ago",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Great service! The vendor was very professional and delivered exactly what I needed. Highly recommended for anyone looking for quality products.",
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = SurfaceLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Verified Purchase",
                    fontSize = 11.sp,
                    color = TealAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AboutTab(vendor: VendorProfile) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "About Vendor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    vendor.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                InfoRow(Icons.Default.LocationOn, "Address", vendor.address)
                InfoRow(Icons.Default.Schedule, "Working Hours", "Mon-Sat: 9AM - 6PM")
                InfoRow(Icons.Default.Payment, "Payment Methods", "Bank Transfer, Cash, POS")

                if (vendor.phone.isNotEmpty()) {
                    InfoRow(Icons.Default.Phone, "Phone", vendor.phone)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = TextTertiary
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun VendorDetailScreenPreview() {
    val mockVendor = VendorProfile(
        vendorId = "1",
        businessName = "TechHub Store",
        category = "Electronics",
        description = "TechHub Store is your one-stop shop for all things tech. We specialize in premium electronics, accessories, and repair services.",
        rating = 4.8,
        totalReviews = 128,
        address = "123 Tech Street, Lagos, Nigeria",
        phone = "+234 123 456 7890"
    )

    BunnixTheme {
        VendorDetailScreen(vendor = mockVendor)
    }
}