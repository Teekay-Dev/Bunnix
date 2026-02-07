package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import kotlinx.coroutines.flow.first
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPrefs = UserPreferences(this)

        setContent {
            LoginScreen(
                onSignupClick = {
                    // User chooses to sign up
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                },
                onLoginSuccess = {
                    // User logged in successfully
                    runBlocking {
                        userPrefs.setLoggedIn(true)
                        userPrefs.setFirstLaunch(false)
                    }
                    val role = runBlocking { userPrefs.userRole.first() }
                    if (role == "BUSINESS") {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                }
            )
        }
    }
}


@Composable
fun LoginScreen(onSignupClick: () -> Unit, onLoginSuccess: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

        Spacer(Modifier.height(20.dp))

        Text(
            "Bunnix",
            fontSize = 50.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text("Login", color = Color.White)
        }

        Spacer(Modifier.height(24.dp))


        // ---------- OR CONTINUE WITH ----------
        Text(
            text = "OR\nContinue with",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // ---------- GOOGLE + APPLE BUTTONS ----------
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = { /* Google login logic */ },
                modifier = Modifier
                    .width(80.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(30.dp) // <-- BIGGER ICON
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = { /* Apple login logic */ },
                modifier = Modifier
                    .width(80.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.apple_logo),
                        contentDescription = "Apple",
                        modifier = Modifier.size(30.dp) // <-- BIGGER ICON
                    )
                }
            }
        }


        Spacer(Modifier.height(24.dp))

        Row {
            Text("Don't have an account? ", color = Color.White)
            Text(
                "Sign Up",
                color = Color(0xFFFF7900),
                modifier = Modifier.clickable { onSignupClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(onSignupClick = {}, onLoginSuccess = {})
}



