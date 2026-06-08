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

// Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    isVendor: Boolean = false,
    currentName: String = "",
    currentEmail: String = "",
    currentPhone: String = "",
    currentAddress: String = "",
    currentCity: String = "",
    currentState: String = "",
    currentBusinessName: String? = null,
    currentBusinessAddress: String? = null,
    currentBusinessDescription: String? = null,
    onSaveProfile: (
        name: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        businessName: String?,
        businessAddress: String?,
        businessDescription: String?
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onChangeProfilePicture: () -> Unit = {}
) {
    if (!showDialog) return

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = if (isVendor) listOf("Personal", "Business") else listOf("Personal")

    // Form states
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var phone by remember { mutableStateOf(currentPhone) }
    var address by remember { mutableStateOf(currentAddress) }
    var city by remember { mutableStateOf(currentCity) }
    var state by remember { mutableStateOf(currentState) }
    
    var businessName by remember { mutableStateOf(currentBusinessName ?: "") }
    var businessAddress by remember { mutableStateOf(currentBusinessAddress ?: "") }
    var businessDescription by remember { mutableStateOf(currentBusinessDescription ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(28.dp),
            color = SurfaceLight
        ) {
            Column {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(OrangePrimary, OrangeLight))).padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Color.White) }
                    }
                }

                if (isVendor) {
                    TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = OrangePrimary) {
                        tabs.forEachIndexed { index, title ->
                            Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                            PersonalInfoSection(name, {name=it}, email, {email=it}, phone, {phone=it})
                            Spacer(modifier = Modifier.height(24.dp))
                            AddressSection(address, {address=it}, city, {city=it}, state, {state=it})
                        }
                        1 -> BusinessInfoTab(businessName, {businessName=it}, businessAddress, {businessAddress=it}, businessDescription, {businessDescription=it})
                    }
                }

                Surface(color = Color.White, tonalElevation = 4.dp) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                isLoading = true
                                onSaveProfile(name, email, phone, address, city, state, businessName, businessAddress, businessDescription)
                                showSuccess = true
                                // Simplified: in a real app, wait for ViewModel callback
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White) else Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoSection(name: String, onNameChange: (String) -> Unit, email: String, onEmailChange: (String) -> Unit, phone: String, onPhoneChange: (String) -> Unit) {
    Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
}

@Composable
private fun AddressSection(address: String, onAddressChange: (String) -> Unit, city: String, onCityChange: (String) -> Unit, state: String, onStateChange: (String) -> Unit) {
    Text("Address Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Street Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = city, onValueChange = onCityChange, label = { Text("City") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = state, onValueChange = onStateChange, label = { Text("State") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun BusinessInfoTab(businessName: String, onBusinessNameChange: (String) -> Unit, businessAddress: String, onBusinessAddressChange: (String) -> Unit, businessDescription: String, onBusinessDescriptionChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Business Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = businessName, onValueChange = onBusinessNameChange, label = { Text("Business Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = businessAddress, onValueChange = onBusinessAddressChange, label = { Text("Business Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 2)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = businessDescription, onValueChange = onBusinessDescriptionChange, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 4)
    }
}
