package com.example.bunnix.frontend

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay

// Colors (matches app theme)
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeGradientStart = Color(0xFFFF6B35)
private val OrangeGradientEnd = Color(0xFFFFB74D)
private val BackgroundGray = Color(0xFF090101) // Old dark color
private val SurfaceLight = Color(0xFFFAFAFA) // ✅ NEW: Light background
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val White = Color.White



@Composable
fun ProfileScreen(
    userName: String = "",
    userEmail: String = "",
    userPhone: String = "",
    customPhotoUrl: String? = null,
    googlePhotoUrl: String? = null,
    uploadProgress: Float = 0f,
    isVendor: Boolean = false,
    vendorBusinessName: String? = null,
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onViewOrders: () -> Unit = {},
    onViewNotifications: () -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onSwitchMode: () -> Unit = {},
    onBecomeVendor: () -> Unit = {},
    onLogout: () -> Unit = {},
    onPhotoSelected: (Uri) -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showProfileNotificationDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val isPreview = LocalInspectionMode.current
    var isVisible by remember { mutableStateOf(isPreview) }
    val context = LocalContext.current


//    var address by remember {
//        mutableStateOf("23 Ada George Road, Port Harcourt")
//    }
//
//    var showEditAddressDialog by remember {
//        mutableStateOf(false)
//    }
//
//    var editedAddress by remember {
//        mutableStateOf(address)
//    }


    LaunchedEffect(Unit) {
        if (!isPreview) {
            delay(100)
            isVisible = true
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoSelected(it) }
    }

    val displayPhotoUrl = when {
        !customPhotoUrl.isNullOrBlank() -> customPhotoUrl
        !googlePhotoUrl.isNullOrBlank() -> googlePhotoUrl
        else -> null
    }

    val displayInitial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ FIX 1: Changed containerColor to SurfaceLight (White)
        Scaffold(containerColor = SurfaceLight) { padding ->
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
                    // ── Orange Gradient Header ──
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(OrangeGradientStart, OrangeGradientEnd)
                                    )
                                )
                        )

                        // Wave at bottom of header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    color = SurfaceLight, // ✅ FIX 2: Changed to SurfaceLight to match body
                                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                                )
                        )

                        // Profile content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .padding(top = 5.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Photo
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Surface(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(4.dp, White.copy(alpha = 0.6f), CircleShape),
                                    shape = CircleShape,
                                    color = White
                                ) {
                                    if (displayPhotoUrl != null) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(displayPhotoUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Profile Photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = displayInitial,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 36.sp,
                                                color = OrangePrimary
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(OrangePrimary, CircleShape)
                                        .border(2.dp, White, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change Photo",
                                        tint = White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name
                            Text(
                                text = userName.ifBlank { "Loading..." },
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = White
                            )

                            Spacer(modifier = Modifier.height(0.dp))

                            // Email
                            Text(
                                text = userEmail,
                                fontSize = 14.sp,
                                color = White // ✅ FIX 3: Changed from White to Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    // ── Vendor / Switch Card ──
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = OrangePrimary),
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
                                    text = if (isVendor) "Switch to Business Mode" else "Become a Vendor",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = White
                                )
                                Text(
                                    text = if (isVendor) "Manage your business account" else "Start selling products & services",
                                    fontSize = 14.sp,
                                    color = White.copy(alpha = 0.9f)
                                )
                            }

                            Surface(
                                onClick = { if (isVendor) onSwitchMode() else onBecomeVendor() },
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = White.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = if (isVendor) Icons.Default.Sync else Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── Account Settings ──
                    ProfileSection(
                        title = "Account Settings",
                        items = listOf(
                            ProfileMenuItem(
                                icon = Icons.Default.Person,
                                title = "Edit Profile",
                                onClick = { showEditProfileDialog = true }
                            ),
                            ProfileMenuItem(
                                icon = Icons.Default.LocationOn,
                                title = "Addresses",
                                onClick = { showAddressDialog = true }
                            ),
                            ProfileMenuItem(
                                icon = Icons.Default.CreditCard,
                                title = "Payment Methods",
                                onClick = { showPaymentDialog = true }
                            ),
                            ProfileMenuItem(
                                icon = Icons.Default.Notifications,
                                title = "Notifications",
                                onClick = { showProfileNotificationDialog = true }
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Support ──
                    ProfileSection(
                        title = "Support",
                        items = listOf(
                            ProfileMenuItem(
                                icon = Icons.Default.Help,
                                title = "Help Center",
                                onClick = { showHelpDialog = true }
                            ),
                            ProfileMenuItem(
                                icon = Icons.Default.Security,
                                title = "Privacy & Security",
                                onClick = { showPrivacyDialog = true }
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Logout ──
                    Surface(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // ── Upload Progress Overlay ──
        if (uploadProgress > 0f && uploadProgress < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.size(64.dp),
                            color = OrangePrimary,
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Uploading... ${(uploadProgress * 100).toInt()}%",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // ── Logout Dialog ──
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
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
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = White
        )
    }

   //  Edit Profile Dialog
    if (showEditProfileDialog) {

        var editedName by remember { mutableStateOf(userName) }
        var editedPhone by remember { mutableStateOf(userPhone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },

            containerColor = White,

            title = {
                Text(
                    "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },

                        label = {
                            Text(
                                "Full Name",
                                color = TextSecondary
                            )
                        },

                        textStyle = LocalTextStyle.current.copy(
                            color = TextPrimary
                        ),

                        modifier = Modifier.fillMaxWidth(),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = TextSecondary,
                            cursorColor = OrangePrimary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },

                        label = {
                            Text(
                                "Phone Number",
                                color = TextSecondary
                            )
                        },

                        textStyle = LocalTextStyle.current.copy(
                            color = TextPrimary
                        ),

                        modifier = Modifier.fillMaxWidth(),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = TextSecondary,
                            cursorColor = OrangePrimary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        // SAVE TO DATABASE HERE
                        showEditProfileDialog = false
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    )
                ) {
                    Text(
                        "Save",
                        color = White
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showEditProfileDialog = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = TextPrimary
                    )
                }
            }
        )
    }

    // Addresses Dialog
    var showEditAddressDialog by remember {
        mutableStateOf(false)
    }

    var address1 by remember {
        mutableStateOf("23 Ada George Road, Port Harcourt")
    }

    var address2 by remember {
        mutableStateOf("12 Peter Odili Road, Port Harcourt")
    }

    var currentEditingAddress by remember {
        mutableStateOf("")
    }

    var editingAddressType by remember {
        mutableStateOf("")
    }
    if (showAddressDialog) {



        var showAddAddressField by remember {
            mutableStateOf(false)
        }

        var newAddress by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Text(
                    "Saved Addresses",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Address 1
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {

                            Text(
                                text = "Home",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = address1,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = {

                                    editingAddressType = "home"

                                    currentEditingAddress = address1

                                    showEditAddressDialog = true
                                }
                            ) {
                                Text(
                                    "Edit Address",
                                    color = OrangePrimary
                                )
                            }
                        }
                    }

                    // Address 2
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {

                            Text(
                                text = "Office",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = address2,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = {

                                    editingAddressType = "office"

                                    currentEditingAddress = address2

                                    showEditAddressDialog = true
                                }
                            ) {
                                Text(
                                    "Edit Address",
                                    color = OrangePrimary
                                )
                            }
                        }
                    }

                    // Add Address Field
                    if (showAddAddressField) {

                        OutlinedTextField(
                            value = newAddress,
                            onValueChange = {
                                newAddress = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("New Address")
                            }
                        )

                        Button(
                            onClick = {

                                if (newAddress.isNotBlank()) {
                                    address2 = newAddress
                                    newAddress = ""
                                    showAddAddressField = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Address")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            showAddAddressField = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Add New Address",
                            color = OrangePrimary
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAddressDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = OrangePrimary
                    )
                }
            },
            containerColor = SurfaceLight
        )
    }

    // Edit Address Dialog
    if (showEditAddressDialog) {

        AlertDialog(
            onDismissRequest = {
                showEditAddressDialog = false
            },

            containerColor = White,

            title = {
                Text(
                    text = "Edit Address",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },

            text = {

                Column {

                    Text(
                        text = "Update your ${editingAddressType.replaceFirstChar { it.uppercase() }} address",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = currentEditingAddress,

                        onValueChange = {
                            currentEditingAddress = it
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Address")
                        },

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = TextSecondary,
                            cursorColor = OrangePrimary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (editingAddressType == "home") {
                            address1 = currentEditingAddress
                        } else {
                            address2 = currentEditingAddress
                        }

                        showEditAddressDialog = false
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    )
                ) {

                    Text(
                        text = "Save",
                        color = White
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showEditAddressDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel",
                        color = TextPrimary
                    )
                }
            }
        )
    }


    // Payment Method Dialog
    if (showPaymentDialog) {

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },

            containerColor = White,

            title = {
                Text(
                    "Payment Methods",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },

            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    PaymentMethodRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Pay On Delivery",
                        subtitle = "Available"
                    )

                    PaymentMethodRow(
                        icon = Icons.Default.CreditCard,
                        title = "Debit / Credit Card",
                        subtitle = "Visa • Mastercard"
                    )

                    PaymentMethodRow(
                        icon = Icons.Default.Payments,
                        title = "Bank Transfer",
                        subtitle = "Pay with bank account"
                    )

                    PaymentMethodRow(
                        icon = Icons.Default.PhoneAndroid,
                        title = "Paystack",
                        subtitle = "Secure online payment"
                    )
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showPaymentDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = OrangePrimary
                    )
                }
            }
        )
    }

    // Notification Dialog
    if (showProfileNotificationDialog) {

        AlertDialog(
            onDismissRequest = { showProfileNotificationDialog = false },

            containerColor = White,

            title = {
                Text(
                    "Notifications",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },

            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileNotificationItem(
                        "🎉 Welcome to Bunnix!",
                        color = TextPrimary
                    )

                    ProfileNotificationItem(
                        "📢 Tell your friends about Bunnix",
                        color = TextPrimary
                    )

                    ProfileNotificationItem(
                        "⭐ Rate the app and share feedback",
                        color = TextPrimary
                    )

                    ProfileNotificationItem(
                        "👋 You haven't been active recently",
                        color = TextPrimary
                    )

                    ProfileNotificationItem(
                        "✅ Your account has been verified",
                        color = TextPrimary
                    )
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showProfileNotificationDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = OrangePrimary
                    )
                }
            }
        )
    }

    // Help Center Dialog
    if (showHelpDialog) {

        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    "Help Center",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileNotificationItem(
                        "📦 Track orders and deliveries",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "🛒 Get help with purchases and refunds",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "💬 Contact Bunnix Support anytime",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "🚚 Delivery issues and complaints",
                        TextPrimary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showHelpDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = OrangePrimary
                    )
                }
            },
            containerColor = SurfaceLight
        )
    }

    // Privacy & Security Dialog
    if (showPrivacyDialog) {

        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    "Privacy & Security",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileNotificationItem(
                        "🔒 Your account is securely protected",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "🛡️ All payments are encrypted",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "📱 Enable 2-factor authentication soon",
                        TextPrimary
                    )

                    ProfileNotificationItem(
                        "👁️ Manage app permissions and privacy",
                        TextPrimary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPrivacyDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = OrangePrimary
                    )
                }
            },
            containerColor = SurfaceLight
        )
    }

}

// ── Reusable Section Card ──
@Composable
private fun ProfileSection(title: String, items: List<ProfileMenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                items.forEachIndexed { index, item ->
                    ProfileMenuRow(item)
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFEEEEEE),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(item: ProfileMenuItem) {
    Surface(
        onClick = item.onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = OrangePrimary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
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
fun PaymentMethodRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                color = OrangePrimary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}


@Composable
fun ProfileNotificationItem(
    text: String,
    color: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun ProfileScreenPreview() {
    BunnixTheme {
        ProfileScreen(
            userName = "John Doe",
            userEmail = "john.doe@gmail.com",
            customPhotoUrl = null,
            googlePhotoUrl = null,
            isVendor = false
        )
    }
}