package com.example.bunnix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.bunnix.frontend.OnboardingActivity
import com.example.bunnix.ui.theme.BunnixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BunnixTheme {
                OnboardingActivity()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BunnixTheme {
        OnboardingActivity()
    }
}