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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val VendorOrange = Color(0xFFFF8C42)

sealed class ProfileMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Orders : ProfileMenuItem("My Orders", Icons.Default.ShoppingBag, "orders")
    object Bookings : ProfileMenuItem("My Bookings", Icons.Default.CalendarToday, "bookings")
    object Addresses : ProfileMenuItem("Saved Addresses", Icons.Default.LocationOn, "addresses")
    object Payments : ProfileMenuItem("Payment Methods", Icons.Default.Payment, "payments")
    object Notifications : ProfileMenuItem("Notifications", Icons.Default.Notifications, "notifications")
    object Help : ProfileMenuItem("Help Center", Icons.Default.Help, "help")
    object About : ProfileMenuItem("About Bunnix", Icons.Default.Info, "about")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String = "John Doe",
    userEmail: String = "john.doe@email.com",
    userPhone: String = "+234 801 234 5678",
    profileImageUrl: String? = null,
    isVendor: Boolean = false,
    vendorBusinessName: String? = null,
    // NEW PARAMETERS ADDED for MainActivity integration
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onViewOrders: () -> Unit = {},
    onViewNotifications: () -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onSwitchMode: () -> Unit = {},
    onBecomeVendor: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val menuItems = listOf(
        ProfileMenuItem.Orders,
        ProfileMenuItem.Bookings,
        ProfileMenuItem.Addresses,
        ProfileMenuItem.Payments,
        ProfileMenuItem.Notifications,
        ProfileMenuItem.Help,
        ProfileMenuItem.About
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // ADDED: Back button
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
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
                // Profile Header with Gradient
                ProfileHeader(
                    userName = userName,
                    userEmail = userEmail,
                    userPhone = userPhone,
                    profileImageUrl = profileImageUrl,
                    isVendor = isVendor,
                    vendorBusinessName = vendorBusinessName,
                    onEditProfile = onEditProfile
                )

                // Mode Switch Card (Customer/Vendor)
                ModeSwitchCard(
                    isVendor = isVendor,
                    onSwitchMode = onSwitchMode,
                    onBecomeVendor = onBecomeVendor
                )

                // Menu Items
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        menuItems.forEachIndexed { index, item ->
                            // MODIFIED: Handle Orders and Notifications with specific callbacks
                            val onItemClick = when (item.route) {
                                "orders" -> onViewOrders
                                "notifications" -> onViewNotifications
                                else -> { { onMenuItemClick(item.route) } }
                            }
                            MenuItemRow(
                                item = item,
                                onClick = onItemClick,
                                showDivider = index < menuItems.size - 1
                            )
                        }
                    }
                }

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = Color(0xFFDC2626)
                    ),
                    elevation = null
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App Version
                Text(
                    "Bunnix v1.0.0",
                    fontSize = 12.sp,
                    color = TextTertiary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    userPhone: String,
    profileImageUrl: String?,
    isVendor: Boolean,
    vendorBusinessName: String?,
    onEditProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(OrangePrimary, OrangeLight)
                    )
                )
        )

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 100.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        color = OrangeSoft,
                        shape = CircleShape,
                        modifier = Modifier.size(100.dp),
                        border = BorderStroke(4.dp, Color.White),
                        shadowElevation = 4.dp
                    ) {
                        if (profileImageUrl != null) {
                            // AsyncImage here
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    userName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = OrangePrimary
                                )
                            }
                        }
                    }

                    // Edit Button
                    Surface(
                        onClick = onEditProfile,
                        color = OrangePrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp),
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Info
                Text(
                    userName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = TextPrimary
                )

                Text(
                    userEmail,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    userPhone,
                    fontSize = 14.sp,
                    color = TextTertiary
                )

                // Vendor Badge
                if (isVendor && vendorBusinessName != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = VendorOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = VendorOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                vendorBusinessName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VendorOrange
                            )
                            Surface(
                                color = VendorOrange,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "VENDOR",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
private fun ModeSwitchCard(
    isVendor: Boolean,
    onSwitchMode: () -> Unit,
    onBecomeVendor: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVendor) VendorOrange.copy(alpha = 0.1f) else TealAccent.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            if (isVendor) VendorOrange.copy(alpha = 0.3f) else TealAccent.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = if (isVendor) VendorOrange else TealAccent,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        if (isVendor) Icons.Default.Store else Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column {
                    Text(
                        if (isVendor) "Vendor Mode" else "Customer Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isVendor) VendorOrange else TealAccent
                    )
                    Text(
                        if (isVendor) "Manage your business" else "Shop and book services",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (isVendor) {
                Switch(
                    checked = true,
                    onCheckedChange = { onSwitchMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VendorOrange
                    )
                )
            } else {
                Button(
                    onClick = onBecomeVendor,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendorOrange
                    )
                ) {
                    Text("Become a Vendor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: ProfileMenuItem,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Surface(
            onClick = onClick,
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = OrangeSoft,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary
                )
            }
        }

        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = TextTertiary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Logout,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Logout", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to logout from Bunnix?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TextTertiary)
            ) {
                Text("Cancel", color = TextPrimary)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProfileScreenPreview() {
    BunnixTheme {
        ProfileScreen(isVendor = false)
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProfileScreenVendorPreview() {
    BunnixTheme {
        ProfileScreen(
            isVendor = true,
            vendorBusinessName = "TechHub Store"
        )
    }
}