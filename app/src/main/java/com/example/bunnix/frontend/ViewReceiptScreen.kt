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
import com.example.bunnix.database.models.Receipt
import com.example.bunnix.database.models.PaymentMethod
import com.example.bunnix.database.models.PaymentStatus
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.bunnix.database.models.*
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
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









// ==================== MAIN PREVIEWS ====================

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Card Payment")
@Composable
fun ViewReceiptScreenCardPaymentPreview() {
    BunnixTheme {
        ViewReceiptScreen(
            orderId = "ORD-2024-001",
            receiptId = "RCT-2024-001",
            onBackClick = {},
            onShareClick = {},
            onDownloadClick = {},
            onPrintClick = {}
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Online Payment (UPI)")
@Composable
fun ViewReceiptScreenOnlinePaymentPreview() {
    BunnixTheme {
        // You'll need to pass a mock receipt here
        // For preview purposes, you can create a wrapper composable
        ReceiptContentPreview(
            receipt = createSampleReceipt(paymentMethod = PaymentMethod.UPI)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Bank Transfer")
@Composable
fun ViewReceiptScreenBankTransferPreview() {
    BunnixTheme {
        ReceiptContentPreview(
            receipt = createSampleReceipt(paymentMethod = PaymentMethod.BANK_TRANSFER)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Pay on Delivery")
@Composable
fun ViewReceiptScreenPayOnDeliveryPreview() {
    BunnixTheme {
        ReceiptContentPreview(
            receipt = createSampleReceipt(
                paymentMethod = PaymentMethod.PAY_ON_DELIVERY,
                paymentStatus = PaymentStatus.PENDING
            )
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - With Discounts")
@Composable
fun ViewReceiptScreenWithDiscountsPreview() {
    BunnixTheme {
        ReceiptContentPreview(
            receipt = createReceiptWithDiscounts()
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Multiple Items")
@Composable
fun ViewReceiptScreenMultipleItemsPreview() {
    BunnixTheme {
        ReceiptContentPreview(
            receipt = createReceiptWithMultipleItems()
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Receipt - Empty State")
@Composable
fun ViewReceiptScreenEmptyStatePreview() {
    BunnixTheme {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize()
        ) {
            // Call the EmptyState composable directly
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                androidx.compose.material3.Text(
                    text = "No Receipt Yet",
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
                androidx.compose.material3.Text(
                    text = "Your receipt will appear here once payment is confirmed",
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// ==================== HELPER PREVIEW WRAPPER ====================

@Composable
private fun ReceiptContentPreview(receipt: Receipt) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFFAFAFA))
    ) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            item {
                ReceiptCard(receipt = receipt)
            }
        }
    }
}

// ==================== SAMPLE DATA CREATORS ====================

private fun createSampleReceipt(
    paymentMethod: PaymentMethod = PaymentMethod.CARD,
    paymentStatus: PaymentStatus = PaymentStatus.COMPLETED
): Receipt {
    val paymentDetails = when (paymentMethod) {
        PaymentMethod.CARD -> PaymentDetails(
            method = PaymentMethod.CARD,
            transactionId = "TXN${UUID.randomUUID().toString().take(12)}",
            reference = "PSK-2024-001-ABC",
            status = paymentStatus,
            paidAt = Timestamp.now(),
            cardLastFour = "4242",
            cardBrand = "Visa"
        )
        PaymentMethod.UPI -> PaymentDetails(
            method = PaymentMethod.UPI,
            transactionId = "TXN${UUID.randomUUID().toString().take(12)}",
            reference = "FLWREF-2024-001",
            status = paymentStatus,
            paidAt = Timestamp.now()
        )
        PaymentMethod.BANK_TRANSFER -> PaymentDetails(
            method = PaymentMethod.BANK_TRANSFER,
            transactionId = "BNKTRF-${UUID.randomUUID().toString().take(10)}",
            reference = "TRANSFER-2024-001",
            status = paymentStatus,
            paidAt = Timestamp.now(),
            bankName = "GTBank",
            accountNumber = "****5678"
        )
        PaymentMethod.PAY_ON_DELIVERY -> PaymentDetails(
            method = PaymentMethod.PAY_ON_DELIVERY,
            transactionId = "COD-${UUID.randomUUID().toString().take(10)}",
            status = PaymentStatus.PENDING
        )
        PaymentMethod.CASH -> PaymentDetails(
            method = PaymentMethod.CASH,
            transactionId = "CASH-${UUID.randomUUID().toString().take(10)}",
            status = paymentStatus,
            paidAt = Timestamp.now()
        )
    }

    return Receipt(
        id = "receipt_${UUID.randomUUID()}",
        receiptNumber = "RCT-2024-${(10000..99999).random()}",
        orderId = "ORD-2024-${(10000..99999).random()}",

        // Vendor Info
        vendorId = "vendor_001",
        vendorName = "TechHub Electronics",
        vendorAddress = "123 Market Street, Victoria Island, Lagos, Nigeria",
        vendorPhone = "+234 801 234 5678",
        vendorEmail = "contact@techhub.ng",
        vendorTaxId = "TIN-12345678",
        vendorLogoUrl = null,

        // Customer Info
        customerId = "customer_001",
        customerName = "John Doe",
        customerEmail = "john.doe@email.com",
        customerPhone = "+234 802 345 6789",
        customerAddress = "45 Allen Avenue, Ikeja",
        customerCity = "Lagos",
        customerState = "Lagos State",

        // Items
        items = listOf(
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_001",
                name = "MacBook Pro 16\"",
                description = "M3 Max, 36GB RAM, 1TB SSD, Space Black",
                quantity = 1,
                unitPrice = 2499999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_002",
                name = "Magic Mouse",
                description = "USB-C, Black",
                quantity = 1,
                unitPrice = 49999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_003",
                name = "USB-C Cable",
                description = "2m, MagSafe 3",
                quantity = 2,
                unitPrice = 19999.0,
                discount = 0.0,
                taxRate = 7.5
            )
        ),

        // Payment Details
        paymentDetails = paymentDetails,

        // Timestamps
        createdAt = Timestamp.now(),
        updatedAt = null,

        // Additional Info
        notes = "Thank you for your purchase! We appreciate your business.",
        deliveryNotes = "Handle with care - fragile electronics",
        subtotal = 2589996.0,
        totalDiscount = 0.0,
        totalTax = 194249.70,
        deliveryFee = 5000.0,
        grandTotal = 2789245.70
    )
}

private fun createReceiptWithDiscounts(): Receipt {
    return Receipt(
        id = "receipt_${UUID.randomUUID()}",
        receiptNumber = "RCT-2024-${(10000..99999).random()}",
        orderId = "ORD-2024-${(10000..99999).random()}",

        vendorId = "vendor_002",
        vendorName = "Premium Electronics Store",
        vendorAddress = "456 Shopping Plaza, Lekki Phase 1, Lagos",
        vendorPhone = "+234 803 456 7890",
        vendorEmail = "sales@premium.ng",
        vendorTaxId = "TIN-87654321",

        customerId = "customer_002",
        customerName = "Jane Smith",
        customerEmail = "jane.smith@email.com",
        customerPhone = "+234 804 567 8901",
        customerAddress = "78 Admiralty Way, Lekki",
        customerCity = "Lagos",
        customerState = "Lagos State",

        items = listOf(
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_004",
                name = "iPhone 15 Pro Max",
                description = "256GB, Natural Titanium",
                quantity = 1,
                unitPrice = 1299999.0,
                discount = 10.0, // 10% discount
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_005",
                name = "AirPods Pro (2nd Gen)",
                description = "USB-C",
                quantity = 2,
                unitPrice = 89999.0,
                discount = 15.0, // 15% discount
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_006",
                name = "MagSafe Charger",
                description = "15W Fast Charging",
                quantity = 1,
                unitPrice = 25999.0,
                discount = 20.0, // 20% discount
                taxRate = 7.5
            )
        ),

        paymentDetails = PaymentDetails(
            method = PaymentMethod.CARD,
            transactionId = "TXN${UUID.randomUUID().toString().take(12)}",
            reference = "PSK-DISC-2024-001",
            status = PaymentStatus.COMPLETED,
            paidAt = Timestamp.now(),
            cardLastFour = "5555",
            cardBrand = "Mastercard"
        ),

        createdAt = Timestamp.now(),
        notes = "Special discount applied! Thanks for being a valued customer.",
        deliveryNotes = "Free express delivery for orders above ₦1M",
        subtotal = 1595996.0,
        totalDiscount = 228099.40,
        totalTax = 102592.25,
        deliveryFee = 0.0, // Free delivery
        grandTotal = 1470488.85
    )
}

private fun createReceiptWithMultipleItems(): Receipt {
    return Receipt(
        id = "receipt_${UUID.randomUUID()}",
        receiptNumber = "RCT-2024-${(10000..99999).random()}",
        orderId = "ORD-2024-${(10000..99999).random()}",

        vendorId = "vendor_003",
        vendorName = "MegaTech SuperStore",
        vendorAddress = "789 Tech Mall, Ikeja City Mall, Lagos",
        vendorPhone = "+234 805 678 9012",
        vendorEmail = "info@megatech.ng",
        vendorTaxId = "TIN-11223344",

        customerId = "customer_003",
        customerName = "Michael Johnson",
        customerEmail = "m.johnson@email.com",
        customerPhone = "+234 806 789 0123",
        customerAddress = "22 Opebi Road, Ikeja",
        customerCity = "Lagos",
        customerState = "Lagos State",

        items = listOf(
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_007",
                name = "Samsung Galaxy S24 Ultra",
                description = "512GB, Titanium Black",
                quantity = 1,
                unitPrice = 1099999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_008",
                name = "Galaxy Buds 2 Pro",
                description = "Graphite",
                quantity = 1,
                unitPrice = 79999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_009",
                name = "45W USB-C Charger",
                description = "Fast Charging",
                quantity = 2,
                unitPrice = 15999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_010",
                name = "Screen Protector",
                description = "Tempered Glass",
                quantity = 3,
                unitPrice = 2999.0,
                discount = 5.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_011",
                name = "Phone Case",
                description = "Clear Silicon",
                quantity = 2,
                unitPrice = 3999.0,
                discount = 0.0,
                taxRate = 7.5
            ),
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                productId = "prod_012",
                name = "USB-C Cable",
                description = "1m, Data Transfer",
                quantity = 3,
                unitPrice = 4999.0,
                discount = 10.0,
                taxRate = 7.5
            )
        ),

        paymentDetails = PaymentDetails(
            method = PaymentMethod.UPI,
            transactionId = "FLW${UUID.randomUUID().toString().take(15)}",
            reference = "FLWREF-MULTI-2024",
            status = PaymentStatus.COMPLETED,
            paidAt = Timestamp.now()
        ),

        createdAt = Timestamp.now(),
        notes = "Bulk purchase discount eligible for future orders!",
        deliveryNotes = "All items packed securely with bubble wrap",
        subtotal = 1247989.0,
        totalDiscount = 1799.55,
        totalTax = 93464.21,
        deliveryFee = 3500.0,
        grandTotal = 1343153.66
    )
}