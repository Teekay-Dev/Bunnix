package com.example.bunnix.frontend

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.database.models.*
import com.example.bunnix.presentation.viewmodel.ReceiptViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ==================== COLORS (Matching Your App) ====================

private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val OrangeDark = Color(0xFFE85D04)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val DividerColor = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReceiptScreen(
    orderId: String? = null,
    receiptId: String? = null,
    viewModel: ReceiptViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onShareClick: (Receipt) -> Unit = {},
    onDownloadClick: (Receipt) -> Unit = {},
    onPrintClick: (Receipt) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkAndLoadReceipt(orderId = orderId, receiptId = receiptId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Receipt",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OrangePrimary
                        )
                    }
                },
                actions = {
                    if (uiState is ReceiptUiState.Success) {
                        val receipt = (uiState as ReceiptUiState.Success).receipt
                        IconButton(onClick = { onShareClick(receipt) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = OrangePrimary
                            )
                        }
                        IconButton(onClick = { onDownloadClick(receipt) }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = OrangePrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is ReceiptUiState.Loading -> LoadingState()
                is ReceiptUiState.Empty -> EmptyState(onRefresh = { viewModel.refresh() })
                is ReceiptUiState.Error -> ErrorState(
                    message = (state as ReceiptUiState.Error).message,
                    onRetry = { viewModel.refresh() }
                )
                is ReceiptUiState.Success -> ReceiptContent(
                    receipt = (state as ReceiptUiState.Success).receipt,
                    onPrintClick = onPrintClick
                )
            }
        }
    }
}

// ==================== EMPTY STATE ====================

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated Illustration
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background pulse
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(OrangePrimary, OrangePrimary.copy(alpha = 0f))
                        ),
                        shape = CircleShape
                    )
            )

            // Floating receipt icon
            Box(
                modifier = Modifier
                    .offset(y = floatAnim.dp)
                    .size(120.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = OrangePrimary.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .width(60.dp)
                                .height(4.dp)
                                .background(OrangeSoft, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            // Decorative floating elements
            Box(
                modifier = Modifier
                    .offset(x = (-60).dp, y = (-40).dp + floatAnim.dp * 0.5f)
                    .size(16.dp)
                    .background(OrangeLight, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = 70.dp, y = 30.dp + floatAnim.dp * 0.3f)
                    .size(24.dp)
                    .background(OrangeSoft, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = 50.dp - floatAnim.dp * 0.4f)
                    .size(12.dp)
                    .background(OrangePrimary.copy(alpha = 0.6f), CircleShape)
            )
        }

        Text(
            text = "No Receipt Yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your receipt will appear here once your payment is confirmed and the order is processed.",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(0.7f)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Check Again",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Navigate to orders */ },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(OrangePrimary, OrangeDark))
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text(
                "View Orders",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==================== RECEIPT CONTENT ====================

@Composable
private fun ReceiptContent(
    receipt: Receipt,
    onPrintClick: (Receipt) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            ReceiptCard(receipt = receipt)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.Print,
                    label = "Print",
                    onClick = { onPrintClick(receipt) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Default.Email,
                    label = "Email",
                    onClick = { /* Handle email */ },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Default.Help,
                    label = "Support",
                    onClick = { /* Handle support */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ReceiptCard(receipt: Receipt) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = OrangePrimary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(OrangePrimary, OrangeDark),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Success Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Payment Successful",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dateFormatter.format(receipt.createdAt.toDate()),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            // Zigzag edge
            ReceiptEdge(color = OrangePrimary, isTop = false)

            // Content
            Column(modifier = Modifier.padding(24.dp)) {
                // Vendor Info
                VendorInfoSection(
                    vendorName = receipt.vendorName,
                    vendorAddress = receipt.vendorAddress,
                    vendorPhone = receipt.vendorPhone,
                    vendorEmail = receipt.vendorEmail,
                    vendorTaxId = receipt.vendorTaxId
                )

                Divider(
                    color = DividerColor,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                // Customer Info
                CustomerInfoSection(
                    customerName = receipt.customerName,
                    customerEmail = receipt.customerEmail,
                    customerPhone = receipt.customerPhone,
                    customerAddress = receipt.customerAddress
                )

                Divider(
                    color = DividerColor,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                // Items
                ItemsSection(items = receipt.items)

                Divider(
                    color = DividerColor,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                // Totals
                TotalsSection(
                    subtotal = receipt.subtotal,
                    discount = receipt.totalDiscount,
                    tax = receipt.totalTax,
                    deliveryFee = receipt.deliveryFee,
                    grandTotal = receipt.grandTotal,
                    currencyFormatter = currencyFormatter
                )

                Divider(
                    color = DividerColor,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                // Payment Info
                PaymentSection(
                    paymentDetails = receipt.paymentDetails,
                    receiptNumber = receipt.receiptNumber
                )

                // Notes
                if (!receipt.notes.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NotesSection(notes = receipt.notes)
                }

                // Footer
                Spacer(modifier = Modifier.height(24.dp))
                ReceiptFooter()
            }
        }
    }
}

@Composable
private fun VendorInfoSection(
    vendorName: String,
    vendorAddress: String,
    vendorPhone: String,
    vendorEmail: String,
    vendorTaxId: String?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        // Logo placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(listOf(OrangePrimary, OrangeDark)),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = vendorName.take(1).uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = vendorName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = vendorAddress,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tel: $vendorPhone",
            fontSize = 13.sp,
            color = TextSecondary
        )

        if (!vendorTaxId.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tax ID: $vendorTaxId",
                fontSize = 12.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun CustomerInfoSection(
    customerName: String,
    customerEmail: String,
    customerPhone: String,
    customerAddress: String
) {
    Column {
        Text(
            text = "Bill To",
            fontSize = 12.sp,
            color = TextTertiary,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = customerName,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        if (customerEmail.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TextTertiary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = customerEmail,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        if (customerPhone.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TextTertiary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = customerPhone,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        if (customerAddress.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TextTertiary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = customerAddress,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ItemsSection(items: List<ReceiptItem>) {
    Column {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "ITEM",
                fontSize = 11.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = "QTY",
                fontSize = 11.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "PRICE",
                fontSize = 11.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Items
        items.forEach { item ->
            ReceiptItemRow(item = item)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReceiptItemRow(item: ReceiptItem) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (!item.description.isNullOrEmpty()) {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 16.sp
                    )
                }
            }

            Text(
                text = "×${item.quantity}",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )

            Text(
                text = currencyFormatter.format(item.total),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        // Discount/Tax indicators
        if (item.discount > 0 || item.taxRate > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                if (item.discount > 0) {
                    Badge(
                        containerColor = OrangeSoft,
                        contentColor = OrangeDark
                    ) {
                        Text(
                            text = "-${item.discount.toInt()}% OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (item.taxRate > 0) {
                    Badge(
                        containerColor = Color(0xFFE0F2FE),
                        contentColor = Color(0xFF0284C7)
                    ) {
                        Text(
                            text = "VAT ${item.taxRate.toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalsSection(
    subtotal: Double,
    discount: Double,
    tax: Double,
    deliveryFee: Double,
    grandTotal: Double,
    currencyFormatter: NumberFormat
) {
    Column {
        TotalRow(label = "Subtotal", amount = subtotal, currencyFormatter = currencyFormatter, isBold = false)

        if (discount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow(
                label = "Discount",
                amount = -discount,
                currencyFormatter = currencyFormatter,
                isBold = false,
                isDiscount = true
            )
        }

        if (tax > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow(label = "VAT/Tax", amount = tax, currencyFormatter = currencyFormatter, isBold = false)
        }

        if (deliveryFee > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            TotalRow(label = "Delivery Fee", amount = deliveryFee, currencyFormatter = currencyFormatter, isBold = false)
        }

        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 12.dp))

        TotalRow(
            label = "Total Amount",
            amount = grandTotal,
            currencyFormatter = currencyFormatter,
            isBold = true,
            isTotal = true
        )
    }
}

@Composable
private fun TotalRow(
    label: String,
    amount: Double,
    currencyFormatter: NumberFormat,
    isBold: Boolean,
    isTotal: Boolean = false,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) TextPrimary else TextSecondary
        )

        Text(
            text = if (isDiscount) "-${currencyFormatter.format(kotlin.math.abs(amount))}"
            else currencyFormatter.format(amount),
            fontSize = if (isTotal) 20.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.ExtraBold else if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isTotal -> OrangePrimary
                isDiscount -> SuccessGreen
                else -> TextPrimary
            }
        )
    }
}

@Composable
private fun PaymentSection(
    paymentDetails: PaymentDetails,
    receiptNumber: String
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "Payment Method", fontSize = 12.sp, color = TextTertiary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentMethodIcon(method = paymentDetails.method)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (paymentDetails.method) {
                            PaymentMethod.CASH -> "Cash"
                            PaymentMethod.CARD -> "${paymentDetails.cardBrand ?: "Card"} •••• ${paymentDetails.cardLastFour ?: "****"}"
                            PaymentMethod.UPI -> "Online Payment"
                            PaymentMethod.BANK_TRANSFER -> "Bank Transfer"
                            PaymentMethod.PAY_ON_DELIVERY -> "Pay on Delivery"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Receipt #", fontSize = 12.sp, color = TextTertiary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = receiptNumber.takeLast(8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Transaction ID:", fontSize = 12.sp, color = TextTertiary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = paymentDetails.transactionId,
                fontSize = 12.sp,
                color = TextSecondary,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }

        if (!paymentDetails.reference.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Reference:", fontSize = 12.sp, color = TextTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = paymentDetails.reference,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        PaymentStatusBadge(status = paymentDetails.status)
    }
}

@Composable
private fun PaymentMethodIcon(method: PaymentMethod) {
    val icon = when (method) {
        PaymentMethod.CASH -> Icons.Default.Payments
        PaymentMethod.CARD -> Icons.Default.CreditCard
        PaymentMethod.UPI -> Icons.Default.Smartphone
        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
        PaymentMethod.PAY_ON_DELIVERY -> Icons.Default.LocalShipping
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color = OrangeSoft, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun PaymentStatusBadge(status: PaymentStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        PaymentStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Completed")
        PaymentStatus.PENDING -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "Pending")
        PaymentStatus.FAILED -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Failed")
        PaymentStatus.REFUNDED -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), "Refunded")
        PaymentStatus.PARTIALLY_REFUNDED -> Triple(Color(0xFFF3E8FF), Color(0xFF7C3AED), "Partially Refunded")
    }

    Badge(containerColor = backgroundColor, contentColor = textColor) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NotesSection(notes: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = OrangeSoft, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Note",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notes,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ReceiptFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        // Barcode simulation
        BarcodeLines()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Thank you for your business!",
            fontSize = 14.sp,
            color = TextTertiary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Scan to verify authenticity",
            fontSize = 11.sp,
            color = TextTertiary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun BarcodeLines() {
    val randomWidths = remember { List(40) { (2..6).random() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(40.dp)
    ) {
        randomWidths.forEach { width ->
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .fillMaxHeight()
                    .background(if (width > 4) TextPrimary else TextSecondary)
            )
        }
    }
}

// ==================== UTILITY COMPONENTS ====================

@Composable
private fun ReceiptEdge(color: Color, isTop: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .drawBehind {
                val path = Path().apply {
                    val zigzagHeight = 12.dp.toPx()
                    val zigzagWidth = 20.dp.toPx()
                    val totalWidth = size.width
                    var x = 0f

                    moveTo(0f, if (isTop) zigzagHeight else 0f)

                    while (x < totalWidth) {
                        x += zigzagWidth / 2
                        lineTo(x.coerceAtMost(totalWidth), if (isTop) 0f else zigzagHeight)
                        x += zigzagWidth / 2
                        lineTo(x.coerceAtMost(totalWidth), if (isTop) zigzagHeight else 0f)
                    }

                    lineTo(totalWidth, if (isTop) zigzagHeight else 0f)
                    lineTo(0f, if (isTop) zigzagHeight else 0f)
                    close()
                }

                drawPath(path = path, color = color)
            }
    )
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        FilledTonalButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = OrangeSoft,
                contentColor = OrangePrimary
            ),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = OrangePrimary, strokeWidth = 4.dp)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = OrangePrimary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
            Text("Try Again")
        }
    }
}

// ==================== PREVIEW ====================

/*
@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ReceiptScreenPreview() {
    MaterialTheme {
        ReceiptScreen()
    }
}
*/