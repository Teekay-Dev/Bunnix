package com.example.bunnix.frontend

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.bunnix.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.utils.NetworkResult
import kotlinx.coroutines.launch
import com.example.bunnix.model.AuthData
import com.example.bunnix.model.Vendor
import com.example.bunnix.model.VendorViewModel
import kotlinx.coroutines.runBlocking

class SignupActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPrefs = UserPreferences(this)
        setContent {
            SignupScreen(userPrefs){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}


@Composable
fun SignupScreen(userPrefs: UserPreferences,
                 viewModel: VendorViewModel = viewModel(),
                 onLogin: () -> Unit) {

    // 🔹 Common fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // 🔹 Customer fields
    var fullName by remember { mutableStateOf("") }

    // 🔹 Business fields
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }

    // 🔹 Account type
    var isCustomer by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(scrollState)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.bunnix_2),
            contentDescription = null,
            modifier = Modifier
                .width(200.dp)
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Bunnix",
            fontSize = 50.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(Modifier.height(20.dp))


        Text("Create Account", fontSize = 30.sp, color = Color.White)

        Spacer(Modifier.height(20.dp))

        // 🔥 CUSTOMER / BUSINESS SELECTOR
        Column {
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

            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.height(24.dp))

        // 🔥 Full Name (shows for BOTH)
        OutlinedTextField(
            fullName,
            { fullName = it },
            placeholder = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // 🔥 BUSINESS INPUTS
        if (!isCustomer) {

            OutlinedTextField(
                businessName,
                { businessName = it },
                placeholder = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                businessAddress,
                { businessAddress = it },
                placeholder = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
        }

        // 🔹 COMMON INPUTS
        OutlinedTextField(
            email,
            { email = it },
            placeholder = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        // 🔹 PHONE FIELD (Common)
        OutlinedTextField(
            phone,
            { phone = it },
            placeholder = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            password,
            { password = it },
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            confirm,
            { confirm = it },
            placeholder = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    val result = viewModel.registerUser( //viewModel.registerUser is still red
                        name = fullName,
                        email = email,
                        phone = phone,
                        password = password,
                        confirmPassword = confirm,
                        businessName = if (!isCustomer) businessName else null,
                        businessAddress = if (!isCustomer) businessAddress else null,
                        isVendor = !isCustomer
                    )

                    when (result) {
                        is NetworkResult.Success -> {
                            // Determine the account type for the local session
                            val accountType = if (isCustomer) "CUSTOMER" else "BUSINESS"

                            // Save to PrefsManager
                            userPrefs.setLoggedIn(true, if (isCustomer) "CUSTOMER" else "BUSINESS")
                            userPrefs.setVendorMode(!isCustomer) // Vendor if NOT customer

                            android.widget.Toast.makeText(context, "Welcome to Bunnix!", android.widget.Toast.LENGTH_SHORT).show()
                            onLogin() // Redirect to Login or Dashboard
                        }
                        is NetworkResult.Error -> {
                            android.widget.Toast.makeText(context, result.message ?: "Registration Failed", android.widget.Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
            shape = RoundedCornerShape(50.dp)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White) else Text("Create Account")
        }

        Spacer(Modifier.height(24.dp))

        Row {
            Text("Already have an account? ", color = Color.White)
            Text(
                "Login",
                color = Color(0xFFFF7900),
                modifier = Modifier.clickable { onLogin() }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    SignupScreen(userPrefs = UserPreferences(context = LocalContext.current)) { }
}



