package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is switching modes (already has an account) - UNCHANGED
        val isSwitchingMode = intent.getBooleanExtra("IS_SWITCHING_MODE", false)
        val currentMode = intent.getStringExtra("CURRENT_MODE") ?: "customer"

        setContent {
            SignupScreen(
                isSwitchingMode = isSwitchingMode,
                currentMode = currentMode,
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onSignupSuccess = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun SignupScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    isSwitchingMode: Boolean = false,
    currentMode: String = "customer",
    onLoginClick: () -> Unit,
    onSignupSuccess: (Boolean) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var isCustomer by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(false) }

    // Title logic - UNCHANGED
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

    // ========== UI STARTS HERE - MATCHING YOUR IMAGE ==========
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

            // Logo with orange border

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

            // Show current account info if switching - UNCHANGED
            if (isSwitchingMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF7900)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "You need a different email address for your ${if (currentMode == "customer") "business" else "customer"} account",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Customer/Business Toggle - EXACT from image
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
                        .clickable { isCustomer = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Customer",
                        color = if (isCustomer) Color.White else Color(0xFF666666),
                        fontSize = 16.sp,
                        fontWeight = if (isCustomer) FontWeight.Bold else FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (!isCustomer) Color(0xFFFF7900) else Color.Transparent)
                        .clickable { isCustomer = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Business",
                        color = if (!isCustomer) Color.White else Color(0xFF666666),
                        fontSize = 16.sp,
                        fontWeight = if (!isCustomer) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Form Fields with Icons - EXACT from image
            IconTextField(fullName, { fullName = it }, "Full Name", Icons.Default.Person, isLoading)
            Spacer(Modifier.height(14.dp))

            if (!isCustomer) {
                IconTextField(businessName, { businessName = it }, "Business Name", Icons.Default.Store, isLoading)
                Spacer(Modifier.height(14.dp))
                IconTextField(businessAddress, { businessAddress = it }, "Business Address", Icons.Default.LocationOn, isLoading)
                Spacer(Modifier.height(14.dp))
            }

            IconTextField(email, { email = it }, "Email Address", Icons.Default.Email, isLoading)
            Spacer(Modifier.height(14.dp))

            IconTextField(phone, { phone = it }, "Phone Number", Icons.Default.Phone, isLoading)
            Spacer(Modifier.height(14.dp))

            // Password with eye icon
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color(0xFFB0B0B0)) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF999999)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            null,
                            tint = Color(0xFF999999)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7900),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color(0xFFFF7900)
                ),
                enabled = !isLoading
            )

            Spacer(Modifier.height(24.dp))

            // Create Account Button
            Button(
                onClick = {
                    scope.launch {
                        // ALL VALIDATION & BACKEND - UNCHANGED
                        if (fullName.isBlank()) {
                            Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (!isCustomer && businessName.isBlank()) {
                            Toast.makeText(context, "Please enter your business name", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (!isCustomer && businessAddress.isBlank()) {
                            Toast.makeText(context, "Please enter your business address", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (phone.isBlank()) {
                            Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (password.isBlank()) {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (password != confirm) {
                            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        isLoading = true

                        val result = authViewModel.signUpWithEmail(
                            email = email,
                            password = password,
                            displayName = fullName,
                            phone = phone,
                            isBusinessAccount = !isCustomer,
                            businessName = businessName,
                            businessAddress = businessAddress
                        )

                        when (result) {
                            is AuthResult.Success -> {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                                try {
                                    val userData = hashMapOf(
                                        "userId" to userId,
                                        "name" to fullName,
                                        "email" to email,
                                        "phone" to phone,
                                        "isVendor" to !isCustomer,
                                        "profilePicUrl" to "",
                                        "address" to if (!isCustomer) businessAddress else "",
                                        "city" to "",
                                        "state" to "",
                                        "country" to "Nigeria",
                                        "fcmToken" to "",
                                        "createdAt" to FieldValue.serverTimestamp(),
                                        "lastActive" to FieldValue.serverTimestamp()
                                    )

                                    firestore.collection("users")
                                        .document(userId)
                                        .set(userData)
                                        .await()

                                    if (!isCustomer) {
                                        val vendorData = hashMapOf(
                                            "vendorId" to userId,
                                            "userId" to userId,
                                            "businessName" to businessName,
                                            "description" to "",
                                            "coverPhotoUrl" to "",
                                            "category" to "",
                                            "subCategories" to emptyList<String>(),
                                            "bankName" to "",
                                            "accountNumber" to "",
                                            "accountName" to "",
                                            "alternativePayment" to "",
                                            "rating" to 0.0,
                                            "totalReviews" to 0,
                                            "totalSales" to 0,
                                            "totalRevenue" to 0.0,
                                            "isAvailable" to true,
                                            "workingHours" to emptyMap<String, String>(),
                                            "location" to null,
                                            "address" to businessAddress,
                                            "phone" to phone,
                                            "email" to email,
                                            "createdAt" to FieldValue.serverTimestamp(),
                                            "updatedAt" to FieldValue.serverTimestamp()
                                        )

                                        firestore.collection("vendorProfiles")
                                            .document(userId)
                                            .set(vendorData)
                                            .await()
                                    }

                                    Toast.makeText(context, "Welcome to Bunnix 🎉", Toast.LENGTH_SHORT).show()
                                    onSignupSuccess(!isCustomer)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }

                            is AuthResult.Error -> {
                                Toast.makeText(
                                    context,
                                    result.message ?: "Registration Failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {}
                        }

                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Secure Registration Badge - EXACT from image
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, tint = Color(0xFF999999), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Secure Registration", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    Text("We protect your data", fontSize = 13.sp, color = Color(0xFF888888))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Login Link
            Row {
                Text("Already have an account? ", color = Color(0xFF666666), fontSize = 14.sp)
                Text(
                    "Login",
                    color = Color(0xFFFF7900),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// Reusable TextField Component
@Composable
fun IconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isLoading: Boolean
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
            focusedBorderColor = Color(0xFFFF7900),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = Color(0xFFFF7900)
        ),
        enabled = !isLoading
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
                "Phone Number",
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
