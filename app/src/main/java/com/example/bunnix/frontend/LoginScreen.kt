package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthViewModel by viewModels()

        setContent {
            LoginScreen(
                authViewModel = viewModel,
                onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                },
                onLoginSuccess = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onSignupClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    // Google Sign-In Launcher - UNCHANGED
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            account.idToken?.let { idToken ->
                scope.launch {
                    isLoading = true
                    val authResult = authViewModel.signInWithGoogle(idToken)

                    when (authResult) {
                        is AuthResult.Success -> {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                            try {
                                val userDoc = firestore.collection("users")
                                    .document(userId)
                                    .get()
                                    .await()

                                val isVendor = userDoc.getBoolean("isVendor") ?: false

                                Toast.makeText(context, "Welcome! 👋", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is AuthResult.Error -> {
                            Toast.makeText(
                                context,
                                authResult.message ?: "Google Sign-In failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                    isLoading = false
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Orange Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0CCBF),
                                Color(0xFFFFFFFF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {

                    Image(
                        painter = painterResource(R.drawable.bunnix_2),
                        contentDescription = "Logo",
                        modifier = Modifier.size(200.dp)
                        )

                }
            }

            // White Card Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome Back 👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Login to continue shopping & booking",
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email Address", color = Color(0xFFB0B0B0)) },
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

                Spacer(Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color(0xFFB0B0B0)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFF999999)
                            )
                        }
                    },
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

                Spacer(Modifier.height(28.dp))

                // Login Button
                Button(
                    onClick = {
                        scope.launch {
                            if (email.isBlank()) {
                                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            if (password.isBlank()) {
                                Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            isLoading = true
                            val result = authViewModel.signInWithEmail(email, password)

                            when (result) {
                                is AuthResult.Success -> {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                                    try {
                                        val userDoc = firestore.collection("users")
                                            .document(userId)
                                            .get()
                                            .await()

                                        val isVendor = userDoc.getBoolean("isVendor") ?: false

                                        Toast.makeText(context, "Welcome back! 👋", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                is AuthResult.Error -> {
                                    Toast.makeText(context, result.message ?: "Login Failed", Toast.LENGTH_LONG).show()
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
                        Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("OR Continue with", color = Color(0xFF888888), fontSize = 14.sp)

                Spacer(Modifier.height(20.dp))

                // Social Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable(enabled = !isLoading) {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "Google",
                            modifier = Modifier.size(28.dp),
                            alpha = if (isLoading) 0.5f else 1f
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable(enabled = false) {
                                Toast.makeText(context, "Apple Sign-In coming soon!", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.apple_logo),
                            contentDescription = "Apple",
                            modifier = Modifier.size(28.dp),
                            alpha = 0.5f
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Sign Up Link
                Row {
                    Text("Don't have an account? ", color = Color(0xFF666666), fontSize = 14.sp)
                    Text(
                        "Sign Up",
                        color = Color(0xFFFF7900),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onSignupClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Orange Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0CCBF),
                                Color(0xFFFFFFFF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                        Image(
                            painter = painterResource(R.drawable.bunnix_2),
                            contentDescription = "Logo",
                            modifier = Modifier.size(200.dp)
                        )

                }
            }

            // White Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
                    .padding(horizontal = 24.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome Back 👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Login to continue shopping & booking",
                    fontSize = 15.sp,
                    color = Color(0xFF666666)
                )

                Spacer(Modifier.height(32.dp))

                // Email Field Preview
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
                    Text("Email Address", color = Color(0xFFB0B0B0))
                }

                Spacer(Modifier.height(16.dp))

                // Password Field Preview
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
                    Text("Password", color = Color(0xFFB0B0B0))
                }

                Spacer(Modifier.height(28.dp))

                // Login Button
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
                        "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text("OR Continue with", color = Color(0xFF888888), fontSize = 14.sp)

                Spacer(Modifier.height(20.dp))

                // Social Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .background(
                                Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .background(
                                Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row {
                    Text("Don't have an account? ", color = Color(0xFF666666), fontSize = 14.sp)
                    Text(
                        "Sign Up",
                        color = Color(0xFFFF7900),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}