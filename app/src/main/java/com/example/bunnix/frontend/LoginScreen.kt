package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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

                    // ✅ Move to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onSignupClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

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
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(15.dp))

        // ✅ PASSWORD FIELD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(25.dp))

        // ✅ LOGIN BUTTON
        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF7900)
            )
        ) {
            Text(
                "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(25.dp))

        // ✅ OR CONTINUE WITH
        Text(
            text = "OR Continue with",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(15.dp))

        // ✅ SOCIAL BUTTONS
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SocialButton(icon = R.drawable.google)
            SocialButton(icon = R.drawable.apple_logo)
        }

        Spacer(Modifier.height(30.dp))

        // ✅ SIGNUP LINK
        Row {
            Text("Don't have an account? ")

            Text(
                "Sign Up",
                color = Color(0xFFFF7900),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onSignupClick()
                }
            )
        }
    }
}

@Composable
fun SocialButton(icon: Int) {
    Button(
        onClick = { },
        modifier = Modifier
            .size(70.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2)
        )
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(onSignupClick = {}, onLoginSuccess = {})
}
