package com.example.bunnix.vendorUI.screens.vendor.profile

import android.app.Activity
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.navigation.VendorRoutes
import com.example.bunnix.viewmodel.ProfileViewModel  // ✅ CORRECT IMPORT

@Composable
fun VendorProfileScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit = {},  // ✅ ADD THIS PARAMETER
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.vendorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadVendorProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(LightGrayBg)
    ) {
        // Orange Header with Profile Info
        ProfileHeader(
            profile = profile,
            isLoading = isLoading,
            onEditPhotoClick = { /* TODO: Image picker */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Vendor Mode Toggle Card
        VendorModeCard(
            onSwitchToCustomer = {
                // TODO: Navigate to customer MainActivity
                // You can implement this later
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Account Settings Section
        SettingsSection(
            title = "Account Settings",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    onClick = { navController.navigate(VendorRoutes.EDIT_BUSINESS) }
                ),
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Addresses",
                    subtitle = "Manage your saved addresses",
                    onClick = { /* TODO: Add Addresses screen */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Payment,
                    title = "Payment Methods",
                    subtitle = "Bank details for withdrawals",
                    onClick = { navController.navigate(VendorRoutes.PAYMENT_SETTINGS) }
                ),
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage notification preferences",
                    onClick = { navController.navigate(VendorRoutes.NOTIFICATIONS) }
                )
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SettingsSection(
            title = "Support",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help Center",
                    subtitle = "Get help with your account",
                    onClick = { /* TODO: Add Help Center */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy & Security",
                    subtitle = "Manage your privacy settings",
                    onClick = { /* TODO: Add Privacy screen */ }
                )
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.05f)
                )
                .clickable { showLogoutDialog = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Version
        Text(
            text = "Bunnix v1.0.0",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    // ✅ LOGOUT DIALOG - NOW NAVIGATES TO LOGIN
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFF44336)
                )
            },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()  // ✅ Logs out from Firebase
                        showLogoutDialog = false

                        // ✅ CLOSE VENDORMAINACTIVITY AND GO TO LOG IN
                        (context as? Activity)?.finish()
                        onNavigateToLogin()  // ✅ Opens LoginActivity
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

// ============= PROFILE HEADER =============
@Composable
fun ProfileHeader(
    profile: VendorProfileData?,
    isLoading: Boolean,
    onEditPhotoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF8C42),
                        Color(0xFFFF6B35)
                    )
                )
            )
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo
            Box {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (profile?.imageUrl?.isNotBlank() == true) {
                        AsyncImage(
                            model = profile.imageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                // Edit button
                IconButton(
                    onClick = onEditPhotoClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(OrangePrimaryModern, CircleShape)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = profile?.name ?: "Loading...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = profile?.email ?: "",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ============= VENDOR MODE CARD =============
@Composable
fun VendorModeCard(
    onSwitchToCustomer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimaryModern),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Vendor Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage your business",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            IconButton(
                onClick = onSwitchToCustomer,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Mode",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ============= SETTINGS SECTION =============
@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.05f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, item ->
                    SettingsItemRow(item = item)

                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(
    item: SettingsItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = OrangePrimaryModern,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (item.subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ============= DATA CLASSES =============
data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val onClick: () -> Unit
)

data class VendorProfileData(
    val name: String,
    val email: String,
    val imageUrl: String,
    val businessName: String,
    val phone: String
)