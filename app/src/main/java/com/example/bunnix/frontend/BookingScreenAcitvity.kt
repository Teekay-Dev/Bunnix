package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Service
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    service: Service,
    onBack: () -> Unit,
    onContinue: (BookingDetails) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Booking states
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var customerNotes by remember { mutableStateOf("") }

    // Generate next 14 days for selection
    val availableDates = remember {
        List(14) { index ->
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, index) }.time
        }
    }

    // Mock time slots based on service availability
    val timeSlots = remember(service) {
        listOf("9:00 AM", "10:00 AM", "11:00 AM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM")
    }

    val isValidBooking = selectedDate != null && selectedTime != null

    Scaffold(
        topBar = {
            BookingTopBar(
                serviceName = service.name,
                onBack = onBack
            )
        },
        bottomBar = {
            BookingBottomBar(
                service = service,
                isEnabled = isValidBooking,
                onContinue = {
                    onContinue(
                        BookingDetails(
                            serviceId = service.serviceId,
                            date = selectedDate!!,
                            time = selectedTime!!,
                            notes = customerNotes
                        )
                    )
                }
            )
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
                // Service Summary Card
                ServiceSummaryCard(service = service)

                // Date Selection
                DateSelectionSection(
                    availableDates = availableDates,
                    selectedDate = selectedDate,
                    onDateSelect = { selectedDate = it }
                )

                // Time Selection
                AnimatedVisibility(
                    visible = selectedDate != null,
                    enter = expandVertically() + fadeIn()
                ) {
                    TimeSelectionSection(
                        timeSlots = timeSlots,
                        selectedTime = selectedTime,
                        onTimeSelect = { selectedTime = it }
                    )
                }

                // Notes Section
                NotesSection(
                    notes = customerNotes,
                    onNotesChange = { customerNotes = it }
                )

                // Booking Policy
                BookingPolicySection()

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// Data class for booking details
data class BookingDetails(
    val serviceId: String,
    val date: Date,
    val time: String,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingTopBar(
    serviceName: String,
    onBack: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Book Appointment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        serviceName,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
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
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
private fun ServiceSummaryCard(service: Service) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                if (service.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(service.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = service.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(OrangeSoft, OrangePrimary.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Text(
                    service.vendorName,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration
                    Surface(
                        color = TealAccent.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${service.duration} mins",
                                fontSize = 12.sp,
                                color = TealAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "%.1f".format(service.rating),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelectionSection(
    availableDates: List<Date>,
    selectedDate: Date?,
    onDateSelect: (Date) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Select Date",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Month indicator
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            Text(
                monthFormat.format(availableDates.first()),
                color = OrangePrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal date scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(availableDates) { date ->
                DateCard(
                    date = date,
                    isSelected = selectedDate?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) ==
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    } ?: false,
                    onClick = { onDateSelect(date) }
                )
            }
        }
    }
}

@Composable
private fun DateCard(
    date: Date,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateFormat = SimpleDateFormat("d", Locale.getDefault())

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val backgroundBrush = if (isSelected) {
        Brush.verticalGradient(listOf(OrangePrimary, OrangeLight))
    } else {
        Brush.verticalGradient(listOf(Color.White, Color.White))
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(70.dp)
            .height(90.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                dayFormat.format(date).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                dateFormat.format(date),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextPrimary
            )

            // Today indicator
            val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) ==
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (isToday) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (isSelected) Color.White else OrangePrimary,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "TODAY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) OrangePrimary else Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSelectionSection(
    timeSlots: List<String>,
    selectedTime: String?,
    onTimeSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            "Select Time",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Morning slots
        Text(
            "Morning",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            timeSlots.filter { it.contains("AM") }.forEach { time ->
                TimeChip(
                    time = time,
                    isSelected = selectedTime == time,
                    onClick = { onTimeSelect(time) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Afternoon slots
        Text(
            "Afternoon",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            timeSlots.filter { it.contains("PM") }.forEach { time ->
                TimeChip(
                    time = time,
                    isSelected = selectedTime == time,
                    onClick = { onTimeSelect(time) }
                )
            }
        }
    }
}

@Composable
private fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) OrangePrimary else SurfaceLight,
        border = if (!isSelected) BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                time,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            "Additional Notes",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = {
                Text(
                    "Any special requests or information for the service provider...",
                    color = TextTertiary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                focusedBorderColor = OrangePrimary
            ),
            maxLines = 5
        )

        Text(
            "${notes.length}/500 characters",
            fontSize = 12.sp,
            color = if (notes.length > 450) ErrorRed else TextTertiary,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
}

@Composable
private fun BookingPolicySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Booking Policy",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PolicyItem(
                icon = Icons.Default.Schedule,
                title = "Free cancellation",
                description = "Cancel up to 24 hours before for full refund"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PolicyItem(
                icon = Icons.Default.Payment,
                title = "Secure payment",
                description = "Your payment is protected until service is completed"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PolicyItem(
                icon = Icons.Default.Verified,
                title = "Verified provider",
                description = "All service providers are vetted by Bunnix"
            )
        }
    }
}

@Composable
private fun PolicyItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = OrangeSoft,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                description,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun BookingBottomBar(
    service: Service,
    isEnabled: Boolean,
    onContinue: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Price",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        formatCurrency(service.price),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangePrimary
                    )
                }

                // Duration reminder
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${service.duration} mins",
                        color = TealAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) OrangePrimary else TextTertiary,
                    disabledContainerColor = TextTertiary.copy(alpha = 0.3f)
                ),
                enabled = isEnabled
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (isEnabled) "Continue to Payment" else "Select date & time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    if (isEnabled) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// FlowRow implementation - FIXED with proper imports and explicit types
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<androidx.compose.ui.layout.Measurable>, constraints: androidx.compose.ui.unit.Constraints ->
        val hGapPx = 10.dp.roundToPx()
        val vGapPx = 10.dp.roundToPx()

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()

        var row = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var rowHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (row.isNotEmpty() && rowWidth + hGapPx + placeable.width > constraints.maxWidth) {
                rows.add(row)
                rowWidths.add(rowWidth)
                rowHeights.add(rowHeight)
                row = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }

            row.add(placeable)
            rowWidth += if (row.size == 1) placeable.width else hGapPx + placeable.width
            rowHeight = max(rowHeight, placeable.height)
        }

        if (row.isNotEmpty()) {
            rows.add(row)
            rowWidths.add(rowWidth)
            rowHeights.add(rowHeight)
        }

        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth) ?: constraints.minWidth
        val height = rowHeights.sum() + (rows.size - 1).coerceAtLeast(0) * vGapPx

        layout(width, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, rowPlaceables ->
                var x = when (horizontalArrangement) {
                    Arrangement.End -> width - rowWidths[rowIndex]
                    Arrangement.Center -> (width - rowWidths[rowIndex]) / 2
                    else -> 0
                }

                rowPlaceables.forEachIndexed { placeableIndex, placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + if (placeableIndex < rowPlaceables.size - 1) hGapPx else 0
                }
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun BookingScreenPreview() {
    val sampleService = Service(
        serviceId = "s1",
        vendorId = "v1",
        vendorName = "Glow Up Salon",
        name = "Premium Hair Styling",
        description = "Professional hair styling with premium products",
        price = 15000.0,
        duration = 90,
        category = "Beauty",
        imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
        availability = listOf("9:00 AM", "10:00 AM", "2:00 PM"),
        totalBookings = 234,
        rating = 4.8,
        isActive = true,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )

    BunnixTheme {
        BookingScreen(
            service = sampleService,
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DateCardPreview() {
    BunnixTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DateCard(
                date = Date(),
                isSelected = true,
                onClick = {}
            )
            DateCard(
                date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time,
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeChipPreview() {
    BunnixTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TimeChip(
                time = "10:00 AM",
                isSelected = true,
                onClick = {}
            )
            TimeChip(
                time = "2:00 PM",
                isSelected = false,
                onClick = {}
            )
        }
    }
}