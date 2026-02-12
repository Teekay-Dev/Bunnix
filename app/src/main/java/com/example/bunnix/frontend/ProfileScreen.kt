package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    currentMode: String,
    vendorEnabled: Boolean,
    onSwitchMode: () -> Unit,
    onLogout: () -> Unit,
    onBecomeVendor: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {

    // ✅ Popup States
    var popupTitle by remember { mutableStateOf("") }
    var popupText by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }

    fun openPopup(title: String, text: String) {
        popupTitle = title
        popupText = text
        showPopup = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // ✅ TOP HEADER (Back + Settings)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onBack?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                openPopup("Settings", "Account settings coming soon.")
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ✅ USER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xFFFF7900), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column {
                    Text(
                        text = "Lucy Fikabo", // later from backend
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "lucy@email.com",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mode Badge
                    Text(
                        text =
                            if (currentMode.lowercase() == "customer")
                                "Customer Account"
                            else
                                "Vendor Dashboard",
                        fontSize = 13.sp,
                        color = Color(0xFFFF7900),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // ✅ QUICK OPTIONS
        Text(
            text = "Account Options",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileOption("Edit Profile", Icons.Default.Edit) {
            openPopup("Edit Profile", "Update your personal information here.")
        }

        ProfileOption("Notifications", Icons.Default.Notifications) {
            openPopup("Notifications", "Manage alerts and reminders here.")
        }

        ProfileOption("Privacy & Security", Icons.Default.Lock) {
            openPopup("Privacy & Security", "Control password & security settings.")
        }

        Spacer(modifier = Modifier.height(25.dp))

        // ✅ BUSINESS SECTION
        Text(
            text = "Business Mode",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Become Vendor Button
        if (!vendorEnabled) {
            OutlinedButton(
                onClick = { onBecomeVendor?.invoke() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFF7900))
            ) {
                Text("Become a Vendor", color = Color(0xFFFF7900))
            }
        }

        // Switch Mode Button
        if (vendorEnabled) {
            Button(
                onClick = onSwitchMode,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7900)
                )
            ) {
                Text(
                    text =
                        if (currentMode.lowercase() == "customer")
                            "Switch to Vendor Dashboard"
                        else
                            "Switch to Customer Mode",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // ✅ LOGOUT BUTTON
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontWeight = FontWeight.Bold)
        }
    }

    // ✅ POPUP DIALOG (Still here)
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("Close")
                }
            },
            title = { Text(popupTitle, fontWeight = FontWeight.Bold) },
            text = { Text(popupText) }
        )
    }
}

@Composable
fun ProfileOption(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF7900)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileScreen(
        currentMode = "customer",
        vendorEnabled = true,
        onSwitchMode = {},
        onLogout = {}
    )
}
