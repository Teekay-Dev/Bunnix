package com.example.bunnix.vendorUI.screens.vendor.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bunnix.OrangeLight
import com.example.bunnix.OrangeSoft
import com.example.bunnix.frontend.LoginActivity
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.navigation.VendorRoutes
import com.example.bunnix.viewmodel.VendorProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorProfileScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit = {},
    viewModel: VendorProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.vendorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }
    var hasCustomerAccount by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfilePhoto(it)
        }
    }

    // ⭐ Check if user has a customer account
    LaunchedEffect(Unit) {
        viewModel.loadVendorProfile()

        // Check if customer account exists
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            try {
                val userDoc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

                // User has customer account if document exists
                hasCustomerAccount = userDoc.exists()
            } catch (e: Exception) {
                hasCustomerAccount = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(LightGrayBg)
        ) {
            // Orange Header with Profile Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Orange gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    OrangePrimaryModern,
                                    OrangeLight
                                )
                            )
                        )
                )

                // Profile content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Profile photo with edit button
                    Box {
                        AsyncImage(
                            model = profile?.imageUrl?.ifEmpty { "https://via.placeholder.com/150" }
                                ?: "https://via.placeholder.com/150",
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )

                        // Camera edit button
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .background(OrangePrimaryModern, CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Edit Photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Name and Email
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else (profile?.businessName ?: "Vendor Name"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Blue verified badge
                    if (profile?.isVerified == true) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = profile?.email ?: "",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ⭐ SWITCH OR CREATE CUSTOMER ACCOUNT
            if (hasCustomerAccount) {
                // User HAS customer account → Show SWITCH button → Go to LOGIN
                SwitchToCustomerCard(
                    onSwitchClick = {
                        // Logout and navigate to login screen
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.putExtra("mode", "CUSTOMER")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                )
            } else {
                // User DOES NOT have customer account → Show CREATE button → Go to SIGNUP
                CreateCustomerAccountCard(
                    onCreateClick = {
                        // Navigate to customer signup in MainActivity
                        val intent = Intent(context, com.example.bunnix.MainActivity::class.java)
                        intent.putExtra("navigate_to", "signup")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account Settings
            SettingsSection(
                title = "Account Settings",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        subtitle = "Update your business information",
                        onClick = { navController.navigate(VendorRoutes.EDIT_BUSINESS) }
                    ),
                    SettingsItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "Get Verified",
                        subtitle = "Verify your business and get the blue badge",
                        onClick = { navController.navigate(VendorRoutes.GET_VERIFIED) },
                        showBadge = profile?.isVerified != true
                    ),
                    SettingsItem(
                        icon = Icons.Default.Payment,
                        title = "Payment Methods",
                        subtitle = "Bank details for receiving payments",
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

            // Support
            SettingsSection(
                title = "Support",
                items = listOf(
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Help Center",
                        subtitle = "Get help with your account",
                        onClick = { /* TODO */ }
                    ),
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Privacy & Security",
                        subtitle = "Manage your privacy settings",
                        onClick = { /* TODO */ }
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
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bunnix v1.0.0",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                textAlign = TextAlign.Center
            )
        }

        // Upload progress indicator
        if (uploadProgress > 0f && uploadProgress < 1f) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier.size(60.dp),
                        color = OrangePrimaryModern,
                        strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                        trackColor = ProgressIndicatorDefaults.circularTrackColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Uploading... ${(uploadProgress * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = ErrorRed
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
                        viewModel.logout()
                        showLogoutDialog = false

                        // Navigate to LoginActivity
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
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

// ⭐ Switch to Customer Mode Card (LOGIN)
@Composable
fun SwitchToCustomerCard(onSwitchClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = OrangePrimaryModern.copy(alpha = 0.3f)
            )
            .clickable(onClick = onSwitchClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = OrangePrimaryModern // ⭐ ORANGE like customer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
//                Box(
//                    modifier = Modifier
//                        .size(56.dp)
//                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ShoppingCart,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }

                Column {
                    Text(
                        text = "Switch to Customer Mode",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Login to your customer account",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Icon(
                Icons.Default.Sync,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ⭐ Create Customer Account Card (ORANGE - matches app theme)
@Composable
fun CreateCustomerAccountCard(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = OrangePrimaryModern.copy(alpha = 0.3f)
            )
            .clickable(onClick = onCreateClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = OrangePrimaryModern // ⭐ ORANGE like customer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
//                Box(
//                    modifier = Modifier
//                        .size(56.dp)
//                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.PersonAdd,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }

                Column {
                    Text(
                        text = "Create Customer Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Sign up to shop as a customer",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

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
            fontSize = 18.sp,
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
                    SettingsItemRow(item)

                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = DividerDefaults.Thickness,
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        OrangePrimaryModern.copy(alpha = 0.1f),
                        CircleShape
                    ),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    if (item.showBadge) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF2196F3).copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "NEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                }

                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
    val showBadge: Boolean = false
)

// ===== PREVIEWS =====

@Preview(showBackground = true, showSystemUi = true, name = "Vendor Profile - With Customer Account")
@Composable
fun VendorProfileWithCustomerPreview() {
    BunnixTheme {
        Scaffold(
            containerColor = LightGrayBg
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(LightGrayBg)
            ) {
                // Header with Profile Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        OrangePrimaryModern,
                                        OrangeLight
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        Box {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color.White, CircleShape)
                                    .background(OrangeSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    tint = OrangePrimaryModern,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp)
                                    .background(OrangePrimaryModern, CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Name and Email
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tech Store Pro",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "techstore@example.com",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Switch to Customer Card
                SwitchToCustomerCard(onSwitchClick = {})

                Spacer(modifier = Modifier.height(24.dp))

                // Account Settings
                SettingsSection(
                    title = "Account Settings",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.Default.Edit,
                            title = "Edit Profile",
                            subtitle = "Update your business information",
                            onClick = {}
                        ),
                        SettingsItem(
                            icon = Icons.Default.VerifiedUser,
                            title = "Get Verified",
                            subtitle = "Verify your business and get the blue badge",
                            onClick = {},
                            showBadge = false
                        ),
                        SettingsItem(
                            icon = Icons.Default.Payment,
                            title = "Payment Methods",
                            subtitle = "Bank details for receiving payments",
                            onClick = {}
                        )
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Support Section
                SettingsSection(
                    title = "Support",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = "Help Center",
                            subtitle = "Get help with your account",
                            onClick = {}
                        ),
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = "Privacy & Security",
                            subtitle = "Manage your privacy settings",
                            onClick = {}
                        )
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Vendor Profile - No Customer Account")
@Composable
fun VendorProfileNoCustomerPreview() {
    BunnixTheme {
        Scaffold(
            containerColor = LightGrayBg
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(LightGrayBg)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(OrangePrimaryModern, OrangeLight)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .background(OrangeSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = OrangePrimaryModern,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                // Name
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fashion Boutique",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "fashion@example.com",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Create Customer Account Card
                CreateCustomerAccountCard(onCreateClick = {})

                Spacer(modifier = Modifier.height(24.dp))

                // Settings
                SettingsSection(
                    title = "Account Settings",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.Default.Edit,
                            title = "Edit Profile",
                            subtitle = "Update your business information",
                            onClick = {}
                        ),
                        SettingsItem(
                            icon = Icons.Default.VerifiedUser,
                            title = "Get Verified",
                            subtitle = "Verify your business and get the blue badge",
                            onClick = {},
                            showBadge = true
                        )
                    )
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Switch Card", widthDp = 400)
@Composable
fun SwitchCardPreview() {
    BunnixTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGrayBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SwitchToCustomerCard(onSwitchClick = {})
            CreateCustomerAccountCard(onCreateClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Settings Section", widthDp = 400)
@Composable
fun SettingsSectionPreview() {
    BunnixTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGrayBg)
                .padding(16.dp)
        ) {
            SettingsSection(
                title = "Account Settings",
                items = listOf(
                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        subtitle = "Update your business information",
                        onClick = {}
                    ),
                    SettingsItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "Get Verified",
                        subtitle = "Verify your business and get the blue badge",
                        onClick = {},
                        showBadge = true
                    ),
                    SettingsItem(
                        icon = Icons.Default.Payment,
                        title = "Payment Methods",
                        subtitle = "Bank details for receiving payments",
                        onClick = {}
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Header", widthDp = 400, heightDp = 300)
@Composable
fun ProfileHeaderPreview() {
    BunnixTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(OrangePrimaryModern, OrangeLight)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .background(OrangeSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "TS",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryModern
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .background(OrangePrimaryModern, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}