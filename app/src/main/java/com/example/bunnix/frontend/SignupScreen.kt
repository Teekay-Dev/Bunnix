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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.R
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// ✅ SIGNUP ACTIVITY - BACKEND CONNECTED
@AndroidEntryPoint
class SignupActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPrefs = UserPreferences(this)

        setContent {
            SignupScreen(
                userPrefs = userPrefs,
                onLogin = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

// ✅ SIGNUP SCREEN - BACKEND INTEGRATED (UI UNCHANGED)
@Composable
fun SignupScreen(
    userPrefs: UserPreferences,
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogin: () -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }
    // 🔹 COMMON INPUTS
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // 🔹 CUSTOMER INPUT
    var fullName by remember { mutableStateOf("") }

    // 🔹 BUSINESS INPUTS
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }

    // 🔹 MODE SELECTOR
    var isCustomer by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // ✅ WHITE BACKGROUND - UNCHANGED
            .verticalScroll(scrollState)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ✅ LOGO - UNCHANGED
        Image(
            painter = painterResource(R.drawable.bunnix_2),
            contentDescription = null,
            modifier = Modifier
                .width(200.dp)
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Bunnix",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Create Account",
            fontSize = 26.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ CUSTOMER / BUSINESS SELECTOR - UNCHANGED
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                "Customer",
                fontSize = 18.sp,
                color = if (isCustomer) Color(0xFFFF7900) else Color.Gray,
                modifier = Modifier.clickable { isCustomer = true }
            )

            Text(
                "Business",
                fontSize = 18.sp,
                color = if (!isCustomer) Color(0xFFFF7900) else Color.Gray,
                modifier = Modifier.clickable { isCustomer = false }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ FULL NAME (Both) - UNCHANGED
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ BUSINESS INPUTS ONLY - UNCHANGED
        if (!isCustomer) {

            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                placeholder = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = businessAddress,
                onValueChange = { businessAddress = it },
                placeholder = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // ✅ COMMON INPUTS - UNCHANGED
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Description for accessibility (screen readers)
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            placeholder = { Text("Confirm Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Description for accessibility (screen readers)
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ SIGNUP BUTTON - BACKEND INTEGRATED (UI UNCHANGED)
        Button(
            onClick = {
                scope.launch {
                    // ✅ VALIDATION
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

                    // ✅ BACKEND CALL - USING YOUR BACKEND USE CASE
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
                            // ✅ Save Login Session
                            userPrefs.setLoggedIn(true)

                            if (isCustomer) {
                                userPrefs.setCustomerCreated(true)
                                userPrefs.setMode("CUSTOMER")
                            } else {
                                userPrefs.setVendorCreated(true)
                                userPrefs.setMode("VENDOR")
                            }

                            Toast.makeText(
                                context,
                                "Welcome to Bunnix 🎉",
                                Toast.LENGTH_SHORT
                            ).show()

                            onLogin()
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900))
        ) {
            if (isLoading)
                CircularProgressIndicator(color = Color.White)
            else
                Text("Create Account")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ LOGIN LINK - UNCHANGED
        Row {
            Text("Already have an account? ", color = Color.Black)

            Text(
                "Login",
                color = Color(0xFFFF7900),
                modifier = Modifier.clickable { onLogin() }
            )
        }
    }
}

// ✅ PREVIEW - UNCHANGED
@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    SignupScreen(
        userPrefs = UserPreferences(LocalContext.current),
        onLogin = {}
    )
}
