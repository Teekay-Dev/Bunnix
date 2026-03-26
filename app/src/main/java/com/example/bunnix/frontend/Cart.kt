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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.models.CartItem
import com.example.bunnix.database.firebase.collections.CartCollection
import com.example.bunnix.database.firebase.FirebaseManager
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage // ✅ IMPORT COIL

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    onBack: () -> Unit = {},
    // ✅ CHANGE: Accept total as a parameter to pass to MainActivity
    onCheckout: (Double) -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onStartShopping: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val userId = FirebaseManager.getCurrentUserId()
    val scope = rememberCoroutineScope()

    val cartItems by remember(userId) {
        if (userId != null) {
            CartCollection.getCartItems(userId)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    // Calculate totals dynamically
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val discount = cartItems.sumOf { (it.originalPrice ?: it.price) - it.price } * cartItems.sumOf { it.quantity }

    // ✅ FIX 2: Remove delivery fee
    val deliveryFee = 0.0

    val total = subtotal - discount + deliveryFee

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Shopping Cart", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "${cartItems.sumOf { it.quantity }} items",
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
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = {
                            if (userId != null) {
                                scope.launch { CartCollection.clearCart(userId) }
                            }
                        }) {
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
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    total = total,
                    itemCount = cartItems.sumOf { it.quantity },
                    // ✅ PASS total to callback
                    onCheckout = { onCheckout(total) }
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
            if (cartItems.isEmpty()) {
                EmptyCartView(onStartShopping)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cart Items
                    items(cartItems, key = { it.id }) { item ->
                        AnimatedVisibility(
                            visible = true,
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
                                    if (userId != null) {
                                        scope.launch { CartCollection.removeFromCart(userId, item.productId) }
                                    }
                                },
                                onIncreaseQty = {
                                    if (userId != null) {
                                        scope.launch { CartCollection.updateQuantity(userId, item.productId, item.quantity + 1) }
                                    }
                                },
                                onDecreaseQty = {
                                    if (item.quantity > 1 && userId != null) {
                                        scope.launch { CartCollection.updateQuantity(userId, item.productId, item.quantity - 1) }
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
            textAlign = TextAlign.Center,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Looks like you haven't added anything to your cart yet",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
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
            Text(
                "Start Shopping",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
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
                    // ✅ FIX 3: Show Product Image
                    Surface(
                        color = OrangeSoft,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(90.dp)
                    ) {
                        if (item.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Default.Inventory,
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
                            item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
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
                            color = TextPrimary,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
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
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SummaryRow("Subtotal", subtotal)

            if (discount > 0) {
                SummaryRow("Discount", -discount, isDiscount = true)
            }

            // ✅ Only show delivery fee if it's > 0
            if (deliveryFee > 0) {
                SummaryRow("Delivery Fee", deliveryFee, isFree = false)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextTertiary.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    formatCurrency(total),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = OrangePrimary
                )
            }

            // Removed the "Free Delivery" banner logic since fee is always 0
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
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    BunnixTheme {
        CartScreen(onStartShopping = {})
    }
}