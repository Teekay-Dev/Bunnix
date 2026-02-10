package com.example.bunnix.frontend

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bunnix.MainActivity
import com.example.bunnix.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen { destination ->
                startActivity(Intent(this, destination))
                finish()
            }
        }
    }
}


@Composable
fun SplashScreen(onFinish: (Class<*>) -> Unit) {

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    LaunchedEffect(Unit) {

        delay(3000)

        val firstLaunch = userPrefs.isFirstLaunch.first()
        val loggedIn = userPrefs.isLoggedIn.first()

        val destination = when {
            firstLaunch -> OnboardingActivity::class.java
            loggedIn -> MainActivity::class.java
            else -> LoginActivity::class.java
        }

        onFinish(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        BunnixAnimatedLogo()
    }
}

