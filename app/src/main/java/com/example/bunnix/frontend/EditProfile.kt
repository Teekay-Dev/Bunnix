package com.example.bunnix.frontend

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.launch

// Colors (same as ProfileScreen)
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val VendorOrange = Color(0xFFFF8C42)
private val SuccessGreen = Color(0xFF10B981)

/**
 * Edit Profile Dialog - Works for both Customer and Vendor modes
 * Shows as a popup dialog with tabs for different sections
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    isVendor: Boolean = false,
    // Current values
    currentName: String = "",
    currentEmail: String = "",
    currentPhone: String = "",
    currentBusinessName: String? = null,
    currentBusinessAddress: String? = null,
    currentBusinessDescription: String? = null,
    // Callbacks
    onSaveProfile: (
        name: String,
        email: String,
        phone: String,
        businessName: String?,
        businessAddress: String?,
        businessDescription: String?
    ) -> Unit = { _, _, _, _, _, _ -> },
    onChangeProfilePicture: () -> Unit = {}
) {
    if (!showDialog) return

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = if (isVendor) listOf("Personal", "Business") else listOf("Personal")

    // Form states
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var phone by remember { mutableStateOf(currentPhone) }
    var businessName by remember { mutableStateOf(currentBusinessName ?: "") }
    var businessAddress by remember { mutableStateOf(currentBusinessAddress ?: "") }
    var businessDescription by remember { mutableStateOf(currentBusinessDescription ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(28.dp),
            color = SurfaceLight
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(OrangePrimary, OrangeLight)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Edit Profile",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }

                        if (isVendor) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Vendor Account",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Tabs (only for vendor)
                if (isVendor) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = OrangePrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = OrangePrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }

                // Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedTab) {
                        0 -> PersonalInfoTab(
                            name = name,
                            onNameChange = { name = it },
                            email = email,
                            onEmailChange = { email = it },
                            phone = phone,
                            onPhoneChange = { phone = it },
                            profileImageUrl = null,
                            onChangeProfilePicture = onChangeProfilePicture
                        )
                        1 -> BusinessInfoTab(
                            businessName = businessName,
                            onBusinessNameChange = { businessName = it },
                            businessAddress = businessAddress,
                            onBusinessAddressChange = { businessAddress = it },
                            businessDescription = businessDescription,
                            onBusinessDescriptionChange = { businessDescription = it }
                        )
                    }
                }

                // Bottom Buttons
                Surface(
                    color = Color.White,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                isLoading = true
                                // Simulate save
                                kotlinx.coroutines.GlobalScope.launch {
                                    kotlinx.coroutines.delay(1000)
                                    onSaveProfile(
                                        name,
                                        email,
                                        phone,
                                        if (isVendor) businessName else null,
                                        if (isVendor) businessAddress else null,
                                        if (isVendor) businessDescription else null
                                    )
                                    isLoading = false
                                    showSuccess = true
                                    kotlinx.coroutines.delay(500)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }
    }

    // Success Snackbar
    if (showSuccess) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = SuccessGreen,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Profile Updated!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoTab(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    profileImageUrl: String?,
    onChangeProfilePicture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Picture
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    color = OrangeSoft,
                    shape = CircleShape,
                    modifier = Modifier.size(120.dp),
                    border = BorderStroke(4.dp, OrangePrimary.copy(alpha = 0.2f)),
                    shadowElevation = 4.dp
                ) {
                    if (profileImageUrl != null) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = OrangePrimary
                            )
                        }
                    }
                }

                Surface(
                    onClick = onChangeProfilePicture,
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp),
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form Fields
        Text(
            "Personal Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = OrangePrimary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = OrangePrimary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary
            )
        )
    }
}

@Composable
private fun BusinessInfoTab(
    businessName: String,
    onBusinessNameChange: (String) -> Unit,
    businessAddress: String,
    onBusinessAddressChange: (String) -> Unit,
    businessDescription: String,
    onBusinessDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Business Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Name
        OutlinedTextField(
            value = businessName,
            onValueChange = onBusinessNameChange,
            label = { Text("Business Name") },
            leadingIcon = {
                Icon(Icons.Default.Store, contentDescription = null, tint = VendorOrange)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VendorOrange,
                focusedLabelColor = VendorOrange
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Address
        OutlinedTextField(
            value = businessAddress,
            onValueChange = onBusinessAddressChange,
            label = { Text("Business Address") },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = VendorOrange)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VendorOrange,
                focusedLabelColor = VendorOrange
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Description
        OutlinedTextField(
            value = businessDescription,
            onValueChange = onBusinessDescriptionChange,
            label = { Text("Business Description") },
            leadingIcon = {
                Icon(Icons.Default.Description, contentDescription = null, tint = VendorOrange)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VendorOrange,
                focusedLabelColor = VendorOrange
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Verification Badge
        Surface(
            color = VendorOrange.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    tint = VendorOrange
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Verified Business",
                        fontWeight = FontWeight.Bold,
                        color = VendorOrange
                    )
                    Text(
                        "Your business is verified on Bunnix",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// ===== PREVIEWS =====
@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun EditProfileDialogCustomerPreview() {
    BunnixTheme {
        EditProfileDialog(
            showDialog = true,
            onDismiss = {},
            isVendor = false,
            currentName = "John Doe",
            currentEmail = "john@example.com",
            currentPhone = "+234 801 234 5678"
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun EditProfileDialogVendorPreview() {
    BunnixTheme {
        EditProfileDialog(
            showDialog = true,
            onDismiss = {},
            isVendor = true,
            currentName = "Jane Smith",
            currentEmail = "jane@techhub.com",
            currentPhone = "+234 802 345 6789",
            currentBusinessName = "TechHub Store",
            currentBusinessAddress = "123 Tech Street, Lagos, Nigeria",
            currentBusinessDescription = "We sell quality electronics and gadgets at affordable prices."
        )
    }
}