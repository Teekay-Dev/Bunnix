package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.OrangePrimary

@Composable
fun PhoneVerificationScreen(
    phoneNumber: String,
    isVerifying: Boolean = false, // NEW: Show loading state
    errorMessage: String? = null, // NEW: Show error
    onVerifyClick: (String) -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Verify Phone Number",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = OrangePrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter the SMS code sent to",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Text(
            text = phoneNumber,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Show error if any
        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otpCode = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("SMS OTP Code") },
            placeholder = { Text("000000", color = Color.LightGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = OrangePrimary,
                cursorColor = OrangePrimary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true,
            enabled = !isVerifying // Disable during verification
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Verify Button with loading state
        Button(
            onClick = { onVerifyClick(otpCode) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            enabled = otpCode.length == 6 && !isVerifying // Disable if not 6 digits or verifying
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Verify Phone", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Didn't get the SMS? ", color = Color.Gray)
            TextButton(
                onClick = onResendClick,
                enabled = !isVerifying // Disable during verification
            ) {
                Text("Resend", color = OrangePrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onBackClick,
            enabled = !isVerifying // Disable during verification
        ) {
            Text("Back", color = Color.Gray)
        }
    }
}