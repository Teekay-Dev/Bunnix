package com.example.bunnix.frontend

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Notification
import com.example.bunnix.presentation.viewmodel.NotificationViewModel
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Simple Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeSoft = Color(0xFFFFF0EB)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val InfoBlue = Color(0xFF3B82F6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val WarningYellow = Color(0xFFF59E0B)
private val TealAccent = Color(0xFF2EC4B6)

// Notification types
enum class NotificationType(val icon: ImageVector, val color: Color, val bgColor: Color) {
    ORDER(Icons.Default.ShoppingBag, SuccessGreen, SuccessGreen.copy(alpha = 0.1f)),
    BOOKING(Icons.Default.CalendarToday, InfoBlue, InfoBlue.copy(alpha = 0.1f)),
    SYSTEM(Icons.Default.Info, WarningYellow, WarningYellow.copy(alpha = 0.1f)),
    MESSAGE(Icons.Default.Chat, PurpleAccent, PurpleAccent.copy(alpha = 0.1f)),
    PAYMENT(Icons.Default.Payment, TealAccent, TealAccent.copy(alpha = 0.1f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    currentUserId: String = "user_123",
    initialVisibility: Boolean = false,
) {
    // ✅ GET VIEWMODEL
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    // ✅ LOAD NOTIFICATIONS ON FIRST LAUNCH
    LaunchedEffect(currentUserId) {
        notificationViewModel.observeNotifications(currentUserId) // Real-time updates
    }

    var isVisible by remember { mutableStateOf(initialVisibility) }
    LaunchedEffect(Unit) {
        if (!initialVisibility) {
            delay(100)
            isVisible = true
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Orders", "Bookings", "Unread")

    val filteredNotifications = notifications.filter { notif ->
        when (selectedFilter) {
            "Orders" -> notif.type == "ORDER" || notif.type == "PAYMENT"
            "Bookings" -> notif.type == "BOOKING"
            "Unread" -> !notif.isRead
            else -> true
        }
    }.sortedByDescending { it.createdAt?.toDate() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OrangePrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            notificationViewModel.markAllAsRead(currentUserId)
                        },
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

                // ✅ SHOW LOADING, ERROR, OR EMPTY STATE
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangePrimary)
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "Failed to load notifications",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    error ?: "",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                                Button(
                                    onClick = { notificationViewModel.loadNotifications(currentUserId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    filteredNotifications.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    "No notifications yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "We'll notify you when something happens",
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    else -> {
                        // ✅ NOTIFICATIONS LIST
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
                                        notificationViewModel.markAsRead(notification.notificationId)
                                        when (notification.relatedType) {
                                            "order" -> navController.navigate("track_order/${notification.relatedId}")
                                            "booking" -> navController.navigate("booking/${Uri.encode(notification.relatedId)}")
                                            "chat" -> {
                                                val chatId = Uri.encode(notification.relatedId)
                                                navController.navigate("chat_detail/$chatId/Message//")
                                            }
                                            else -> { } // payment, system — just mark as read, no navigation
                                        }
                                    },
                                    onDismiss = {
                                        // ✅ Delete notification
                                        notificationViewModel.deleteNotification(notification.notificationId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== REST OF YOUR CODE STAYS THE SAME =====

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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val count = when (filter) {
                    "All" -> notifications.size
                    "Orders" -> notifications.count { it.type == "ORDER" || it.type == "PAYMENT" }
                    "Bookings" -> notifications.count { it.type == "BOOKING" }
                    "Unread" -> notifications.count { !it.isRead }
                    else -> 0
                }

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelect(filter) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(filter)
                            if (count > 0 && filter != "All") {
                                Text(
                                    "($count)",
                                    fontSize = 12.sp
                                )
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

    val scope = rememberCoroutineScope()

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
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = notification.title,
                                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )

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

                IconButton(
                    onClick = {
                        isDismissed = true
                        scope.launch {
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

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun NotificationScreenPreview() {
    BunnixTheme {
        NotificationScreen(
            navController = rememberNavController(),
            currentUserId = "user_123",
            initialVisibility = true
        )
    }
}