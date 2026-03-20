package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.R
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VerificationStep
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@AndroidEntryPoint
class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isSwitchingMode = intent.getBooleanExtra("IS_SWITCHING_MODE", false)
        val currentMode = intent.getStringExtra("CURRENT_MODE") ?: "customer"

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val verificationState by authViewModel.verificationState.collectAsState()

            SignupScreen(
                isSwitchingMode = isSwitchingMode,
                currentMode = currentMode,
                verificationStep = verificationState.currentStep, // NEW: Pass verification state
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onSignupSuccess = { user, password, vendorData ->
                    authViewModel.initiateSignup(user, password, vendorData)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    isSwitchingMode: Boolean = false,
    currentMode: String = "customer",
    verificationStep: VerificationStep = VerificationStep.IDLE, // NEW PARAM
    onLoginClick: () -> Unit,
    onSignupSuccess: (User, String, com.example.bunnix.database.models.VendorProfile?) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var businessCategory by remember { mutableStateOf("") }
    var isCustomer by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // NEW: Check if verification is in progress
    val isVerificationInProgress = verificationStep != VerificationStep.IDLE

    val titleText = when {
        isSwitchingMode && currentMode == "customer" -> "Create Business Account"
        isSwitchingMode && currentMode == "vendor" -> "Create Customer Account"
        else -> "Create Account"
    }

    val subtitleText = when {
        isSwitchingMode && currentMode == "customer" -> "Use a different email for your business"
        isSwitchingMode && currentMode == "vendor" -> "Use a different email for shopping"
        else -> "Join Bunnix today"
    }

    // ========== UI EXACTLY AS YOUR IMAGE ==========
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Image(
                painter = painterResource(R.drawable.bunnix_2),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text("Bunnix", fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

            Spacer(Modifier.height(8.dp))

            Text(titleText, fontSize = 20.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)

            if (subtitleText.isNotEmpty()) {
                Text(
                    subtitleText,
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            if (isSwitchingMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFFF7900))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "You need a different email address for your ${if (currentMode == "customer") "business" else "customer"} account",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Toggle - DISABLED DURING VERIFICATION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isCustomer) Color(0xFFFF7900) else Color.Transparent)
                        .clickable(enabled = !isVerificationInProgress) { // DISABLED
                            if (!isCustomer){
                                isCustomer = true
                                email = ""; phone = ""; fullName = ""; password = ""; confirm = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Customer", color = if (isCustomer) Color.White else Color(0xFF666666), fontSize = 16.sp, fontWeight = if (isCustomer) FontWeight.Bold else FontWeight.Medium)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (!isCustomer) Color(0xFFFF7900) else Color.Transparent)
                        .clickable(enabled = !isVerificationInProgress) { // DISABLED
                            if (isCustomer){
                                isCustomer = false
                                email = ""; phone = ""; fullName = ""; password = ""; confirm = ""
                                businessName = ""; businessAddress = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Business", color = if (!isCustomer) Color.White else Color(0xFF666666), fontSize = 16.sp, fontWeight = if (!isCustomer) FontWeight.Bold else FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Form Fields - DISABLED DURING VERIFICATION
            IconTextField(fullName, { fullName = it }, "Full Name", Icons.Default.Person, isVerificationInProgress, KeyboardType.Text)
            Spacer(Modifier.height(14.dp))

            if (!isCustomer) {
                IconTextField(businessName, { businessName = it }, "Business Name", Icons.Default.Store, isVerificationInProgress, KeyboardType.Text)
                Spacer(Modifier.height(14.dp))
                IconTextField(businessAddress, { businessAddress = it }, "Business Address", Icons.Default.LocationOn, isVerificationInProgress, KeyboardType.Text)
                Spacer(Modifier.height(14.dp))


                // REPLACE the entire Box { OutlinedTextField + DropdownMenu } block with:

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isVerificationInProgress) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = businessCategory,
                        onValueChange = {},
                        placeholder = { Text("Select Category", color = Color(0xFFB0B0B0)) },
                        leadingIcon = { Icon(Icons.Default.Category, null, tint = Color(0xFF999999)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFFFF7900),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = Color(0xFFFF7900)
                        ),
                        enabled = !isVerificationInProgress
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("Food","Fashion","Events","Home","Beauty","Tech","Sports","Health","Versatile")
                            .forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            color = if (option == businessCategory) Color(0xFFFF7900) else Color(0xFF1A1A1A),
                                            fontWeight = if (option == businessCategory) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = { businessCategory = option; expanded = false }
                                )
                            }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            IconTextField(email, { email = it }, "Email Address", Icons.Default.Email, isVerificationInProgress, KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            IconTextField(phone, { phone = it }, "Phone Number", Icons.Default.Phone, isVerificationInProgress, KeyboardType.Phone)
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color(0xFFB0B0B0)) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF999999)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color(0xFF999999))
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF7900),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color(0xFFFF7900)
                ),
                enabled = !isVerificationInProgress // DISABLED
            )
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                placeholder = { Text(" Confirm Password", color = Color(0xFFB0B0B0)) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF999999)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color(0xFF999999))
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF7900),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color(0xFFFF7900)
                ),
                enabled = !isVerificationInProgress // DISABLED
            )

            Spacer(Modifier.height(24.dp))

            // Create Account Button - SHOWS LOADING DURING VERIFICATION
            Button(
                onClick = {
                    scope.launch {
                        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (!isCustomer && (businessName.isBlank() || businessAddress.isBlank() || businessCategory.isBlank())) {
                            Toast.makeText(context, "Please fill all business details including category", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (password.trim() != confirm.trim()) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val newUser = User(
                            userId = "",
                            name = fullName,
                            email = email,
                            phone = phone,
                            profilePicUrl = "",
                            isVendor = !isCustomer,
                            address = if (!isCustomer) businessAddress else "",
                            city = "", state = "", country = "Nigeria",
                            createdAt = Timestamp.now(),
                            lastActive = Timestamp.now()
                        )

                        if (!isCustomer) {
                            val vendorData = com.example.bunnix.database.models.VendorProfile(
                                vendorId = "",
                                businessName = businessName,
                                category = businessCategory,
                                address = businessAddress,
                                phone = phone,
                                email = email
                            )
                            onSignupSuccess(newUser, password, vendorData)
                        } else {
                            onSignupSuccess(newUser, password, null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
                enabled = !isVerificationInProgress // DISABLED
            ) {
                if (isVerificationInProgress) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, tint = Color(0xFF999999), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Secure Registration", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    Text("We protect your data", fontSize = 13.sp, color = Color(0xFF888888))
                }
            }

            Spacer(Modifier.height(24.dp))

            Row {
                Text("Already have an account? ", color = Color(0xFF666666), fontSize = 14.sp)
                Text(
                    "Login",
                    color = Color(0xFFFF7900),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = !isVerificationInProgress) { onLoginClick() } // DISABLED
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun IconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isLoading: Boolean, // Used to disable
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFB0B0B0)) },
        leadingIcon = { Icon(icon, null, tint = Color(0xFF999999)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color(0xFFFF7900),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = Color(0xFFFF7900)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        enabled = !isLoading // DISABLED
    )
}


@Preview(showBackground = true, device = "id:pixel_5", name = "SignUp Screen")
@Composable
fun SignupScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))



                Image(
                    painter = painterResource(R.drawable.bunnix_2),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )


            Spacer(Modifier.height(12.dp))

            Text("Bunnix", fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

            Spacer(Modifier.height(8.dp))

            Text("Create Account", fontSize = 20.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(24.dp))

            // Customer/Business Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(27.dp)
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFFF7900),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Customer",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Business",
                        color = Color(0xFF666666),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Form Fields Preview
            val placeholders = listOf(
                "Full Name",
                "Email Address",
                "Phone Number Eg:+2348012345678, not 08012345678",
                "Password"
            )

            placeholders.forEach { placeholder ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(placeholder, color = Color(0xFFB0B0B0))
                }
                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(10.dp))

            // Create Account Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Color(0xFFFF7900),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))


            Row {
                Text("Already have an account? ", color = Color(0xFF666666), fontSize = 14.sp)
                Text(
                    "Login",
                    color = Color(0xFFFF7900),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
