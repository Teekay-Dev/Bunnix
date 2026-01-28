package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.model.Product

@Composable
fun FinalConfirmationScreen(
    orderedItems: List<Product>,
    onTrackOrder: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Large Orange Check Circle
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFF2711C) // Bunnix Orange
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Order Confirmed!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your vendor has been notified and will contact you shortly.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Summary Card ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF5F5F5), // Light Grey background
            border = BorderStroke(1.dp, Color(0xFF1B264F)) // Navy border
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                orderedItems.forEach { item ->
                    SummaryItemRow(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", color = Color.Gray)
                    Text("₦5,500", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "₦5,300",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFF2711C)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Action Buttons ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onTrackOrder,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C))
            ) {
                Text("Track Order", fontSize = 18.sp, color = Color.White)
            }

            Button(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Back to Home", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun SummaryItemRow(product: Product) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image Placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Img", fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("1", color = Color.Gray) // Quantity hardcoded for summary
        }

        Text("₦${product.price}", fontWeight = FontWeight.Medium)
    }
}


@Preview(showBackground = true)
@Composable
fun FinalConfirmationPreview() {
    val dummyItems = listOf(
        Product(name = "Face Cap", price = "2,000", image_url = "", description = "", category = "", location = "", quantity = "1"),
        Product(name = "Black Cap", price = "3,500", image_url = "", description = "", category = "", location = "", quantity = "1")
    )

    FinalConfirmationScreen(
        orderedItems = dummyItems,
        onTrackOrder = {},
        onBackHome = {}
    )
}