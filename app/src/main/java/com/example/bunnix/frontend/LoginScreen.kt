package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPrefs = UserPreferences(this)

        setContent {
            LoginScreen(
                onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                },
                onLoginSuccess = {
                    runBlocking {
                        userPrefs.setLoggedIn(true)
                        userPrefs.setFirstLaunch(false)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}


// ✅ LOGIN SCREEN - BACKEND INTEGRATED (YOUR EXACT UI - ZERO CHANGES)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onSignupClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ✅ GOOGLE SIGN-IN LAUNCHER
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            // ✅ Use the idToken to sign in with Firebase
            account.idToken?.let { idToken ->
                scope.launch {
                    isLoading = true
                    val authResult = authViewModel.signInWithGoogle(idToken)

                    when (authResult) {
                        is AuthResult.Success -> {
                            Toast.makeText(
                                context,
                                "Welcome! 👋",
                                Toast.LENGTH_SHORT
                            ).show()
                            onLoginSuccess()
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
            Toast.makeText(
                context,
                "Google Sign-In failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ LOGO
        Image(
            painter = painterResource(R.drawable.bunnix_2),
            contentDescription = null,
            modifier = Modifier
                .width(180.dp)
                .height(140.dp)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Welcome Back 👋",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            "Login to continue shopping & booking",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(30.dp))

        // ✅ EMAIL FIELD
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            enabled = !isLoading


        )

        Spacer(Modifier.height(15.dp))

        // ✅ PASSWORD FIELD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),

            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),


            enabled = !isLoading,

            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(Modifier.height(25.dp))

        // ✅ LOGIN BUTTON
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
                            Toast.makeText(context, "Welcome back! 👋", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
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
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(Modifier.height(25.dp))

        Text(text = "OR Continue with", color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(15.dp))

        // ✅ SOCIAL BUTTONS
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // ✅ GOOGLE BUTTON - NOW WORKING!
            SocialButton(
                icon = R.drawable.google,
                enabled = !isLoading,
                onClick = {
                    // ✅ Launch Google Sign-In
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
            )

            // ✅ APPLE BUTTON - DISABLED (not implemented)
            SocialButton(
                icon = R.drawable.apple_logo,
                enabled = false,
                onClick = {
                    Toast.makeText(
                        context,
                        "Apple Sign-In coming soon!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        Spacer(Modifier.height(30.dp))

        // ✅ SIGNUP LINK
        Row {
            Text("Don't have an account? ")
            Text(
                "Sign Up",
                color = Color(0xFFFF7900),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignupClick() }
            )
        }
    }
}

// ✅ SOCIAL BUTTON - UNCHANGED
@Composable
fun SocialButton(
    icon: Int,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2),
            disabledContainerColor = Color(0xFFE0E0E0)
        ),
        enabled = enabled
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            alpha = if (enabled) 1f else 0.5f
        )
    }
}

// ✅ PREVIEW - UNCHANGED
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(onSignupClick = {}, onLoginSuccess = {})
}
