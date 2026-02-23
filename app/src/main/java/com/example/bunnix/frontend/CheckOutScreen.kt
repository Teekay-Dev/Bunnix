package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat

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

// Payment methods (no wallet)
enum class PaymentMethod(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    CARD("Credit/Debit Card", Icons.Default.CreditCard, Color(0xFF1A1F71)),
    BANK("Bank Transfer", Icons.Default.AccountBalance, Color(0xFF6B7280)),
    USSD("USSD", Icons.Default.PhoneAndroid, Color(0xFF10B981)),
    PAY_ON_DELIVERY("Pay on Delivery", Icons.Default.LocalShipping, OrangePrimary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CheckoutItem>,
    isServiceBooking: Boolean = false,
    onBack: () -> Unit,
    onPaymentMethodSelect: (PaymentMethod) -> Unit,
    onApplyPromo: (String) -> Boolean,
    onPlaceOrder: () -> Unit,
    total: Double // Keep this parameter
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // States
    var selectedPayment by remember { mutableStateOf(PaymentMethod.CARD) }
    var promoCode by remember { mutableStateOf("") }
    var promoApplied by remember { mutableStateOf<String?>(null) }
    var showPromoError by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("123 Victoria Island, Lagos") }
    var showAddressEdit by remember { mutableStateOf(false) }

    // Calculate totals - RENAME internal total to calculatedTotal to avoid conflict
    val subtotal = items.sumOf { it.price * it.quantity }
    val discount = promoApplied?.let { calculateDiscount(subtotal, it) } ?: 0.0
    val serviceFee = subtotal * 0.05 // 5% service fee
    val deliveryFee = if (isServiceBooking) 0.0 else 1500.0
    val calculatedTotal = subtotal - discount + serviceFee + deliveryFee // RENAMED from 'total' to 'calculatedTotal'

    // Use the passed 'total' parameter (external) or calculatedTotal (internal)
    // For now, we'll use calculatedTotal for display since it's more accurate with discounts
    val displayTotal = calculatedTotal

    Scaffold(
        topBar = {
            CheckoutTopBar(
                itemCount = items.sumOf { it.quantity },
                onBack = onBack
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                total = displayTotal, // Use displayTotal here
                onPlaceOrder = onPlaceOrder
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
                // Delivery Address (hide for services)
                if (!isServiceBooking) {
                    AddressSection(
                        address = deliveryAddress,
                        onEditClick = { showAddressEdit = true }
                    )
                }

                // Order Items
                OrderItemsSection(items = items)

                // Promo Code
                PromoCodeSection(
                    code = promoCode,
                    onCodeChange = {
                        promoCode = it
                        showPromoError = false
                    },
                    appliedCode = promoApplied,
                    onApply = {
                        if (onApplyPromo(promoCode)) {
                            promoApplied = promoCode
                            promoCode = ""
                        } else {
                            showPromoError = true
                        }
                    },
                    onRemove = { promoApplied = null },
                    showError = showPromoError
                )

                // Payment Methods
                PaymentMethodSection(
                    selectedMethod = selectedPayment,
                    onMethodSelect = {
                        selectedPayment = it
                        onPaymentMethodSelect(it)
                    }
                )

                // Order Summary - use displayTotal
                OrderSummary(
                    subtotal = subtotal,
                    discount = discount,
                    serviceFee = serviceFee,
                    deliveryFee = deliveryFee,
                    total = displayTotal, // Use displayTotal here
                    isServiceBooking = isServiceBooking
                )

                // Security Badge
                SecurityBadge()

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Address Edit Dialog
    if (showAddressEdit) {
        AddressEditDialog(
            currentAddress = deliveryAddress,
            onDismiss = { showAddressEdit = false },
            onSave = {
                deliveryAddress = it
                showAddressEdit = false
            }
        )
    }
}

// Data class for checkout items
data class CheckoutItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val variant: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutTopBar(
    itemCount: Int,
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
                        "Checkout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "$itemCount ${if (itemCount == 1) "item" else "items"}",
                        fontSize = 13.sp,
                        color = TextSecondary
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
private fun AddressSection(
    address: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = OrangeSoft,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = OrangePrimary
                            )
                        }
                    }

                    Column {
                        Text(
                            "Delivery Address",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Home",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(onClick = onEditClick) {
                    Text("Change", color = OrangePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                address,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = SuccessGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Deliverable",
                        color = SuccessGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsSection(items: List<CheckoutItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Order Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    "${items.sumOf { it.quantity }} items",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, item ->
                CheckoutItemRow(item = item)
                if (index < items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = SurfaceLight
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutItemRow(item: CheckoutItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        tint = TextTertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            item.variant?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Text(
                "Qty: ${item.quantity}",
                fontSize = 12.sp,
                color = TextTertiary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                formatCurrency(item.price * item.quantity),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )

            Text(
                formatCurrency(item.price) + " each",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromoCodeSection(
    code: String,
    onCodeChange: (String) -> Unit,
    appliedCode: String?,
    onApply: () -> Unit,
    onRemove: () -> Unit,
    showError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Promo Code",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (appliedCode != null) {
                // Applied state
                Surface(
                    color = SuccessGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                            Column {
                                Text(
                                    appliedCode.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Text(
                                    "Promo applied successfully!",
                                    fontSize = 12.sp,
                                    color = SuccessGreen
                                )
                            }
                        }

                        IconButton(onClick = onRemove) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = ErrorRed
                            )
                        }
                    }
                }
            } else {
                // Input state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = onCodeChange,
                        placeholder = { Text("Enter promo code") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = SurfaceLight,
                            unfocusedBorderColor = if (showError) ErrorRed else TextTertiary.copy(alpha = 0.3f),
                            focusedBorderColor = if (showError) ErrorRed else OrangePrimary
                        ),
                        singleLine = true,
                        isError = showError
                    )

                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        enabled = code.isNotBlank()
                    ) {
                        Text("Apply")
                    }
                }

                AnimatedVisibility(
                    visible = showError,
                    enter = expandVertically() + fadeIn()
                ) {
                    Text(
                        "Invalid promo code",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: PaymentMethod,
    onMethodSelect: (PaymentMethod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PaymentMethod.values().forEach { method ->
                PaymentMethodItem(
                    method = method,
                    isSelected = selectedMethod == method,
                    onClick = { onMethodSelect(method) }
                )

                if (method != PaymentMethod.values().last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = SurfaceLight
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) method.color.copy(alpha = 0.1f) else Color.Transparent,
        border = if (isSelected) BorderStroke(2.dp, method.color) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = method.color.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        method.icon,
                        contentDescription = null,
                        tint = method.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    method.displayName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp
                )

                Text(
                    when (method) {
                        PaymentMethod.CARD -> "Visa, Mastercard, Verve"
                        PaymentMethod.BANK -> "Transfer to bank account"
                        PaymentMethod.USSD -> "Dial code to pay"
                        PaymentMethod.PAY_ON_DELIVERY -> "Pay when you receive"
                    },
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = method.color
                )
            )
        }
    }
}

@Composable
private fun OrderSummary(
    subtotal: Double,
    discount: Double,
    serviceFee: Double,
    deliveryFee: Double,
    total: Double,
    isServiceBooking: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Order Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SummaryRow("Subtotal", subtotal)

            if (discount > 0) {
                SummaryRow("Discount", -discount, isDiscount = true)
            }

            SummaryRow("Service Fee (5%)", serviceFee)

            if (!isServiceBooking) {
                SummaryRow("Delivery Fee", deliveryFee)
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = SurfaceLight,
                thickness = 2.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    formatCurrency(total),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = TextSecondary,
            fontSize = 14.sp
        )

        Text(
            (if (isDiscount) "-" else "") + formatCurrency(amount),
            fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (isDiscount) SuccessGreen else TextPrimary
        )
    }
}

@Composable
private fun SecurityBadge() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            "Secured by 256-bit SSL Encryption",
            fontSize = 12.sp,
            color = TextTertiary
        )
    }
}

@Composable
private fun CheckoutBottomBar(
    total: Double,
    onPlaceOrder: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Amount",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        formatCurrency(total),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangePrimary
                    )
                }

                Text(
                    "VAT included",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Text(
                        "Place Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressEditDialog(
    currentAddress: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newAddress by remember { mutableStateOf(currentAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Address") },
        text = {
            OutlinedTextField(
                value = newAddress,
                onValueChange = { newAddress = it },
                label = { Text("Delivery Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(newAddress) },
                enabled = newAddress.isNotBlank()
            ) {
                Text("Save", color = OrangePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun calculateDiscount(subtotal: Double, code: String): Double {
    return when (code.uppercase()) {
        "WELCOME10" -> subtotal * 0.10
        "SAVE20" -> subtotal * 0.20
        else -> 0.0
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun CheckoutScreenPreview() {
    val sampleItems = listOf(
        CheckoutItem(
            id = "1",
            name = "Wireless Earbuds Pro",
            imageUrl = "",
            price = 59999.0,
            quantity = 1,
            variant = "Black"
        ),
        CheckoutItem(
            id = "2",
            name = "Phone Case",
            imageUrl = "",
            price = 3500.0,
            quantity = 2
        )
    )

    // Calculate total for preview
    val previewTotal = sampleItems.sumOf { it.price * it.quantity } * 1.05 + 1500.0 // Include fees

    BunnixTheme {
        CheckoutScreen(
            items = sampleItems,
            isServiceBooking = false, // ADD THIS
            onBack = {},
            onPaymentMethodSelect = {},
            onApplyPromo = { it.uppercase() == "WELCOME10" },
            onPlaceOrder = {},
            total = previewTotal // ADD THIS - was missing!
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodItemPreview() {
    BunnixTheme {
        Column {
            PaymentMethodItem(
                method = PaymentMethod.CARD,
                isSelected = true,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            PaymentMethodItem(
                method = PaymentMethod.USSD,
                isSelected = false,
                onClick = {}
            )
        }
    }
}