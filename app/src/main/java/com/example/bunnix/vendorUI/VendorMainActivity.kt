package com.example.bunnix.vendorUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bunnix.frontend.LoginActivity
import com.example.bunnix.ui.theme.BunnixTheme
import com.example.bunnix.vendorUI.screens.vendor.VendorMainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VendorMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BunnixTheme {
                VendorMainScreen(
                    onNavigateToLogin = {
                        // When user logs out
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}