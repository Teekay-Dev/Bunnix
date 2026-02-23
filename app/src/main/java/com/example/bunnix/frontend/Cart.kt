package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay

// Colors - defined only ONCE here
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)

data class CartItem(
    val id: String,
    val name: String,
    val vendorName: String,
    val price: Double,
    val originalPrice: Double?,
    val quantity: Int,
    val imageUrl: String?,
    val variant: String?
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem> = sampleCartItems(),
    onBack: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onRemoveItem: (String) -> Unit = {},
    onUpdateQuantity: (String, Int) -> Unit = { _, _ -> }
) {
    var isVisible by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(cartItems) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val subtotal = items.sumOf { it.price * it.quantity }
    val discount = items.sumOf { (it.originalPrice ?: it.price) - it.price } * items.sumOf { it.quantity }
    val deliveryFee = if (subtotal > 50000) 0.0 else 2500.0
    val total = subtotal + deliveryFee

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Shopping Cart", fontWeight = FontWeight.Bold)
                        Text(
                            "${items.size} items",
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
                actions = {
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { items = emptyList() }) {
                            Text("Clear", color = ErrorRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                CartBottomBar(
                    total = total,
                    itemCount = items.sumOf { it.quantity },
                    onCheckout = onCheckout
                )
            }
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 4 },
            modifier = Modifier.padding(padding)
        ) {
            if (items.isEmpty()) {
                EmptyCartView(onContinueShopping)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cart Items
                    items(items, key = { it.id }) { item ->
                        AnimatedVisibility(
                            visible = items.contains(item),
                            exit = shrinkVertically() + fadeOut(),
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            CartItemCard(
                                item = item,
                                onRemove = {
                                    items = items.filter { it.id != item.id }
                                    onRemoveItem(item.id)
                                },
                                onIncreaseQty = {
                                    val newItems = items.toMutableList()
                                    val index = newItems.indexOfFirst { it.id == item.id }
                                    if (index != -1) {
                                        newItems[index] = item.copy(quantity = item.quantity + 1)
                                        items = newItems
                                        onUpdateQuantity(item.id, item.quantity + 1)
                                    }
                                },
                                onDecreaseQty = {
                                    if (item.quantity > 1) {
                                        val newItems = items.toMutableList()
                                        val index = newItems.indexOfFirst { it.id == item.id }
                                        if (index != -1) {
                                            newItems[index] = item.copy(quantity = item.quantity - 1)
                                            items = newItems
                                            onUpdateQuantity(item.id, item.quantity - 1)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // Order Summary
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        OrderSummaryCard(
                            subtotal = subtotal,
                            discount = discount,
                            deliveryFee = deliveryFee,
                            total = total
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCartView(onContinueShopping: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val scale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "scale"
        )

        Surface(
            color = OrangeSoft,
            shape = CircleShape,
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.padding(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Your cart is empty",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Looks like you haven't added anything to your cart yet",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Icon(Icons.Default.ShoppingBag, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Shopping", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    color = ErrorRed,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Product Image
                    Surface(
                        color = OrangeSoft,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Product Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 2
                        )

                        Text(
                            item.vendorName,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (item.variant != null) {
                            Surface(
                                color = SurfaceLight,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                Text(
                                    item.variant,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                formatCurrency(item.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = OrangePrimary
                            )

                            if (item.originalPrice != null) {
                                Text(
                                    formatCurrency(item.originalPrice),
                                    fontSize = 12.sp,
                                    color = TextTertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    // Quantity Controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onIncreaseQty,
                            modifier = Modifier
                                .size(32.dp)
                                .background(OrangePrimary, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            item.quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.width(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        IconButton(
                            onClick = onDecreaseQty,
                            modifier = Modifier
                                .size(32.dp)
                                .background(SurfaceLight, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    discount: Double,
    deliveryFee: Double,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Order Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SummaryRow("Subtotal", subtotal)

            if (discount > 0) {
                SummaryRow("Discount", -discount, isDiscount = true)
            }

            SummaryRow("Delivery Fee", deliveryFee, isFree = deliveryFee == 0.0)

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextTertiary.copy(alpha = 0.2f))

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

            if (deliveryFee == 0.0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SuccessGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "You got FREE delivery!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false,
    isFree: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            if (isFree) "FREE" else formatCurrency(amount),
            fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = when {
                isDiscount -> SuccessGreen
                isFree -> SuccessGreen
                else -> TextPrimary
            }
        )
    }
}

@Composable
private fun CartBottomBar(
    total: Double,
    itemCount: Int,
    onCheckout: () -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total ($itemCount items)",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        formatCurrency(total),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = OrangePrimary
                    )
                }
            }

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "Proceed to Checkout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

private fun sampleCartItems(): List<CartItem> = listOf(
    CartItem(
        id = "1",
        name = "iPhone 15 Pro Max Silicone Case",
        vendorName = "TechHub Store",
        price = 15000.0,
        originalPrice = 20000.0,
        quantity = 1,
        imageUrl = null,
        variant = "Midnight Black"
    ),
    CartItem(
        id = "2",
        name = "Anker 737 Power Bank 24000mAh",
        vendorName = "Gadget World",
        price = 85000.0,
        originalPrice = null,
        quantity = 1,
        imageUrl = null,
        variant = null
    ),
    CartItem(
        id = "3",
        name = "AirPods Pro 2nd Generation",
        vendorName = "Apple Reseller NG",
        price = 180000.0,
        originalPrice = 220000.0,
        quantity = 2,
        imageUrl = null,
        variant = "USB-C"
    )
)

private fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun CartScreenPreview() {
    BunnixTheme {
        CartScreen()
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun EmptyCartScreenPreview() {
    BunnixTheme {
        CartScreen(cartItems = emptyList())
    }
}