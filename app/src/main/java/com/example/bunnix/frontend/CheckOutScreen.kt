package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    title: String,
    price: Int,
    onBack: () -> Unit,
    isProduct: Boolean = false,
    onContinueToPayment: (String) -> Unit
) {

    var quantity by remember { mutableStateOf(1) }

    // ✅ Total Calculations
    val subtotal = if (isProduct) price * quantity else price
    val serviceFee = (subtotal * 0.05).toInt()
    val total = subtotal + serviceFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Checkout", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ✅ Checkout Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFFFF6A00)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = if (isProduct)
                                "Product Checkout"
                            else
                                "Service Checkout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    CheckoutRow("Item", title)

                    if (isProduct) {
                        Spacer(Modifier.height(14.dp))

                        // ✅ Quantity Selector
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text("Quantity", color = Color.Gray)

                            Row(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        Color.LightGray,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- }
                                ) {
                                    Text("−", fontSize = 18.sp)
                                }

                                Text(
                                    quantity.toString(),
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { quantity++ }
                                ) {
                                    Text("+", fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Divider()

                    Spacer(Modifier.height(14.dp))
                    CheckoutRow("Subtotal", "₦$subtotal")
                    CheckoutRow("Service Fee (5%)", "₦$serviceFee")

                    Divider(Modifier.padding(vertical = 14.dp))

                    CheckoutRow(
                        "Total",
                        "₦$total",
                        isTotal = true
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // ✅ Continue Button → Payment Screen
            Button(
                onClick = {
                    onContinueToPayment("₦$total")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6A00)
                )
            ) {
                Text(
                    "Continue to Payment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CheckoutRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)

        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = if (isTotal) 18.sp else 14.sp,
            color = if (isTotal) Color(0xFFFF6A00) else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    BunnixTheme {
        CheckoutScreen(
            title = "Classic Shirt",
            price = 3500,
            isProduct = true,
            onBack = {},
            onContinueToPayment = {}
        )
    }
}
