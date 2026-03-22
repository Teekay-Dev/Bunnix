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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay

// Exact colors from the image
private val OrangePrimary = Color(0xFFFF8C42)
private val OrangeLight = Color(0xFFFFA726)
private val OrangeGradientStart = Color(0xFFFF8C42)
private val OrangeGradientEnd = Color(0xFFFFB74D)
private val BackgroundGray = Color(0xFFF5F5F5)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val White = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String = "John Doe",
    userEmail: String = "john@example.com",
    userPhone: String = "+234 801 234 5678",
    profileImageUrl: String? = null,
    isVendor: Boolean = false,
    vendorBusinessName: String? = null,
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onViewOrders: () -> Unit = {},
    onViewNotifications: () -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onSwitchMode: () -> Unit = {},
    onBecomeVendor: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isPreview = LocalInspectionMode.current
    var isVisible by remember { mutableStateOf(isPreview) }

    LaunchedEffect(Unit) {
        if (!isPreview) {
            delay(100)
            isVisible = true
        }
    }

    Scaffold(
        containerColor = BackgroundGray
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
                // Orange Gradient Header with Wave
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(OrangeGradientStart, OrangeGradientEnd)
                            )
                        )
                ) {
                    // Wave decoration at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                color = BackgroundGray,
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            )
                    )

                    // Profile content in header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Circular Profile Image
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .border(4.dp, White.copy(alpha = 0.3f), CircleShape),
                            shape = CircleShape,
                            color = White
                        ) {
                            if (profileImageUrl != null) {
                                // AsyncImage would go here
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        userName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 36.sp,
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(1.dp))

                        // User Name
                        Text(
                            userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = White
                        )

                        // Email
                        Text(
                            userEmail,
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(-20.dp))

                // Vendor Mode Card
                // Vendor Mode Card - Always show switch/become vendor option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OrangePrimary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                if (isVendor) "Switch to Business Mode" else "Become a Vendor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = White
                            )
                            Text(
                                if (isVendor) "Manage your business account" else "Create a business account",
                                fontSize = 14.sp,
                                color = White.copy(alpha = 0.9f)
                            )
                        }

                        // Switch/Arrow button
                        Surface(
                            onClick = {
                                if (isVendor) {
                                    onSwitchMode()
                                } else {
                                    onBecomeVendor()
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = White.copy(alpha = 0.2f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    if (isVendor) Icons.Default.Sync else Icons.Default.ArrowForward,
                                    contentDescription = if (isVendor) "Switch Mode" else "Become Vendor",
                                    tint = White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Account Settings Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Account Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Edit Profile
                        MenuItem(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            onClick = onEditProfile
                        )

                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                        // Addresses
                        MenuItem(
                            icon = Icons.Default.LocationOn,
                            title = "Addresses",
                            onClick = { onMenuItemClick("addresses") }
                        )

                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                        // Payment Methods
                        MenuItem(
                            icon = Icons.Default.CreditCard,
                            title = "Payment Methods",
                            onClick = { onMenuItemClick("payments") }
                        )

                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                        // Notifications
                        MenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            onClick = onViewNotifications
                        )
                    }
                }

                // Support Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Support",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Help Center
                        MenuItem(
                            icon = Icons.Default.Help,
                            title = "Help Center",
                            iconBackground = OrangePrimary.copy(alpha = 0.1f),
                            iconTint = OrangePrimary,
                            onClick = { onMenuItemClick("help") }
                        )

                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                        // Privacy & Security
                        MenuItem(
                            icon = Icons.Default.Security,
                            title = "Privacy & Security",
                            iconBackground = OrangePrimary.copy(alpha = 0.1f),
                            iconTint = OrangePrimary,
                            onClick = { onMenuItemClick("privacy") }
                        )
                    }
                }

                // Logout Button
                Surface(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Log Out",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
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
private fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconBackground: Color = Color(0xFFF5F5F5),
    iconTint: Color = TextSecondary
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = iconBackground,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
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
        title = { Text("Logout", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = { Text("Are you sure you want to logout from Bunnix?", color = TextPrimary) },
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
                border = BorderStroke(1.dp, TextSecondary)
            ) {
                Text("Cancel", color = TextPrimary)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = White
    )
}


@Preview(
    name = "Profile Screen - Customer",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
fun ProfileScreenCustomerPreview() {
    BunnixTheme {
        ProfileScreen(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            userPhone = "+234 801 234 5678",
            profileImageUrl = null,
            isVendor = false,
            vendorBusinessName = null,
            onBack = {},
            onEditProfile = {},
            onViewOrders = {},
            onViewNotifications = {},
            onMenuItemClick = {},
            onSwitchMode = {},
            onBecomeVendor = {},
            onLogout = {}
        )
    }
}

@Preview(
    name = "Profile Screen - Vendor",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
fun ProfileScreenVendorPreview() {
    BunnixTheme {
        ProfileScreen(
            userName = "Sarah Johnson",
            userEmail = "sarah@techstore.com",
            userPhone = "+234 901 234 5678",
            profileImageUrl = null,
            isVendor = true,
            vendorBusinessName = "TechHub Store",
            onBack = {},
            onEditProfile = {},
            onViewOrders = {},
            onViewNotifications = {},
            onMenuItemClick = {},
            onSwitchMode = {},
            onBecomeVendor = {},
            onLogout = {}
        )
    }
}

@Preview(
    name = "Logout Dialog",
    showBackground = true
)
@Composable
fun LogoutDialogPreview() {
    BunnixTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            LogoutConfirmationDialog(
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(
    name = "Menu Item",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun MenuItemPreview() {
    BunnixTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            MenuItem(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            MenuItem(
                icon = Icons.Default.Help,
                title = "Help Center",
                iconBackground = Color(0xFFFF8C42).copy(alpha = 0.1f),
                iconTint = Color(0xFFFF8C42),
                onClick = {}
            )
        }
    }
}