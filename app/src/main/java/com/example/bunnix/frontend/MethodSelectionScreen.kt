package com.example.bunnix.frontend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MethodSelectionScreen(
    onPhoneSelected: () -> Unit,
    onEmailSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Choose Verification Method", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onPhoneSelected, modifier = Modifier.fillMaxWidth()) {
            Text("Verify via Phone (SMS)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEmailSelected, modifier = Modifier.fillMaxWidth()) {
            Text("Verify via Email (Link)")
        }
    }
}