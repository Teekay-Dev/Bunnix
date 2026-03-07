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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.bunnix.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

@Composable
fun EmailInstructionScreen(
    onCheckVerification: () -> Unit,
    onBackClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Auto-check every 2 seconds when screen is visible
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            onCheckVerification()
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
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = OrangePrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Verify your email",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "We've sent a verification link to your email. Please check your inbox and click the link.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📧 Steps:", fontWeight = FontWeight.Bold, color = Color(0xFFFF7900))
                Spacer(modifier = Modifier.height(8.dp))
                Text("1. Open your email app", color = Color(0xFF666666))
                Text("2. Click the verification link", color = Color(0xFF666666))
                Text("3. Return to this app", color = Color(0xFF666666))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        OutlinedButton(
            onClick = onCheckVerification,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
        ) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp), tint = OrangePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("I've Verified - Continue", fontSize = 18.sp, color = OrangePrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Auto-checking...",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackClick) {
            Text("Back", color = Color.Gray)
        }
    }
}