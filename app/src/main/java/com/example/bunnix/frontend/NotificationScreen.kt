package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Notification
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val YellowAccent = Color(0xFFFFBE0B)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)
private val WarningYellow = Color(0xFFF59E0B)
private val InfoBlue = Color(0xFF3B82F6)

// Notification types using your model's "type" field
enum class NotificationType(val icon: ImageVector, val color: Color, val bgColor: Color) {
    ORDER(Icons.Default.ShoppingBag, SuccessGreen, SuccessGreen.copy(alpha = 0.1f)),
    BOOKING(Icons.Default.CalendarToday, InfoBlue, InfoBlue.copy(alpha = 0.1f)),
    PROMO(Icons.Default.LocalOffer, OrangePrimary, OrangeSoft),
    SYSTEM(Icons.Default.Info, WarningYellow, WarningYellow.copy(alpha = 0.1f)),
    MESSAGE(Icons.Default.Chat, PurpleAccent, PurpleAccent.copy(alpha = 0.1f)),
    PAYMENT(Icons.Default.Payment, TealAccent, TealAccent.copy(alpha = 0.1f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    currentUserId: String = "user_123"
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Your exact Notification model
    var notifications by remember {
        mutableStateOf(
            listOf(
                Notification(
                    notificationId = "notif_001",
                    userId = "user_123",
                    type = "ORDER",
                    title = "Payment Confirmed! 🎉",
                    message = "Your payment for Order #ORD-2024-001 has been verified by Glow Up Salon. Your order is now being processed and will be shipped soon.",
                    relatedId = "ORD-2024-001",
                    relatedType = "order",
                    imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
                    isRead = false,
                    createdAt = Timestamp.now()
                ),
                Notification(
                    notificationId = "notif_002",
                    userId = "user_123",
                    type = "BOOKING",
                    title = "Booking Reminder",
                    message = "Your appointment with TechFix Pro is tomorrow at 2:00 PM. Don't forget to bring your device and payment receipt!",
                    relatedId = "BK-2024-002",
                    relatedType = "booking",
                    imageUrl = "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400",
                    isRead = false,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)))
                ),
                Notification(
                    notificationId = "notif_003",
                    userId = "user_123",
                    type = "PROMO",
                    title = "Weekend Special! 🔥",
                    message = "Get 30% off all spa services this weekend only! Book now and treat yourself to some well-deserved relaxation.",
                    relatedId = "",
                    relatedType = "promotion",
                    imageUrl = "",
                    isRead = false,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)))
                ),
                Notification(
                    notificationId = "notif_004",
                    userId = "user_123",
                    type = "ORDER",
                    title = "Order Shipped 🚚",
                    message = "Your order #ORD-2024-003 has been shipped! Track your package in real-time. Estimated delivery: 2-3 business days.",
                    relatedId = "ORD-2024-003",
                    relatedType = "order",
                    imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=400",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5)))
                ),
                Notification(
                    notificationId = "notif_005",
                    userId = "user_123",
                    type = "PAYMENT",
                    title = "Refund Processed",
                    message = "Your refund of ₦15,000 for cancelled booking #BK-2024-001 has been processed. It will reflect in your account within 3-5 business days.",
                    relatedId = "BK-2024-001",
                    relatedType = "booking",
                    imageUrl = "",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
                ),
                Notification(
                    notificationId = "notif_006",
                    userId = "user_123",
                    type = "MESSAGE",
                    title = "New Message",
                    message = "FreshMart Grocery: \"Your organic vegetable basket is ready for pickup!\"",
                    relatedId = "chat_003",
                    relatedType = "chat",
                    imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=400",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)))
                ),
                Notification(
                    notificationId = "notif_007",
                    userId = "user_123",
                    type = "SYSTEM",
                    title = "Welcome to Bunnix! 👋",
                    message = "Complete your profile to get personalized recommendations and exclusive deals tailored just for you.",
                    relatedId = "",
                    relatedType = "system",
                    imageUrl = "",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)))
                ),
                Notification(
                    notificationId = "notif_008",
                    userId = "user_123",
                    type = "BOOKING",
                    title = "Booking Cancelled",
                    message = "Your booking with HomeClean Services has been cancelled as requested. A refund has been initiated.",
                    relatedId = "BK-2024-005",
                    relatedType = "booking",
                    imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=400",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)))
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Orders", "Bookings", "Promos", "Unread")

    val filteredNotifications = notifications.filter { notif ->
        when (selectedFilter) {
            "Orders" -> notif.type == "ORDER" || notif.type == "PAYMENT"
            "Bookings" -> notif.type == "BOOKING"
            "Promos" -> notif.type == "PROMO"
            "Unread" -> !notif.isRead
            else -> true
        }
    }.sortedByDescending { it.createdAt?.toDate() }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            ModernNotificationTopBar(
                unreadCount = unreadCount,
                onMarkAllRead = {
                    notifications = notifications.map { it.copy(isRead = true) }
                },
                onBack = { navController.popBackStack() }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Filter Chips
                FilterChips(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    notifications = notifications
                )

                // Stats Row
                NotificationStats(notifications = notifications)

                // Notifications List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = filteredNotifications,
                        key = { it.notificationId }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                // Mark as read
                                notifications = notifications.map {
                                    if (it.notificationId == notification.notificationId)
                                        it.copy(isRead = true)
                                    else
                                        it
                                }
                                // Navigate based on relatedType
                                when (notification.relatedType) {
                                    "order" -> navController.navigate("order_detail/${notification.relatedId}")
                                    "booking" -> navController.navigate("booking_detail/${notification.relatedId}")
                                    "chat" -> navController.navigate("chat_detail/${notification.relatedId}")
                                    else -> { }
                                }
                            },
                            onDismiss = {
                                notifications = notifications.filter {
                                    it.notificationId != notification.notificationId
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernNotificationTopBar(
    unreadCount: Int,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    AnimatedVisibility(
                        visible = unreadCount > 0,
                        enter = scaleIn() + fadeIn()
                    ) {
                        Surface(
                            color = OrangePrimary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                unreadCount.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = OrangePrimary
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = onMarkAllRead,
                    enabled = unreadCount > 0
                ) {
                    Text(
                        "Mark all read",
                        color = if (unreadCount > 0) OrangePrimary else TextTertiary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
private fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    notifications: List<Notification>
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val count = when (filter) {
                    "All" -> notifications.size
                    "Orders" -> notifications.count { it.type == "ORDER" || it.type == "PAYMENT" }
                    "Bookings" -> notifications.count { it.type == "BOOKING" }
                    "Promos" -> notifications.count { it.type == "PROMO" }
                    "Unread" -> notifications.count { !it.isRead }
                    else -> 0
                }

                val isSelected = selectedFilter == filter

                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelect(filter) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(filter)
                            if (count > 0 && filter != "All") {
                                Surface(
                                    color = if (isSelected) Color.White.copy(alpha = 0.3f) else TextTertiary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        count.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationStats(notifications: List<Notification>) {
    val todayCount = notifications.count {
        it.createdAt?.toDate()?.let { date ->
            val now = Date()
            val diff = now.time - date.time
            diff < TimeUnit.DAYS.toMillis(1)
        } ?: false
    }

    val orderCount = notifications.count { it.type == "ORDER" && !it.isRead }
    val promoCount = notifications.count { it.type == "PROMO" && !it.isRead }

    AnimatedVisibility(
        visible = todayCount > 0 || orderCount > 0 || promoCount > 0,
        enter = expandVertically() + fadeIn()
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (todayCount > 0) {
                    StatCard(
                        icon = Icons.Default.Today,
                        value = todayCount.toString(),
                        label = "Today",
                        color = TealAccent
                    )
                }
                if (orderCount > 0) {
                    StatCard(
                        icon = Icons.Default.ShoppingBag,
                        value = orderCount.toString(),
                        label = "Orders",
                        color = SuccessGreen
                    )
                }
                if (promoCount > 0) {
                    StatCard(
                        icon = Icons.Default.LocalOffer,
                        value = promoCount.toString(),
                        label = "Deals",
                        color = OrangePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier.weight(1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = color
                )
                Text(
                    label,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val notifType = try {
        NotificationType.valueOf(notification.type)
    } catch (e: IllegalArgumentException) {
        NotificationType.SYSTEM
    }

    val isUnread = !notification.isRead

    // Swipe to dismiss animation
    var isDismissed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isDismissed) 0.9f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDismissed) 0f else 1f,
        animationSpec = tween(200),
        label = "alpha"
    )

    AnimatedVisibility(
        visible = !isDismissed,
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .scale(scale)
                .alpha(alpha),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUnread) OrangeSoft else Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isUnread) 2.dp else 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon or Image
                if (notification.imageUrl.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(notification.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(notifType.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = notifType.icon,
                            contentDescription = null,
                            tint = notifType.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier.weight(1f)  // This makes the column take available space
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)  // FIXED: Changed from weight(1f, fill = false) to just weight(1f)
                        ) {
                            Text(
                                text = notification.title,
                                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)  // FIXED: Changed from weight(1f, fill = false) to weight(1f)
                            )

                            // Type badge
                            Surface(
                                color = notifType.bgColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = notification.type.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 9.sp,
                                    color = notifType.color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Unread indicator
                        if (isUnread) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(OrangePrimary, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        color = if (isUnread) TextSecondary else TextTertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(14.dp)
                        )

                        Text(
                            text = formatNotificationTime(notification.createdAt),
                            fontSize = 12.sp,
                            color = TextTertiary
                        )

                        // Related ID chip
                        if (notification.relatedId.isNotEmpty()) {
                            Surface(
                                color = notifType.color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "#${notification.relatedId.takeLast(6)}",
                                    fontSize = 10.sp,
                                    color = notifType.color,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Dismiss button
                IconButton(
                    onClick = {
                        isDismissed = true
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(200)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(18.dp),
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}

private fun formatNotificationTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""

    val date = timestamp.toDate()
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
        diff < TimeUnit.DAYS.toMillis(7) -> SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun NotificationScreenPreview() {
    BunnixTheme {
        NotificationScreen(
            navController = rememberNavController(),
            currentUserId = "user_123"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationCardPreview() {
    BunnixTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Unread notification
            NotificationCard(
                notification = Notification(
                    notificationId = "1",
                    userId = "user_123",
                    type = "ORDER",
                    title = "Payment Confirmed!",
                    message = "Your payment has been verified by the vendor.",
                    relatedId = "ORD-001",
                    relatedType = "order",
                    isRead = false,
                    createdAt = Timestamp.now()
                ),
                onClick = {},
                onDismiss = {}
            )

            // Read notification
            NotificationCard(
                notification = Notification(
                    notificationId = "2",
                    userId = "user_123",
                    type = "PROMO",
                    title = "Weekend Special!",
                    message = "Get 20% off all services this weekend.",
                    isRead = true,
                    createdAt = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
                ),
                onClick = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatCardPreview() {
    BunnixTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Today,
                value = "5",
                label = "Today",
                color = TealAccent
            )
            StatCard(
                icon = Icons.Default.ShoppingBag,
                value = "3",
                label = "Orders",
                color = SuccessGreen
            )
        }
    }
}