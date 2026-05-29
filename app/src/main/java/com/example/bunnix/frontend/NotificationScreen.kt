package com.example.bunnix.frontend

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bunnix.database.models.Notification
import com.example.bunnix.presentation.viewmodel.NotificationViewModel
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
    MESSAGE(Icons.AutoMirrored.Filled.Chat, PurpleAccent, PurpleAccent.copy(alpha = 0.1f)),
    PAYMENT(Icons.Default.Payment, TealAccent, TealAccent.copy(alpha = 0.1f)),
    MARKETING(Icons.Default.Star, PurpleAccent, PurpleAccent.copy(alpha = 0.1f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    currentUserId: String = "user_123",
    isGeneralMode: Boolean = false, // If true, show marketing/system. If false, show transactional.
    initialVisibility: Boolean = false,
    notificationViewModel: NotificationViewModel
) {
//    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()

    LaunchedEffect(currentUserId) {
        notificationViewModel.observeNotifications(currentUserId)
//        notificationViewModel.markAllAsRead(currentUserId)
    }

    var isVisible by remember { mutableStateOf(initialVisibility) }
    LaunchedEffect(Unit) {
        if (!initialVisibility) {
            delay(100)
            isVisible = true
        }
    }

    // Filter logic based on mode
    val modeFilteredNotifications = notifications.filter { notif ->
        if (isGeneralMode) {
            notif.type == "SYSTEM" || notif.type == "MARKETING"
        } else {
            notif.type != "SYSTEM" && notif.type != "MARKETING"
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = if (isGeneralMode) listOf("All", "Unread") else listOf("All", "Orders", "Bookings", "Unread")

    val filteredNotifications = modeFilteredNotifications.filter { notif ->
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
                        if (isGeneralMode) "Notifications" else "Alerts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        enabled = modeFilteredNotifications.any { !it.isRead }
                    ) {
                        Text(
                            "Mark all read",
                            color = if (modeFilteredNotifications.any { !it.isRead }) OrangePrimary else TextTertiary,
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
                CustomFilterChips(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    notifications = modeFilteredNotifications
                )

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = OrangePrimary)
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Error, null, tint = OrangePrimary, modifier = Modifier.size(48.dp))
                                Text("Failed to load notifications", fontWeight = FontWeight.Bold)
                                Button(onClick = { notificationViewModel.loadNotifications(currentUserId) }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    filteredNotifications.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Notifications, null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                                Text("No notifications yet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    if(isGeneralMode) "General notifications will appear here" else "Updates about your orders and bookings will appear here",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 32.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(items = filteredNotifications, key = { it.notificationId }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        notificationViewModel.markAsRead(notification.notificationId)
                                        when (notification.relatedType) {
                                            "order" -> navController.navigate("track_order/${notification.relatedId}")
                                            "booking" -> navController.navigate("track_booking/${notification.relatedId}")
                                            "chat" -> {
                                                val chatId = Uri.encode(notification.relatedId)
                                                navController.navigate("chat_detail/$chatId/Message//")
                                            }
                                        }
                                    },
                                    onDismiss = {
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

@Composable
private fun CustomFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    notifications: List<Notification>
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(filters) { filter ->
                val count = when (filter) {
                    "All" -> notifications.size
                    // 🌟 ADD '!it.isRead' filters to these tags so the counts update instantly when clicked!
                    "Orders" -> notifications.count { !it.isRead && (it.type == "ORDER" || it.type == "PAYMENT") }
                    "Bookings" -> notifications.count { !it.isRead && it.type == "BOOKING" }
                    "Unread" -> notifications.count { !it.isRead }
                    else -> 0
                }

                val isSelected = selectedFilter == filter

                Surface(
                    onClick = { onFilterSelect(filter) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) OrangePrimary else SurfaceLight,
                    border = if (isSelected) null else BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                        if (count > 0) {
                            Surface(
                                color = if (isSelected) Color.White.copy(alpha = 0.2f) else OrangePrimary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (count > 99) "99+" else count.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else OrangePrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
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
    } catch (e: Exception) {
        NotificationType.SYSTEM
    }

    val isUnread = !notification.isRead

    var isDismissed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isDismissed) 0.9f else 1f, label = "")
    val alpha by animateFloatAsState(targetValue = if (isDismissed) 0f else 1f, label = "")

    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = !isDismissed,
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .scale(scale)
                .alpha(alpha),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUnread) OrangeSoft.copy(alpha = 0.6f) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUnread) 2.dp else 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(notifType.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = notifType.icon,
                        contentDescription = null,
                        tint = notifType.color,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = notification.title,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isUnread) {
                            Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).background(OrangePrimary, CircleShape))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        color = if (isUnread) TextPrimary.copy(alpha = 0.8f) else TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                        Text(
                            text = formatNotificationTime(notification.createdAt),
                            fontSize = 12.sp,
                            color = TextTertiary
                        )

                        if (notification.relatedType.isNotEmpty()) {
                            Surface(
                                color = notifType.color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = notification.relatedType.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = notifType.color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        isDismissed = true
                        scope.launch { delay(200); onDismiss() }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
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
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}
