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
import com.example.bunnix.ui.theme.BunnixTheme
import com.example.bunnix.vendorUI.screens.vendor.dashboard.VendorMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BunnixTheme {
                SplashScreen(
                    onNavigateToLogin = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onNavigateToSignup = {
                        startActivity(Intent(this, SignupActivity::class.java))
                        finish()
                    },
                    onNavigateToCustomer = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onNavigateToVendor = {
                        startActivity(Intent(this, VendorMainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToVendor: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(true) {
        delay(2000) // Show splash for 2 seconds

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is logged in, check if vendor or customer
            try {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                val isVendor = userDoc.getBoolean("isVendor") ?: false

                if (isVendor) {
                    onNavigateToVendor()
                } else {
                    onNavigateToCustomer()
                }
            } catch (e: Exception) {
                // Error fetching user data, go to login
                onNavigateToLogin()
            }
        } else {
            // User NOT logged in, go to login
            onNavigateToLogin()
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BunnixAnimatedLogo()
    }
}