package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val SuccessGreen = Color(0xFF10B981)
private val WarningYellow = Color(0xFFF59E0B)

sealed class OrderStatus(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    object AwaitingPayment : OrderStatus(
        "Awaiting Payment",
        "Payment confirmation pending",
        Icons.Default.Payment,
        WarningYellow
    )
    object Processing : OrderStatus(
        "Processing",
        "Vendor is preparing your order",
        Icons.Default.Inventory,
        OrangePrimary
    )
    object Shipped : OrderStatus(
        "Shipped",
        "Order is on the way",
        Icons.Default.LocalShipping,
        TealAccent
    )
    object Delivered : OrderStatus(
        "Delivered",
        "Order completed successfully",
        Icons.Default.CheckCircle,
        SuccessGreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    orderId: String = "ORD-2024-001",
    orderType: String = "Product",
    currentStatus: OrderStatus = OrderStatus.Processing,
    vendorName: String = "TechHub Store",
    estimatedDelivery: String = "Tomorrow, 2:00 PM - 4:00 PM",
    onBack: () -> Unit = {},
    onContactVendor: () -> Unit = {},
    onCancelOrder: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val statusSteps = listOf(
        OrderStatus.AwaitingPayment,
        OrderStatus.Processing,
        OrderStatus.Shipped,
        OrderStatus.Delivered
    )

    val currentStepIndex = statusSteps.indexOf(currentStatus)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Track Order", fontWeight = FontWeight.Bold)
                        Text(
                            "#$orderId",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
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
            enter = fadeIn() + slideInVertically { it / 4 },
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Live Status Card
                LiveStatusCard(currentStatus, estimatedDelivery)

                // Progress Tracker
                ProgressTracker(
                    steps = statusSteps,
                    currentStep = currentStepIndex
                )

                // Delivery Map Visualization
                DeliveryMapCard(vendorName)

                // Order Items
                OrderItemsCard()

                // Action Buttons
                ActionButtons(
                    onContactVendor = onContactVendor,
                    onCancelOrder = onCancelOrder,
                    canCancel = currentStepIndex < 2
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LiveStatusCard(status: OrderStatus, estimatedDelivery: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = status.color)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pulsing dot
                Box(
                    modifier = Modifier.size(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .scale(pulseScale)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                Text(
                    "LIVE STATUS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        status.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Column {
                    Text(
                        status.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        status.description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Estimated Delivery",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            estimatedDelivery,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressTracker(
    steps: List<OrderStatus>,
    currentStep: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Order Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, step ->
                    val isCompleted = index <= currentStep
                    val isCurrent = index == currentStep

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Step Circle
                        val scale by animateFloatAsState(
                            targetValue = if (isCurrent) 1.1f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "scale$index"
                        )

                        Box(contentAlignment = Alignment.Center) {
                            if (isCurrent) {
                                Surface(
                                    color = step.color.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(56.dp)
                                ) {}
                            }

                            Surface(
                                color = when {
                                    isCurrent -> step.color
                                    isCompleted -> SuccessGreen
                                    else -> TextTertiary.copy(alpha = 0.3f)
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp).scale(scale),
                                shadowElevation = if (isCurrent) 8.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isCompleted && !isCurrent) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Icon(
                                            step.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Step Label
                        Text(
                            step.title,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isCurrent -> step.color
                                isCompleted -> TextPrimary
                                else -> TextTertiary
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        // Connecting Line
                        if (index < steps.size - 1) {
                            val lineProgress by animateFloatAsState(
                                targetValue = if (index < currentStep) 1f else 0f,
                                animationSpec = tween(1000, delayMillis = index * 200),
                                label = "line$index"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .offset(y = (-40).dp)
                                    .padding(horizontal = 20.dp)
                                    .background(
                                        TextTertiary.copy(alpha = 0.2f),
                                        RoundedCornerShape(2.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(lineProgress)
                                        .background(
                                            SuccessGreen,
                                            RoundedCornerShape(2.dp)
                                        )
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
private fun DeliveryMapCard(vendorName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Delivery Route",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Surface(
                    color = OrangeSoft,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Live",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = OrangePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stylized Map Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceLight)
            ) {
                // Map background pattern
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw roads
                    drawLine(
                        color = TextTertiary.copy(alpha = 0.2f),
                        start = Offset(0f, size.height * 0.3f),
                        end = Offset(size.width, size.height * 0.7f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = TextTertiary.copy(alpha = 0.2f),
                        start = Offset(size.width * 0.2f, 0f),
                        end = Offset(size.width * 0.8f, size.height),
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }

                // Vendor Location
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = OrangePrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            vendorName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Delivery Location
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = TealAccent,
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            "Your Location",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Moving Delivery Icon
                val infiniteTransition = rememberInfiniteTransition(label = "delivery")
                val progress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 60.dp)
                ) {
                    Surface(
                        color = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .offset(
                                x = (progress * 200).dp,
                                y = (progress * 40).dp
                            ),
                        shadowElevation = 8.dp
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Driver is 12 minutes away",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun OrderItemsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Order Items",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            repeat(2) { index ->
                OrderItemRow(
                    name = if (index == 0) "iPhone 15 Pro Case" else "Wireless Charger",
                    quantity = if (index == 0) 1 else 2,
                    price = if (index == 0) 15000.0 else 8000.0
                )
                if (index < 1) {
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextTertiary.copy(alpha = 0.2f))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextTertiary.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    formatCurrency(31000.0),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(name: String, quantity: Int, price: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = OrangeSoft,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Qty: $quantity", fontSize = 12.sp, color = TextTertiary)
        }

        Text(
            formatCurrency(price),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextPrimary
        )
    }
}

@Composable
private fun ActionButtons(
    onContactVendor: () -> Unit,
    onCancelOrder: () -> Unit,
    canCancel: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onContactVendor,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, OrangePrimary)
        ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chat", fontWeight = FontWeight.Bold)
        }

        if (canCancel) {
            OutlinedButton(
                onClick = onCancelOrder,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
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
fun TrackOrderScreenPreview() {
    BunnixTheme {
        TrackOrderScreen()
    }
}