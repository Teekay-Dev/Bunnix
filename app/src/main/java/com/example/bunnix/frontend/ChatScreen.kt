package com.example.bunnix.frontend

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Message
import com.example.bunnix.database.models.ParticipantInfo
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import com.example.bunnix.presentation.viewmodel.ChatViewModel
import com.example.bunnix.database.supabase.storage.ChatStorage
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFF5F5F5)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String,
    vendorName: String,
    vendorImageUrl: String,
    vendorId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // ✅ COLLECT STATE
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoadingMessages.collectAsState()
    val isSending by viewModel.isSendingMessage.collectAsState()
    val messageSent by viewModel.messageSent.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val vendorProfile by viewModel.vendorProfile.collectAsState()

    LaunchedEffect(chatId) {
        // Create chat if it doesn't exist yet
        currentUserId?.let { uid ->
            viewModel.getOrCreateChat(
                currentUserId = uid,
                vendorId = vendorId,
                vendorName = vendorName,
                vendorImage = vendorImageUrl,
                onResult = { /* chat exists or created */ }
            )
        }
        viewModel.observeChatMessages(chatId)
        viewModel.loadVendorProfile(vendorId)
        currentUserId?.let { viewModel.markMessagesAsRead(chatId, it) }
    }

    LaunchedEffect(messageSent) {
        if (messageSent) viewModel.resetMessageSent()
    }

    // ✅ REAL PARTICIPANT INFO
    val participantInfo = remember(vendorName, vendorImageUrl) {
        ParticipantInfo(
            name = vendorName,
            profilePic = vendorImageUrl,
            isVendor = true
        )
    }

    var messageText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    // ✅ VOICE RECORDING STATE
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    // ✅ IMAGE PICKER
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = ChatStorage.uploadChatImage(context, it)
                result.onSuccess { url ->
                    currentUserId?.let { id ->
                        viewModel.sendImageMessage(chatId, id, "You", url)
                    }
                }
            }
        }
    }

    // ✅ PERMISSIONS
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording(context) { recorder, file ->
                mediaRecorder = recorder
                audioFile = file
                isRecording = true
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    // ✅ CALL INTENT
    val makeCall: () -> Unit = {
        vendorProfile?.phone?.let { phone ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            ModernChatTopBar(
                participantInfo = participantInfo,
                isTyping = isTyping,
                onBack = { navController.popBackStack() },
                onCall = makeCall,
                onMore = { }
            )
        },
        bottomBar = {
            ModernChatInput(
                value = messageText,
                onValueChange = {
                    messageText = it
                    isTyping = it.isNotEmpty()
                },
                onSend = {
                    if (messageText.isNotBlank() && !isSending && currentUserId != null) {
                        viewModel.sendTextMessage(
                            chatId = chatId,
                            senderId = currentUserId!!,
                            senderName = "You",
                            text = messageText
                        )
                        messageText = ""
                        isTyping = false
                    }
                    keyboardController?.hide()
                },
                onAttach = { imagePickerLauncher.launch("image/*") },
                isRecording = isRecording,
                onStartRecording = {
                    // ✅ FIX: Simplified permission check
                    recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onStopRecording = {
                    stopRecording(mediaRecorder) {
                        isRecording = false
                        audioFile?.let { file ->
                            val uri = Uri.fromFile(file)
                            scope.launch {
                                val result = ChatStorage.uploadVoiceNote(context, uri)
                                result.onSuccess { url ->
                                    currentUserId?.let { id ->
                                        viewModel.sendVoiceMessage(chatId, id, "You", url)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = OrangePrimary)
            } else if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                    Text("No messages yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Start chatting with ${participantInfo.name}", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { DateHeader(date = "Today") }
                    items(items = messages, key = { it.messageId }) { message ->
                        AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically { it / 2 }) {
                            MessageItem(
                                message = message,
                                isFromMe = message.senderId == currentUserId,
                                participantInfo = participantInfo,
                                onOrderClick = { },
                                onImageClick = { }
                            )
                        }
                    }
                    if (isTyping) item { TypingIndicator(participantInfo = participantInfo) }
                }
            }
        }
    }
}

// Helpers for recording
fun startRecording(context: Context, onStarted: (MediaRecorder, File) -> Unit) {
    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }

    val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")

    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(file.absolutePath)
        prepare()
        start()
    }
    onStarted(recorder, file)
}

fun stopRecording(recorder: MediaRecorder?, onStopped: () -> Unit) {
    recorder?.apply {
        stop()
        release()
    }
    onStopped()
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun ModernChatTopBar(
    participantInfo: ParticipantInfo,
    isTyping: Boolean,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onMore: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = participantInfo.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        AnimatedContent(
                            targetState = isTyping,
                            transitionSpec = { fadeIn() + expandVertically() with fadeOut() + shrinkVertically() }
                        ) { typing ->
                            if (typing) {
                                Text(
                                    text = "typing...",
                                    fontSize = 12.sp,
                                    color = TealAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(SuccessGreen, CircleShape)
                                    )
                                    Text(
                                        text = "Online",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
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
                    IconButton(onClick = onCall) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = OrangePrimary
                        )
                    }
                    IconButton(onClick = onMore) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = TextPrimary
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

@Composable
private fun DateHeader(date: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = TextTertiary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = date,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    isFromMe: Boolean,
    participantInfo: ParticipantInfo,
    onOrderClick: (String) -> Unit,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        when (message.messageType) {
            "order_link" -> OrderLinkCard(
                message = message,
                isFromMe = isFromMe,
                onClick = {
                    val orderId = message.orderPreview["id"] as? String ?: ""
                    onOrderClick(orderId)
                }
            )
            "image" -> ImageMessage(
                message = message,
                isFromMe = isFromMe,
                onClick = { message.imageUrl?.let { onImageClick(it) } }
            )
            else -> TextMessage(
                message = message,
                isFromMe = isFromMe
            )
        }
    }
}

@Composable
private fun TextMessage(
    message: Message,
    isFromMe: Boolean
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isFromMe) 20.dp else 4.dp,
        bottomEnd = if (isFromMe) 4.dp else 20.dp
    )

    val bubbleColor = if (isFromMe) {
        Brush.linearGradient(listOf(OrangePrimary, OrangeLight))
    } else {
        Brush.linearGradient(listOf(Color.White, Color.White))
    }

    Column(
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
        modifier = Modifier.padding(
            start = if (isFromMe) 64.dp else 0.dp,
            end = if (isFromMe) 0.dp else 64.dp
        )
    ) {
        Surface(
            shape = bubbleShape,
            shadowElevation = if (isFromMe) 0.dp else 2.dp,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(bubbleColor)
                    .padding(16.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isFromMe) Color.White else TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // Timestamp and read status
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatMessageTime(message.timestamp),
                fontSize = 11.sp,
                color = TextTertiary
            )

            if (isFromMe) {
                Icon(
                    imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (message.isRead) TealAccent else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun ImageMessage(
    message: Message,
    isFromMe: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
        modifier = Modifier.padding(
            start = if (isFromMe) 64.dp else 0.dp,
            end = if (isFromMe) 0.dp else 64.dp
        )
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.width(250.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(message.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Shared image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    contentScale = ContentScale.Crop
                )

                // Overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                            )
                        )
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatMessageTime(message.timestamp),
                fontSize = 11.sp,
                color = TextTertiary
            )

            if (isFromMe) {
                Icon(
                    imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (message.isRead) TealAccent else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun OrderLinkCard(
    message: Message,
    isFromMe: Boolean,
    onClick: () -> Unit
) {
    val orderData = message.orderPreview

    Column(
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
        modifier = Modifier.padding(
            start = if (isFromMe) 32.dp else 0.dp,
            end = if (isFromMe) 0.dp else 32.dp
        )
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.width(280.dp)
            ) {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    if (orderData["type"] == "booking") TealAccent else OrangePrimary,
                                    if (orderData["type"] == "booking") Color(0xFF00BBF9) else OrangeLight
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (orderData["type"] == "booking")
                                Icons.Default.CalendarToday
                            else
                                Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Column {
                            Text(
                                text = orderData["id"] as? String ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = (orderData["type"] as? String)?.uppercase() ?: "",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = orderData["title"] as? String ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    orderData["date"]?.let { date ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = date as String,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    orderData["location"]?.let { location ->
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = location as String,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = orderData["amount"] as? String ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = OrangePrimary
                        )

                        Surface(
                            color = SuccessGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = orderData["status"] as? String ?: "",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // View button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLight
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Details",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = OrangePrimary
                        )
                    }
                }
            }
        }

        Text(
            text = formatMessageTime(message.timestamp),
            fontSize = 11.sp,
            color = TextTertiary,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
private fun TypingIndicator(
    participantInfo: ParticipantInfo
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
    ) {
        // Avatar
        AsyncImage(
            model = participantInfo.profilePic,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        // Typing bubbles
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val offset by rememberInfiniteTransition(label = "typing").animateFloat(
                        initialValue = 0f,
                        targetValue = -6f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = offset.dp)
                            .background(TextTertiary, CircleShape)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column {
            // Quick actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Image,
                    onClick = onAttach
                )
                QuickActionButton(
                    icon = Icons.Default.CameraAlt,
                    onClick = { /* Take photo implementation */ }
                )
                QuickActionButton(
                    icon = Icons.Default.AttachFile,
                    onClick = onAttach
                )
            }

            // Input field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = SurfaceLight,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = OrangePrimary.copy(alpha = 0.5f)
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() })
                )

                val sendScale by animateFloatAsState(
                    targetValue = if (value.isNotBlank()) 1f else 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "sendScale"
                )

                // ✅ CHANGE: Mic Icon when empty, Send Icon when text present
                FloatingActionButton(
                    onClick = {
                        if (value.isNotBlank()) {
                            onSend()
                        } else {
                            if (isRecording) onStopRecording() else onStartRecording()
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .scale(sendScale),
                    shape = CircleShape,
                    containerColor = if (isRecording) ErrorRed else if (value.isNotBlank()) OrangePrimary else TextTertiary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else if (value.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(SurfaceLight, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrangePrimary
        )
    }
}

private fun formatMessageTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""

    val date = timestamp.toDate()
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
        diff < TimeUnit.DAYS.toMillis(1) -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ChatDetailScreenPreview() {
    BunnixTheme {
        ChatDetailScreen(
            navController = rememberNavController(),
            chatId = "chat_1",
            vendorName = "Vendor",
            vendorImageUrl = "",
            vendorId = "vendor_1"
        )
    }
}