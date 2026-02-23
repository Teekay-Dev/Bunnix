package com.example.bunnix.frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.bunnix.R

@Composable
fun ShoppingCartScreen() {
    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) }
                Text("Shopping Cart (1)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Clear All", color = Color.Red, fontSize = 14.sp)
            }
        },
        bottomBar = {
            Column(Modifier.background(Color.White).padding(16.dp)) {
                CartSummaryRow("Subtotal", "$18.99")
                CartSummaryRow("Delivery Fee", "$5.00")
                CartSummaryRow("Service Fee", "$2.00")
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("$25.99", color = Color(0xFFD35400), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* Navigate to Checkout */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F8F8))) {
            // Cart Item Card
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(R.drawable.dress), // Placeholder for your gourmet burger
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gourmet Burger", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Icon(Icons.Default.Delete, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        Text("Gourmet Bites", color = Color.Gray, fontSize = 12.sp)
                        Text("$18.99", color = Color(0xFFD35400), fontWeight = FontWeight.Bold)

                        // Quantity Stepper
                        Row(
                            Modifier.padding(top = 8.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("-", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            Text("1", fontWeight = FontWeight.Bold)
                            Text("+", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, name = "1. Shopping Cart")
@Composable
fun PreviewCart() {
    MaterialTheme { ShoppingCartScreen() }
}