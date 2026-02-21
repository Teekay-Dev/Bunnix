package com.example.bunnix.vendorUI.screens.vendor.dashboard

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.vendorUI.components.EmptyOrders
import com.example.bunnix.vendorUI.components.ErrorState
import com.example.bunnix.vendorUI.components.ShimmerCard
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.components.StatCard
import com.example.bunnix.viewmodel.VendorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    onNavigateToOrders: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToAddService: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: VendorDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // Orange Header with Gradient
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OrangePrimary,
                                OrangePrimary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    // Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Business",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Analytics Icon with haptic
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToAnalytics()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Analytics",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Balance Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Available Balance",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                AnimatedCounter(
                                    targetNumber = uiState.balance,
                                    prefix = "$"
                                )
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    /* Withdraw */
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.25f)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Withdraw", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Content based on state - FIXED when conditions
        when {
            uiState.isLoading -> {
                // Shimmer loading
                DashboardShimmer()
            }
            uiState.error != null -> {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.refreshData() }
                )
            }
            else -> {
                // Stats Grid with animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Total Sales",
                                value = "$${uiState.stats.totalSales}",
                                icon = Icons.Default.AttachMoney,
                                iconBgColor = SuccessGreen.copy(alpha = 0.1f),
                                iconColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Total Orders",
                                value = uiState.stats.totalOrders.toString(),
                                icon = Icons.Default.ShoppingBag,
                                iconBgColor = InfoBlue.copy(alpha = 0.1f),
                                iconColor = InfoBlue,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Bookings",
                                value = uiState.stats.totalBookings.toString(),
                                icon = Icons.Default.CalendarToday,
                                iconBgColor = ActionPurple.copy(alpha = 0.1f),
                                iconColor = ActionPurple,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Customers",
                                value = uiState.stats.totalCustomers.toString(),
                                icon = Icons.Default.People,
                                iconBgColor = WarningYellow.copy(alpha = 0.1f),
                                iconColor = WarningYellow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Quick Actions
                QuickActionsSection(
                    onNavigateToOrders = onNavigateToOrders,
                    onNavigateToAddProduct = onNavigateToAddProduct,
                    onNavigateToAddService = onNavigateToAddService,
                    onNavigateToMessages = onNavigateToMessages
                )

                // Recent Orders - no isEmpty check
                RecentOrdersSection(
                    orders = uiState.recentOrders,
                    onViewAll = onNavigateToOrders
                )

                // Performance
                PerformanceSection(data = uiState.weeklyPerformance)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AnimatedCounter(targetNumber: Double, prefix: String = "") {
    val animatedValue = animateFloatAsState(
        targetValue = targetNumber.toFloat(),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "counter"
    )

    Text(
        text = "$prefix${String.format("%,.2f", animatedValue.value)}",
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DashboardShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerCard(modifier = Modifier.weight(1f))
            ShimmerCard(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerCard(modifier = Modifier.weight(1f))
            ShimmerCard(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToOrders: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToAddService: () -> Unit,
    onNavigateToMessages: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add Product",
                    color = ActionBlue,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToAddProduct()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.ShoppingCart,
                    label = "View Orders",
                    color = ActionGreen,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToOrders()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.CalendarToday,
                    label = "Bookings",
                    color = ActionPurple,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToAddService()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.Message,
                    label = "Messages",
                    color = ActionOrange,
                    badge = 1,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToMessages()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: Int = 0
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(80.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    if (badge > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-8).dp),
                            containerColor = Color.White,
                            contentColor = color
                        ) {
                            Text(badge.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Placeholder composables
@Composable
fun RecentOrdersSection(
    orders: Any,
    onViewAll: () -> Unit
) {
    Text("Recent Orders Section", modifier = Modifier.padding(16.dp))
}

@Composable
fun PerformanceSection(data: Any) {
    Text("Performance Section", modifier = Modifier.padding(16.dp))
}