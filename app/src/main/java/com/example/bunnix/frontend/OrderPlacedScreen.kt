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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// Reuse colors from PaymentMethodScreen
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

data class ConfettiParticle(
    val color: Color,
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val rotation: Float,
    val velocityY: Float,
    val velocityX: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderPlacedScreen(
    orderId: String = "ORD-2024-001",
    orderType: String = "Product", // "Product" or "Service"
    vendorName: String = "TechHub Store",
    totalAmount: Double = 45999.0,
    estimatedDelivery: String = "2-3 business days",
    // FIXED: Changed from () -> Unit to (String) -> Unit to accept orderId
    onTrackOrder: (String) -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onViewReceipt: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
        delay(3000)
        showConfetti = false
    }

    // Confetti particles
    val particles = remember {
        List(50) {
            ConfettiParticle(
                color = listOf(OrangePrimary, TealAccent, PurpleAccent, SuccessGreen, Color(0xFFFFD93D)).random(),
                initialX = Random.nextFloat() * 1000f,
                initialY = -100f,
                size = Random.nextFloat() * 8f + 4f,
                rotation = Random.nextFloat() * 360f,
                velocityY = Random.nextFloat() * 15f + 10f,
                velocityX = Random.nextFloat() * 6f - 3f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Order Confirmed", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Confetti Animation Layer
            if (showConfetti) {
                particles.forEachIndexed { index, particle ->
                    val animatedY by infiniteTransition.animateFloat(
                        initialValue = particle.initialY,
                        targetValue = 2000f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                            initialStartOffset = StartOffset(index * 50)
                        ),
                        label = "y$index"
                    )

                    val animatedX by infiniteTransition.animateFloat(
                        initialValue = particle.initialX,
                        targetValue = particle.initialX + particle.velocityX * 100,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                            initialStartOffset = StartOffset(index * 50)
                        ),
                        label = "x$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(particle.size.dp)
                            .offset(animatedX.dp, animatedY.dp)
                            .graphicsLayer(rotationZ = particle.rotation + animatedY)
                            .background(particle.color, RoundedCornerShape(2.dp))
                    )
                }
            }

            // Main Content
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 4 },
                modifier = Modifier.padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Success Animation Circle
                    SuccessCircle()

                    Spacer(modifier = Modifier.height(32.dp))

                    // Order Confirmed Text
                    Text(
                        "Order Placed Successfully! 🎉",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Your $orderType order has been sent to $vendorName",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Order Details Card
                    OrderDetailsCard(
                        orderId = orderId,
                        orderType = orderType,
                        vendorName = vendorName,
                        totalAmount = totalAmount,
                        estimatedDelivery = estimatedDelivery
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // What's Next Section
                    WhatsNextSection()

                    Spacer(modifier = Modifier.height(32.dp))

                    // FIXED: Pass orderId to onTrackOrder callback
                    PrimaryButton(
                        text = "Track Order",
                        icon = Icons.Default.LocationOn,
                        onClick = { onTrackOrder(orderId) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SecondaryButton(
                        text = "View Receipt",
                        icon = Icons.Default.Receipt,
                        onClick = onViewReceipt,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onContinueShopping,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, OrangePrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrangePrimary
                        )
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue Shopping", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Support Info
                    SupportInfo()
                }
            }
        }
    }
}

@Composable
private fun SuccessCircle() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer rings
        repeat(3) { index ->
            val animatedScale by animateFloatAsState(
                targetValue = 1f + (index * 0.2f),
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ring$index"
            )

            Surface(
                modifier = Modifier
                    .size((140 + index * 30).dp)
                    .scale(animatedScale)
                    .alpha(0.1f - index * 0.03f),
                shape = CircleShape,
                color = SuccessGreen
            ) {}
        }

        // Main circle
        Surface(
            modifier = Modifier
                .size(140.dp)
                .scale(scale),
            shape = CircleShape,
            color = SuccessGreen,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }
        }

        // Decorative elements
        Surface(
            modifier = Modifier
                .size(40.dp)
                .offset(x = 70.dp, y = (-50).dp),
            shape = CircleShape,
            color = OrangePrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✨", fontSize = 20.sp)
            }
        }

        Surface(
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-60).dp, y = 60.dp),
            shape = CircleShape,
            color = TealAccent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🎊", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun OrderDetailsCard(
    orderId: String,
    orderType: String,
    vendorName: String,
    totalAmount: Double,
    estimatedDelivery: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Order ID Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Order ID",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                    Text(
                        "#$orderId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = OrangeSoft,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        orderType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = OrangePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = TextTertiary.copy(alpha = 0.2f))

            // Details
            OrderDetailRow(Icons.Default.Store, "Vendor", vendorName)
            OrderDetailRow(Icons.Default.Payments, "Total Amount", formatCurrency(totalAmount))
            OrderDetailRow(Icons.Default.Schedule, "Estimated Delivery", estimatedDelivery)
            OrderDetailRow(Icons.Default.Info, "Status", "Awaiting Payment Confirmation", SuccessGreen)

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = TextTertiary.copy(alpha = 0.2f))

            // Note
            Surface(
                color = OrangeSoft,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.NotificationImportant,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "The vendor will verify your payment and update the order status shortly.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = SurfaceLight,
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 12.sp,
                color = TextTertiary
            )
            Text(
                value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = valueColor
            )
        }
    }
}

@Composable
private fun WhatsNextSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "What's Next?",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val steps = listOf(
            Triple(Icons.Default.Payment, "Payment Verification", "Vendor confirms your transfer receipt"),
            Triple(Icons.Default.Inventory, "Processing", "Vendor prepares your order"),
            Triple(Icons.Default.LocalShipping, "Delivery", "Order shipped to your address")
        )

        steps.forEachIndexed { index, (icon, title, desc) ->
            TimelineItem(
                icon = icon,
                title = title,
                description = desc,
                isLast = index == steps.size - 1,
                isActive = index == 0
            )
        }
    }
}

@Composable
private fun TimelineItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isLast: Boolean,
    isActive: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline line and dot
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = if (isActive) OrangePrimary else TextTertiary.copy(alpha = 0.3f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isActive) Color.White else TextTertiary,
                    modifier = Modifier.padding(10.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(
                            if (isActive) OrangePrimary.copy(alpha = 0.3f) else TextTertiary.copy(alpha = 0.2f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isActive) TextPrimary else TextSecondary
            )
            Text(
                description,
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier.padding(top = 4.dp, bottom = if (isLast) 0.dp else 24.dp)
            )
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f))
    ) {
        Icon(icon, contentDescription = null, tint = TextPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

@Composable
private fun SupportInfo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.HeadsetMic,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            "Need help? Contact Support",
            fontSize = 12.sp,
            color = TextTertiary
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun OrderPlacedScreenPreview() {
    BunnixTheme {
        OrderPlacedScreen()
    }
}