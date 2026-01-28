package com.example.bunnix.frontend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OrderConfirmedScreen(
    onViewReceipt: () -> Unit,
    onContinueShopping: () -> Unit
) {

//    onViewReceipt = {
//        // Logic to open receipt PDF or Screen
//        println("User clicked View Receipt")
//    },
//    onContinueShopping = {
//        // Logic to navigate back to the home/store screen
//        println("User going back to shop")
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Black Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Confirmed",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Large Green Check Circle
        Surface(
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = Color(0xFF00B14F) // Success Green
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.padding(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Main Text
        Text(
            text = "Thank you for\nyour purchase!",
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes buttons to the bottom area

        // Buttons Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // View Receipt Button (Filled)
            Button(
                onClick = onViewReceipt,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C))
            ) {
                Text(text = "View Receipt", fontSize = 18.sp, color = Color.White)
            }

            // Continue Shopping Button (Outlined)
            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF1B264F))
            ) {
                Text(text = "Continue Shopping", fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun BunnixOrderFlow() {
    // 1. Create a state to control the popup visibility
    var showRatingPopup by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // The screen we built earlier
        OrderConfirmedScreen(
            onViewReceipt = {
                // Show the popup when they click View Receipt
                showRatingPopup = true
            },
            onContinueShopping = { /* Navigate back */ }
        )

        // 2. Conditionally show the popup
        if (showRatingPopup) {
            RateOurAppDialog(
                onDismiss = { showRatingPopup = false },
                onSubmit = { finalRating, text ->
                    println("Bunnix Rating: $finalRating stars, Review: $text")
                    showRatingPopup = false // Close after submitting
                }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun OrderConfirmedPreview() {
    // We provide empty braces {} for the clicks so the preview just displays the UI
    OrderConfirmedScreen(
        onViewReceipt = {},
        onContinueShopping = {}
    )
}