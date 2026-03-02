package com.example.bunnix.frontend

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bunnix.MainActivity
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs: SharedPreferences =
            getSharedPreferences("bunnix_prefs", MODE_PRIVATE)

        setContent {
            BunnixTheme {
                SplashScreen(
                    prefs = prefs,
                    onNavigateToOnboarding = {
                        startActivity(Intent(this, OnboardingActivity::class.java))
                        finish()
                    },
                    onNavigateToLogin = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onNavigateToMain = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    prefs: SharedPreferences,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(true) {

        delay(2000)

        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
            onNavigateToOnboarding()
            return@LaunchedEffect
        }

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Logged in → Always go to MainActivity
            // MainActivity will decide Vendor or Customer mode
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BunnixAnimatedLogo()
    }
}