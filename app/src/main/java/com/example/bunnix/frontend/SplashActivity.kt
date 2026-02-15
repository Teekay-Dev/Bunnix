package com.example.bunnix.frontend

import android.content.Intent
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
import com.example.bunnix.backend.Routes
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BunnixTheme {
                val userPrefs = UserPreferences(this)

                SplashScreen(
                    userPrefs = userPrefs,
                    onNavigate = { route ->
                        // Navigate to MainActivity with route
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("start_route", route)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

// ✅ UNCHANGED - Your original UI
@Composable
fun SplashScreen(
    userPrefs: UserPreferences,
    onNavigate: (String) -> Unit
) {

    // ✅ Collect flows OUTSIDE LaunchedEffect
    val loggedIn by userPrefs.isLoggedIn.collectAsState(initial = false)
    val firstLaunch by userPrefs.isFirstLaunch.collectAsState(initial = true)

    // ✅ Navigation logic inside LaunchedEffect
    LaunchedEffect(true) {

        delay(2000)

        when {
            firstLaunch -> onNavigate(Routes.Signup)
            loggedIn -> onNavigate(Routes.Home)
            else -> onNavigate(Routes.Login)
        }
    }

    // ✅ UI - UNCHANGED
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        BunnixAnimatedLogo()
    }
}