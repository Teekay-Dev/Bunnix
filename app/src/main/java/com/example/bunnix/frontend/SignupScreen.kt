package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
//import com.example.bunnix.vendorui.VendorMainActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

@AndroidEntryPoint
class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is switching modes (already has an account)
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
    onSignupSuccess: (Boolean) -> Unit // Pass isVendor flag
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Common inputs
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // Customer input
    var fullName by remember { mutableStateOf("") }

    // Business inputs
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }

    // Mode selector
    var isCustomer by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val firestore = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(false) }

    // Show different title based on mode
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(R.drawable.bunnix_2),
            contentDescription = null,
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Bunnix",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = titleText, // Dynamic title
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = subtitleText, // Dynamic subtitle
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Show current account info if switching
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

        // Customer / Business Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(50.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Customer Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isCustomer) Color(0xFFFF7900) else Color.Transparent,
                        RoundedCornerShape(50.dp)
                    )
                    .clickable { isCustomer = true }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Customer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCustomer) Color.White else Color.Gray
                )
            }

            // Business Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (!isCustomer) Color(0xFFFF7900) else Color.Transparent,
                        RoundedCornerShape(50.dp)
                    )
                    .clickable { isCustomer = false }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Business",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isCustomer) Color.White else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7900),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Business Name (only for vendors)
        if (!isCustomer) {
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                placeholder = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7900),
                    unfocusedBorderColor = Color.LightGray
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Business Address
            OutlinedTextField(
                value = businessAddress,
                onValueChange = { businessAddress = it },
                placeholder = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7900),
                    unfocusedBorderColor = Color.LightGray
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7900),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Phone
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7900),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7900),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = !isLoading,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            placeholder = { Text("Confirm Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7900),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = !isLoading,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Create Account Button
        Button(
            onClick = {
                scope.launch {
                    // Validation
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

                    // Sign up with email
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
                            // CORRECT
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                            try {
                                // Create user document in Firestore
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

                                // If vendor, create vendor profile
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
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secure Registration Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    "Secure Registration",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Text(
                    "We protect your data",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Login Link
        Row {
            Text("Already have an account? ", color = Color.Black)
            Text(
                "Login",
                color = Color(0xFFFF7900),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}