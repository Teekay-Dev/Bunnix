package com.example.bunnix.frontend

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
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

//class SplashActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            SplashScreen { destination ->
//                startActivity(Intent(this, destination))
//                finish()
//            }
//        }
//    }
//}

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
            firstLaunch -> onNavigate("signup")
            loggedIn -> onNavigate("home")
            else -> onNavigate("login")
        }
    }

    // ✅ UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        BunnixAnimatedLogo()
    }
}



