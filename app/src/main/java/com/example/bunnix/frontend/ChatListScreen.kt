package com.example.bunnix.frontend

import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Chat
import com.example.bunnix.database.models.ParticipantInfo
import com.example.bunnix.presentation.viewmodel.ChatViewModel
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
    viewModel: ChatViewModel = hiltViewModel() // ✅ No need to pass ID manually
) {
    // ✅ COLLECT STATE FROM VIEWMODEL
    val chats by viewModel.userChats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // We get the current user ID from the ViewModel to calculate unread counts
    // (Assuming you updated ChatViewModel to expose currentUserId, or we derive it from AuthRepository inside VM)
    // For now, we assume the 'unreadCount' map in the Chat object handles the logic.

    // Animated entrance
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Orders", "Bookings", "Unread")

    // ✅ FILTERING LOGIC
    // Since we don't have currentUserId in the composable anymore, we check if ANY user in the map has unread count > 0
    // Ideally, pass currentUserId from ViewModel to here if needed for specific UI logic.
    // For now, let's simplify filtering.

    val filteredChats = chats.filter { chat ->
        val matchesSearch = chat.participantDetails.values.any {
            it.name.contains(searchQuery, ignoreCase = true)
        } || chat.lastMessage.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Orders" -> chat.relatedOrderId.isNotEmpty()
            "Bookings" -> chat.relatedBookingId.isNotEmpty()
            "Unread" -> chat.unreadCount.values.any { it > 0 } // Check if anyone has unread
            else -> true
        }

        matchesSearch && matchesFilter
    }.sortedByDescending { it.lastMessageTime?.toDate() }

    val totalUnread = chats.sumOf { it.unreadCount.values.sum() }

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
                    .navigationBarsPadding()
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
                    chats = chats
                )

                // ✅ UI STATES
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
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = UnreadBadge,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("Failed to load chats", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(error ?: "", color = TextSecondary, fontSize = 14.sp)
                                Button(
                                    onClick = { viewModel.loadCurrentUserChats() },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    filteredChats.isEmpty() -> {
                        EmptyChatState()
                    }
                    else -> {
                        // ✅ CHAT LIST
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Inside ChatListScreen.kt

                            items(items = filteredChats, key = { it.chatId }) { chat ->
                                ChatListItem(
                                    chat = chat,
                                    isUnread = chat.unreadCount.values.any { it > 0 },
                                    onClick = {
                                        // ✅ 1. Get the other participant's info
                                        val participant = chat.participantDetails.values.firstOrNull()

                                        // ✅ 2. Encode data so it navigates safely
                                        val name = Uri.encode(participant?.name ?: "Unknown")
                                        val image = Uri.encode(participant?.profilePic ?: "")

                                        // ✅ 3. Navigate with REAL DATA
                                        navController.navigate("chat_detail/${chat.chatId}/$name/$image")
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
private fun EmptyChatState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("No conversations yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Start chatting with vendors", color = TextSecondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernChatTopBar(totalUnread: Int, onSearchClick: () -> Unit) {
    Surface(color = Color.White, tonalElevation = 2.dp) {
        CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Messages", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                    if (totalUnread > 0) {
                        Surface(color = UnreadBadge, shape = RoundedCornerShape(12.dp)) {
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
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
    Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder, color = TextTertiary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = OrangePrimary) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
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
private fun FilterChips(filters: List<String>, selectedFilter: String, onFilterSelect: (String) -> Unit, chats: List<Chat>) {
    Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelect(filter) },
                    label = { Text(filter) },
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

// ✅ SIMPLIFIED CHAT LIST ITEM
@Composable
private fun ChatListItem(chat: Chat, isUnread: Boolean, onClick: () -> Unit) {
    // Get the first participant details (usually the vendor)
    val participantInfo = chat.participantDetails.values.firstOrNull()
    val unreadCount = chat.unreadCount.values.sum()

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isUnread) OrangeSoft else Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(modifier = Modifier.size(60.dp), shape = CircleShape) {
                AsyncImage(
                    model = participantInfo?.profilePic,
                    contentDescription = participantInfo?.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = participantInfo?.name ?: "Unknown",
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (participantInfo?.isVendor == true) {
                            Icon(Icons.Default.Verified, "Verified", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(text = formatTime(chat.lastMessageTime), fontSize = 12.sp, color = if (isUnread) OrangePrimary else TextTertiary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = if (isUnread) TextPrimary else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (isUnread) {
                        Surface(color = UnreadBadge, shape = CircleShape, modifier = Modifier.padding(start = 8.dp)) {
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

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ChatListScreenPreview() {
    BunnixTheme {
        ChatListScreen(navController = rememberNavController())
    }
}