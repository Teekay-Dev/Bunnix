package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.bunnix.ui.theme.OrangePrimary

@Composable
fun EmailInstructionScreen(
    onOpenEmailApp: () -> Unit,
    onCheckVerification: () -> Unit,
    onBackClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Auto-check when screen becomes visible
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onCheckVerification()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon/Illustration
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = OrangePrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Verify your email",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a verification link to your email address. Please check your inbox and click the link to verify your account.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Open Email App Button
        Button(
            onClick = onOpenEmailApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text("Open Email App", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Check Verification Button
        OutlinedButton(
            onClick = onCheckVerification,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = OrangePrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = OrangePrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("I've Verified My Email", fontSize = 18.sp, color = OrangePrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Auto-check hint
        Text(
            "Auto-checking when you return...",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackClick) {
            Text("Back", color = Color.Gray)
        }
    }
}