package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme

@Composable
fun OrderPlacedScreen(
    onTrackOrder: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF9F9F9)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ Success Icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFFF6A00),
                                Color(0xFFFF9500)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ✅ Title
            Text(
                text = "Order Placed Successfully 🎉",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ✅ Subtitle
            Text(
                text = "Your payment was successful.\nYour order is now being processed.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ✅ Track Order Button
            Button(
                onClick = onTrackOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6A00)
                )
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Track Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ✅ Continue Shopping Button
            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(18.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFFF6A00),
                            Color(0xFFFF9500)
                        )
                    )
                )
            ) {
                Text(
                    "Continue Shopping",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF6A00)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderPreview() {
    BunnixTheme {
        OrderPlacedScreen(
            onTrackOrder = {},
            onContinueShopping = {}
        )
    }
}
