package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat

// Modern Colors - ALL DEFINED HERE ONLY
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
private val WarningYellow = Color(0xFFF59E0B) // ADDED THIS

// Payment Categories
sealed class PaymentCategory(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    object Card : PaymentCategory("Card Payment", Icons.Default.CreditCard, Color(0xFF1A1F71))
    object OnlineBank : PaymentCategory("Online Bank", Icons.Default.AccountBalance, Color(0xFF00BFA5))
    object BankTransfer : PaymentCategory("Bank Transfer", Icons.Default.AccountBalanceWallet, Color(0xFF6B7280))
    object USSD : PaymentCategory("USSD", Icons.Default.PhoneAndroid, Color(0xFF10B981))
    object PayOnDelivery : PaymentCategory("Pay on Delivery", Icons.Default.LocalShipping, OrangePrimary)
}

// Online Bank Options
data class OnlineBankOption(
    val name: String,
    val logoUrl: String,
    val color: Color
)

val onlineBanks = listOf(
    OnlineBankOption("Opay", "https://opayweb.com/static/images/logo.png", Color(0xFF00BFA5)),
    OnlineBankOption("Moniepoint", "https://moniepoint.com/logo.png", Color(0xFF1A1F71)),
    OnlineBankOption("Kuda", "https://kuda.com/logo.png", Color(0xFF9B5DE5)),
    OnlineBankOption("PalmPay", "https://palmpay.com/logo.png", Color(0xFFFF6B35)),
    OnlineBankOption("Carbon", "https://getcarbon.co/logo.png", Color(0xFF2EC4B6))
)

// Traditional Banks
val traditionalBanks = listOf(
    "GTBank" to "Guaranty Trust Bank",
    "First Bank" to "First Bank of Nigeria",
    "UBA" to "United Bank for Africa",
    "Zenith Bank" to "Zenith Bank Plc",
    "Access Bank" to "Access Bank Plc",
    "Fidelity Bank" to "Fidelity Bank",
    "Union Bank" to "Union Bank of Nigeria",
    "Stanbic IBTC" to "Stanbic IBTC Bank"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    total: Double,
    orderId: String,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // ADDED THIS for coroutines

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // States
    var selectedCategory by remember { mutableStateOf<PaymentCategory>(PaymentCategory.OnlineBank) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    // Card form states
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    // Online bank states
    var selectedOnlineBank by remember { mutableStateOf<OnlineBankOption?>(onlineBanks.first()) }
    var onlineBankPhone by remember { mutableStateOf("") }

    // Bank transfer states
    var selectedTraditionalBank by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showBankDetails by remember { mutableStateOf(false) }

    // USSD states
    var selectedUSSDBank by remember { mutableStateOf<Pair<String, String>?>(null) }
    var ussdCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            PaymentTopBar(
                orderId = orderId,
                onBack = onBack
            )
        },
        bottomBar = {
            if (!showSuccess) {
                PaymentBottomBar(
                    total = total,
                    isEnabled = when (selectedCategory) {
                        PaymentCategory.Card -> cardNumber.length >= 16 && cardName.isNotBlank()
                        PaymentCategory.OnlineBank -> selectedOnlineBank != null && onlineBankPhone.length >= 10
                        PaymentCategory.BankTransfer -> selectedTraditionalBank != null
                        PaymentCategory.USSD -> selectedUSSDBank != null
                        PaymentCategory.PayOnDelivery -> true
                    },
                    isProcessing = isProcessing,
                    onPay = {
                        isProcessing = true
                        // FIXED: Use scope.launch instead of GlobalScope
                        scope.launch {
                            delay(2000)
                            isProcessing = false
                            showSuccess = true
                            delay(1500)
                            onPaymentSuccess()
                        }
                    }
                )
            }
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 4 }
        ) {
            if (showSuccess) {
                PaymentSuccessAnimation(total = total)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Total Display
                    TotalDisplay(total = total)

                    // Category Selection
                    CategorySelection(
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it }
                    )

                    // Payment Form
                    when (selectedCategory) {
                        PaymentCategory.Card -> CardPaymentForm(
                            cardNumber = cardNumber,
                            onCardNumberChange = { cardNumber = it },
                            cardName = cardName,
                            onCardNameChange = { cardName = it },
                            cardExpiry = cardExpiry,
                            onCardExpiryChange = { cardExpiry = it },
                            cardCvv = cardCvv,
                            onCardCvvChange = { cardCvv = it }
                        )

                        PaymentCategory.OnlineBank -> OnlineBankSection(
                            selectedBank = selectedOnlineBank,
                            onBankSelect = { selectedOnlineBank = it },
                            phoneNumber = onlineBankPhone,
                            onPhoneChange = { onlineBankPhone = it }
                        )

                        PaymentCategory.BankTransfer -> BankTransferSection(
                            orderId = orderId, // FIXED: Pass orderId
                            selectedBank = selectedTraditionalBank,
                            onBankSelect = {
                                selectedTraditionalBank = it
                                showBankDetails = true
                            },
                            showDetails = showBankDetails,
                            total = total
                        )

                        PaymentCategory.USSD -> USSDSection(
                            selectedBank = selectedUSSDBank,
                            onBankSelect = {
                                selectedUSSDBank = it
                                ussdCode = generateUSSDCode(it.first, total)
                            },
                            ussdCode = ussdCode,
                            total = total
                        )

                        PaymentCategory.PayOnDelivery -> PayOnDeliverySection()
                    }

                    // Security Info
                    SecurityInfo()

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentTopBar(
    orderId: String,
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
                        "Payment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "Order #$orderId",
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
private fun TotalDisplay(total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = OrangePrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Amount to Pay",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                formatCurrency(total),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "Secure Payment",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelection(
    selectedCategory: PaymentCategory,
    onCategorySelect: (PaymentCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Select Payment Method",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Online Banks (Featured)
        Text(
            "Online Banking",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PaymentCategoryChip(
            category = PaymentCategory.OnlineBank,
            isSelected = selectedCategory == PaymentCategory.OnlineBank,
            onClick = { onCategorySelect(PaymentCategory.OnlineBank) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Other Methods
        Text(
            "Other Methods",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                PaymentCategory.Card,
                PaymentCategory.BankTransfer,
                PaymentCategory.USSD,
                PaymentCategory.PayOnDelivery
            ).forEach { category ->
                PaymentCategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelect(category) }
                )
            }
        }
    }
}

@Composable
private fun PaymentCategoryChip(
    category: PaymentCategory,
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
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) category.color.copy(alpha = 0.15f) else SurfaceLight,
        border = if (isSelected) BorderStroke(2.dp, category.color) else BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = if (isSelected) category.color else TextSecondary,
                modifier = Modifier.size(22.dp)
            )

            Text(
                category.displayName,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) category.color else TextPrimary,
                fontSize = 14.sp
            )

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardPaymentForm(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardName: String,
    onCardNameChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    cardCvv: String,
    onCardCvvChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Card Details",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card Number
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    if (it.length <= 19) onCardNumberChange(it.filter { c -> c.isDigit() || c == ' ' })
                },
                label = { Text("Card Number") },
                placeholder = { Text("0000 0000 0000 0000") },
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, null, tint = TextTertiary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = SurfaceLight,
                    unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                    focusedBorderColor = PaymentCategory.Card.color
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card Name
            OutlinedTextField(
                value = cardName,
                onValueChange = onCardNameChange,
                label = { Text("Cardholder Name") },
                placeholder = { Text("JOHN DOE") },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = TextTertiary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = SurfaceLight,
                    unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                    focusedBorderColor = PaymentCategory.Card.color
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expiry and CVV
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = {
                        if (it.length <= 5) onCardExpiryChange(it.filter { c -> c.isDigit() || c == '/' })
                    },
                    label = { Text("MM/YY") },
                    placeholder = { Text("12/25") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = SurfaceLight,
                        unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                        focusedBorderColor = PaymentCategory.Card.color
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = cardCvv,
                    onValueChange = {
                        if (it.length <= 4) onCardCvvChange(it.filter { c -> c.isDigit() })
                    },
                    label = { Text("CVV") },
                    placeholder = { Text("123") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = TextTertiary)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = SurfaceLight,
                        unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                        focusedBorderColor = PaymentCategory.Card.color
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Supported cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("VISA", "MASTERCARD", "VERVE").forEach { card ->
                    Surface(
                        color = SurfaceLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            card,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnlineBankSection(
    selectedBank: OnlineBankOption?,
    onBankSelect: (OnlineBankOption) -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Select Online Bank",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bank Grid
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                onlineBanks.forEach { bank ->
                    OnlineBankCard(
                        bank = bank,
                        isSelected = selectedBank?.name == bank.name,
                        onClick = { onBankSelect(bank) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Number
            Text(
                "Phone Number Linked to ${selectedBank?.name ?: "Bank"}",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 11) onPhoneChange(it.filter { c -> c.isDigit() })
                },
                placeholder = { Text("08012345678") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, null, tint = TextTertiary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = SurfaceLight,
                    unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                    focusedBorderColor = PaymentCategory.OnlineBank.color
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Instructions
            Surface(
                color = PaymentCategory.OnlineBank.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "How it works:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PaymentCategory.OnlineBank.color
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        "1. Enter your registered phone number",
                        "2. Tap 'Pay Now' to initiate payment",
                        "3. Check your ${selectedBank?.name ?: "bank"} app for payment request",
                        "4. Authorize the payment in your app"
                    ).forEach { step ->
                        Text(
                            step,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnlineBankCard(
    bank: OnlineBankOption,
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
        modifier = Modifier
            .width(100.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) bank.color.copy(alpha = 0.15f) else SurfaceLight,
        border = if (isSelected) BorderStroke(2.dp, bank.color) else BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bank Logo Placeholder
            Surface(
                color = bank.color.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        bank.name.first().toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = bank.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                bank.name,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) bank.color else TextPrimary
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = bank.color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankTransferSection(
    orderId: String, // ADDED THIS PARAMETER
    selectedBank: Pair<String, String>?,
    onBankSelect: (Pair<String, String>) -> Unit,
    showDetails: Boolean,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Select Bank",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bank Dropdown
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedBank?.second ?: "Select your bank",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = SurfaceLight,
                        unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                        focusedBorderColor = PaymentCategory.BankTransfer.color
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    traditionalBanks.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(bank.second) },
                            onClick = {
                                onBankSelect(bank)
                                expanded = false
                            },
                            leadingIcon = {
                                if (selectedBank?.first == bank.first) {
                                    Icon(Icons.Default.Check, null, tint = OrangePrimary)
                                }
                            }
                        )
                    }
                }
            }

            // Bank Account Details
            AnimatedVisibility(
                visible = showDetails,
                enter = expandVertically() + fadeIn()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        color = SuccessGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Transfer to this account:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SuccessGreen
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow("Bank Name", "Bunnix Technologies Ltd")
                            DetailRow("Account Number", "0123456789")
                            DetailRow("Account Name", "BUNNIX LIMITED")
                            DetailRow("Amount", formatCurrency(total))

                            Spacer(modifier = Modifier.height(16.dp))

                            // Copy button
                            Button(
                                onClick = { /* Copy account details */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy Account Details")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "⚠️ Important: Use your Order ID ($orderId) as payment description",
                        fontSize = 12.sp,
                        color = WarningYellow,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 13.sp,
            color = TextSecondary
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun USSDSection(
    selectedBank: Pair<String, String>?,
    onBankSelect: (Pair<String, String>) -> Unit,
    ussdCode: String,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Select Your Bank",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bank Grid
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                traditionalBanks.take(6).forEach { bank ->
                    BankChip(
                        bankCode = bank.first,
                        bankName = bank.second,
                        isSelected = selectedBank?.first == bank.first,
                        onClick = { onBankSelect(bank) }
                    )
                }
            }

            // USSD Code Display
            AnimatedVisibility(
                visible = ussdCode.isNotEmpty(),
                enter = expandVertically() + fadeIn()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        color = PaymentCategory.USSD.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Dial this code:",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = 4.dp
                            ) {
                                Text(
                                    ussdCode,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = PaymentCategory.USSD.color,
                                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* Dial code */ },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PaymentCategory.USSD.color)
                            ) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dial Now")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BankChip(
    bankCode: String,
    bankName: String,
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
        color = if (isSelected) PaymentCategory.USSD.color.copy(alpha = 0.15f) else SurfaceLight,
        border = if (isSelected) BorderStroke(2.dp, PaymentCategory.USSD.color) else BorderStroke(1.dp, TextTertiary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                bankCode,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isSelected) PaymentCategory.USSD.color else TextPrimary
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = PaymentCategory.USSD.color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PayOnDeliverySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = PaymentCategory.PayOnDelivery.color.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = PaymentCategory.PayOnDelivery.color,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Pay on Delivery",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Pay with cash or card when your order arrives",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Requirements
            Surface(
                color = SurfaceLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Please ensure:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    listOf(
                        "• Someone is available to receive the order",
                        "• Exact amount or POS card ready",
                        "• Valid ID may be required for verification"
                    ).forEach { item ->
                        Text(
                            item,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Shield,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            "Your payment is secured with 256-bit encryption",
            fontSize = 12.sp,
            color = TextTertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaymentBottomBar(
    total: Double,
    isEnabled: Boolean,
    isProcessing: Boolean,
    onPay: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = onPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) OrangePrimary else TextTertiary.copy(alpha = 0.3f),
                    disabledContainerColor = TextTertiary.copy(alpha = 0.3f)
                ),
                enabled = isEnabled && !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Text(
                            "Pay ${formatCurrency(total)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentSuccessAnimation(total: Double) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Circle
        Surface(
            modifier = Modifier
                .size(150.dp)
                .scale(scale),
            shape = CircleShape,
            color = SuccessGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Payment Successful!",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            formatCurrency(total),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OrangePrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Your order has been confirmed",
            fontSize = 16.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = SuccessGreen.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "Redirecting...",
                color = SuccessGreen,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

private fun generateUSSDCode(bankCode: String, amount: Double): String {
    return when (bankCode) {
        "GTBank" -> "*737*2*${amount.toInt()}#"
        "First Bank" -> "*894*${amount.toInt()}#"
        "UBA" -> "*919*8*${amount.toInt()}#"
        "Zenith Bank" -> "*966*${amount.toInt()}#"
        "Access Bank" -> "*901*2*${amount.toInt()}#"
        else -> "*${(100..999).random()}#"
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// FlowRow implementation - FIXED with proper imports
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
        val hGapPx = 12.dp.roundToPx()
        val vGapPx = 12.dp.roundToPx()

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
            rowHeight = maxOf(rowHeight, placeable.height)
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

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun PaymentMethodScreenPreview() {
    BunnixTheme {
        PaymentMethodScreen(
            total = 45999.0,
            orderId = "ORD-2024-001",
            onBack = {},
            onPaymentSuccess = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnlineBankCardPreview() {
    BunnixTheme {
        OnlineBankCard(
            bank = OnlineBankOption("Opay", "", Color(0xFF00BFA5)),
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentSuccessAnimationPreview() {
    BunnixTheme {
        PaymentSuccessAnimation(total = 45999.0)
    }
}