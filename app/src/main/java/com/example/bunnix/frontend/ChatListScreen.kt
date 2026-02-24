package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.ParticipantInfo
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val UnreadBadge = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    currentUserId: String = "user_123"
) {
    // Animated entrance
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Mock data matching your Chat model
    val chats = remember {
        listOf(
            Chat(
                chatId = "chat_1",
                participants = listOf("user_123", "vendor_456"),
                participantDetails = mapOf(
                    "vendor_456" to ParticipantInfo(
                        name = "Glow Up Salon",
                        profilePic = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "Your appointment is confirmed for tomorrow at 2 PM! Don't forget to bring the payment receipt. We're excited to see you! 🎉",
                lastMessageTime = Timestamp.now(),
                lastMessageSender = "vendor_456",
                unreadCount = mapOf("user_123" to 3),
                relatedOrderId = "ORD-001",
                createdAt = Timestamp.now()
            ),
            Chat(
                chatId = "chat_2",
                participants = listOf("user_123", "vendor_789"),
                participantDetails = mapOf(
                    "vendor_789" to ParticipantInfo(
                        name = "TechFix Pro",
                        profilePic = "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "Your iPhone screen replacement is complete! You can pick it up anytime today.",
                lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))),
                lastMessageSender = "vendor_789",
                unreadCount = mapOf("user_123" to 0),
                relatedBookingId = "BK-002",
                createdAt = Timestamp.now()
            ),
            Chat(
                chatId = "chat_3",
                participants = listOf("user_123", "vendor_101"),
                participantDetails = mapOf(
                    "vendor_101" to ParticipantInfo(
                        name = "FreshMart Grocery",
                        profilePic = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "We have a special 20% discount on organic vegetables this weekend only! 🥬🥕",
                lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2))),
                lastMessageSender = "vendor_101",
                unreadCount = mapOf("user_123" to 1),
                createdAt = Timestamp.now()
            ),
            Chat(
                chatId = "chat_4",
                participants = listOf("user_123", "vendor_202"),
                participantDetails = mapOf(
                    "vendor_202" to ParticipantInfo(
                        name = "Style Hub Fashion",
                        profilePic = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "Thanks for your order! Your items have been shipped and will arrive in 2-3 days.",
                lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))),
                lastMessageSender = "user_123",
                unreadCount = mapOf("user_123" to 0),
                relatedOrderId = "ORD-003",
                createdAt = Timestamp.now()
            ),
            Chat(
                chatId = "chat_5",
                participants = listOf("user_123", "vendor_303"),
                participantDetails = mapOf(
                    "vendor_303" to ParticipantInfo(
                        name = "HomeClean Services",
                        profilePic = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "Can we reschedule tomorrow's cleaning to 4 PM instead?",
                lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2))),
                lastMessageSender = "vendor_303",
                unreadCount = mapOf("user_123" to 0),
                relatedBookingId = "BK-005",
                createdAt = Timestamp.now()
            ),
            Chat(
                chatId = "chat_6",
                participants = listOf("user_123", "vendor_404"),
                participantDetails = mapOf(
                    "vendor_404" to ParticipantInfo(
                        name = "FitZone Gym",
                        profilePic = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400",
                        isVendor = true
                    )
                ),
                lastMessage = "Your personal training session is booked for Monday 6 AM. Let's crush those goals! 💪",
                lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3))),
                lastMessageSender = "vendor_404",
                unreadCount = mapOf("user_123" to 0),
                createdAt = Timestamp.now()
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Orders", "Bookings", "Unread")

    val filteredChats = chats.filter { chat ->
        val matchesSearch = chat.participantDetails.values.any {
            it.name.contains(searchQuery, ignoreCase = true)
        } || chat.lastMessage.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Orders" -> chat.relatedOrderId.isNotEmpty()
            "Bookings" -> chat.relatedBookingId.isNotEmpty()
            "Unread" -> (chat.unreadCount[currentUserId] ?: 0) > 0
            else -> true
        }

        matchesSearch && matchesFilter
    }.sortedByDescending { it.lastMessageTime?.toDate() }

    val totalUnread = chats.sumOf { it.unreadCount[currentUserId] ?: 0 }

    Scaffold(
        topBar = {
            ModernChatTopBar(
                totalUnread = totalUnread,
                onSearchClick = { /* Toggle search */ }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search conversations..."
                )

                // Filter Chips
                FilterChips(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    chats = chats,
                    currentUserId = currentUserId
                )

                // Chat List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    items(
                        items = filteredChats,
                        key = { it.chatId }
                    ) { chat ->
                        ChatListItem(
                            chat = chat,
                            currentUserId = currentUserId,
                            onClick = {
                                navController.navigate("chat_detail/${chat.chatId}")
                            },
                            onLongClick = { /* Show options */ }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernChatTopBar(
    totalUnread: Int,
    onSearchClick: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Messages",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (totalUnread > 0) {
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn() + fadeIn()
                            ) {
                                Surface(
                                    color = UnreadBadge,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        totalUnread.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = OrangePrimary
                        )
                    }
                    IconButton(onClick = { /* New chat */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "New chat",
                            tint = OrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    placeholder,
                    color = TextTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = OrangePrimary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextTertiary
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceLight,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = OrangePrimary.copy(alpha = 0.5f)
            ),
            singleLine = true
        )
    }
}

@Composable
private fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    chats: List<Chat>,
    currentUserId: String
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
                    "All" -> chats.size
                    "Orders" -> chats.count { it.relatedOrderId.isNotEmpty() }
                    "Bookings" -> chats.count { it.relatedBookingId.isNotEmpty() }
                    "Unread" -> chats.count { (it.unreadCount[currentUserId] ?: 0) > 0 }
                    else -> 0
                }

                val isSelected = selectedFilter == filter

                FilterChip(
                    selected = isSelected,
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

@Composable
private fun ChatListItem(
    chat: Chat,
    currentUserId: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    

    val otherParticipantId = chat.participants.find { it != currentUserId } ?: ""
    val participantInfo = chat.participantDetails[otherParticipantId]
    val unreadCount = chat.unreadCount[currentUserId] ?: 0
    val isUnread = unreadCount > 0
    val isLastMessageFromMe = chat.lastMessageSender == currentUserId

    // Animation for unread items
    val scale by animateFloatAsState(
        targetValue = if (isUnread) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(scale),
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status
            Box {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    shadowElevation = if (isUnread) 4.dp else 0.dp
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(participantInfo?.profilePic)
                            .crossfade(true)
                            .build(),
                        contentDescription = participantInfo?.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Online indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp, y = (-2).dp)
                        .align(Alignment.BottomEnd)
                        .background(SuccessGreen, CircleShape)
                        .border(3.dp, if (isUnread) OrangeSoft else Color.White, CircleShape)
                )

                // Typing indicator (mock)
                if (chat.chatId == "chat_1") {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-20).dp, y = 0.dp),
                        color = OrangePrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            repeat(3) { index ->
                                val offset by rememberInfiniteTransition(label = "typing").animateFloat(
                                    initialValue = 0f,
                                    targetValue = -4f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(300, delayMillis = index * 100),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "dot$index"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .offset(y = offset.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = participantInfo?.name ?: "Unknown",
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )

                        // Verified badge for vendors
                        if (participantInfo?.isVendor == true) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = OrangePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = formatTime(chat.lastMessageTime),
                        fontSize = 12.sp,
                        color = if (isUnread) OrangePrimary else TextTertiary,
                        fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Message preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLastMessageFromMe) {
                        Icon(
                            imageVector = if (isUnread) Icons.Default.Done else Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isUnread) TextTertiary else TealAccent
                        )
                    }

                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = if (isUnread) TextPrimary else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal
                    )

                    if (isUnread) {
                        Surface(
                            color = UnreadBadge,
                            shape = CircleShape,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Order/Booking tag
                if (chat.relatedOrderId.isNotEmpty() || chat.relatedBookingId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    val isOrder = chat.relatedOrderId.isNotEmpty()
                    Surface(
                        color = if (isOrder) OrangePrimary.copy(alpha = 0.1f) else TealAccent.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isOrder) Icons.Default.ShoppingBag else Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (isOrder) OrangePrimary else TealAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isOrder)
                                    "Order #${chat.relatedOrderId.takeLast(6)}"
                                else
                                    "Booking #${chat.relatedBookingId.takeLast(6)}",
                                fontSize = 11.sp,
                                color = if (isOrder) OrangePrimary else TealAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""

    val date = timestamp.toDate()
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
        diff < TimeUnit.DAYS.toMillis(7) -> SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ChatListScreenPreview() {
    BunnixTheme {
        ChatListScreen(
            navController = rememberNavController(),
            currentUserId = "user_123"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListItemPreview() {
    BunnixTheme {
        val mockChat = Chat(
            chatId = "chat_1",
            participants = listOf("user_123", "vendor_456"),
            participantDetails = mapOf(
                "vendor_456" to ParticipantInfo(
                    name = "Glow Up Salon",
                    profilePic = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
                    isVendor = true
                )
            ),
            lastMessage = "Your appointment is confirmed for tomorrow!",
            lastMessageTime = Timestamp.now(),
            lastMessageSender = "vendor_456",
            unreadCount = mapOf("user_123" to 3),
            relatedOrderId = "ORD-001",
            createdAt = Timestamp.now()
        )

        ChatListItem(
            chat = mockChat,
            currentUserId = "user_123",
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListItemReadPreview() {
    BunnixTheme {
        val mockChat = Chat(
            chatId = "chat_2",
            participants = listOf("user_123", "vendor_789"),
            participantDetails = mapOf(
                "vendor_789" to ParticipantInfo(
                    name = "TechFix Pro",
                    profilePic = "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400",
                    isVendor = true
                )
            ),
            lastMessage = "Your repair is complete!",
            lastMessageTime = Timestamp(Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2))),
            lastMessageSender = "user_123",
            unreadCount = mapOf("user_123" to 0),
            relatedBookingId = "BK-002",
            createdAt = Timestamp.now()
        )

        ChatListItem(
            chat = mockChat,
            currentUserId = "user_123",
            onClick = {},
            onLongClick = {}
        )
    }
}