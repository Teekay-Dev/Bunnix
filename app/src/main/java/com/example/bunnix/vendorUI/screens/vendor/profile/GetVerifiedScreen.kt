package com.example.bunnix.vendorUI.screens.vendor.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.components.BunnixTopBar
import com.example.bunnix.viewmodel.VerificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetVerifiedScreen(
    navController: NavController,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var businessRegistrationNumber by remember { mutableStateOf("") }
    var taxIdentificationNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }

    var businessLicenseUri by remember { mutableStateOf<Uri?>(null) }
    var governmentIdUri by remember { mutableStateOf<Uri?>(null) }
    var proofOfAddressUri by remember { mutableStateOf<Uri?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    // Document pickers
    val businessLicensePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> businessLicenseUri = uri }

    val governmentIdPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> governmentIdUri = uri }

    val proofOfAddressPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> proofOfAddressUri = uri }

    // Navigate back on success
    LaunchedEffect(successMessage) {
        if (successMessage?.isNotEmpty() == true) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            BunnixTopBar(
                title = "Get Verified",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGrayBg)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Why Get Verified?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Verified vendors get a blue checkmark badge, increased visibility, and customer trust. We'll review your application within 2-3 business days.",
                                fontSize = 13.sp,
                                color = Color(0xFF1565C0),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Personal Information Section
                Text(
                    text = "Personal Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal Name") },
                    placeholder = { Text("As it appears on government ID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryModern,
                        focusedLabelColor = OrangePrimaryModern
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+234...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryModern,
                        focusedLabelColor = OrangePrimaryModern
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Business Information Section
                Text(
                    text = "Business Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = businessRegistrationNumber,
                    onValueChange = { businessRegistrationNumber = it },
                    label = { Text("Business Registration Number (CAC)") },
                    placeholder = { Text("RC123456 or BN123456") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryModern,
                        focusedLabelColor = OrangePrimaryModern
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = taxIdentificationNumber,
                    onValueChange = { taxIdentificationNumber = it },
                    label = { Text("Tax Identification Number (TIN)") },
                    placeholder = { Text("Optional") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryModern,
                        focusedLabelColor = OrangePrimaryModern
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = businessAddress,
                    onValueChange = { businessAddress = it },
                    label = { Text("Business Address") },
                    placeholder = { Text("Full physical address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryModern,
                        focusedLabelColor = OrangePrimaryModern
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Required Documents Section
                Text(
                    text = "Required Documents",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Business License/CAC Document
                DocumentUploadCard(
                    title = "Business License / CAC Certificate",
                    description = "Upload your registered business certificate",
                    selectedUri = businessLicenseUri,
                    onUploadClick = { businessLicensePicker.launch("image/*") },
                    required = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Government ID
                DocumentUploadCard(
                    title = "Government Issued ID",
                    description = "National ID, Driver's License, or International Passport",
                    selectedUri = governmentIdUri,
                    onUploadClick = { governmentIdPicker.launch("image/*") },
                    required = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Proof of Address
                DocumentUploadCard(
                    title = "Proof of Business Address",
                    description = "Utility bill, rent agreement, or bank statement",
                    selectedUri = proofOfAddressUri,
                    onUploadClick = { proofOfAddressPicker.launch("image/*") },
                    required = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Error message
                if (error?.isNotEmpty() == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFF44336)
                            )
                            Text(
                                text = error ?: "",
                                fontSize = 14.sp,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Submit Button
                Button(
                    onClick = {
                        if (businessLicenseUri != null && governmentIdUri != null && proofOfAddressUri != null) {
                            viewModel.submitVerificationRequest(
                                fullName = fullName,
                                phoneNumber = phoneNumber,
                                businessRegistrationNumber = businessRegistrationNumber,
                                taxIdentificationNumber = taxIdentificationNumber,
                                businessAddress = businessAddress,
                                businessLicenseUri = businessLicenseUri!!,
                                governmentIdUri = governmentIdUri!!,
                                proofOfAddressUri = proofOfAddressUri!!
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading &&
                            fullName.isNotBlank() &&
                            phoneNumber.isNotBlank() &&
                            businessRegistrationNumber.isNotBlank() &&
                            businessAddress.isNotBlank() &&
                            businessLicenseUri != null &&
                            governmentIdUri != null &&
                            proofOfAddressUri != null,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimaryModern
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Submit Verification Request",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Upload Progress Overlay
            if (uploadProgress > 0f && uploadProgress < 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.size(64.dp),
                            color = OrangePrimaryModern,
                            strokeWidth = 6.dp,
                            trackColor = ProgressIndicatorDefaults.circularTrackColor,
                            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Uploading Documents...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(uploadProgress * 100).toInt()}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryModern
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentUploadCard(
    title: String,
    description: String,
    selectedUri: Uri?,
    onUploadClick: () -> Unit,
    required: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUploadClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedUri != null) Color(0xFFE8F5E9) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (selectedUri != null) Color(0xFF4CAF50) else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (selectedUri != null) Color(0xFF4CAF50).copy(alpha = 0.1f)
                        else OrangePrimaryModern.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri != null) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = OrangePrimaryModern,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (required) {
                        Text(
                            text = "*",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF44336)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedUri != null) "Document uploaded ✓" else description,
                    fontSize = 13.sp,
                    color = if (selectedUri != null) Color(0xFF4CAF50) else TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = if (selectedUri != null) Icons.Default.Done else Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (selectedUri != null) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}