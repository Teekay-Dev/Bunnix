package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    total: String,
    onBack: () -> Unit,
    onPaySuccess: () -> Unit
) {

    var selectedMethod by remember { mutableStateOf("Mobile Money") }

    val methods = listOf(
        "Credit/Debit Card",
        "Bank Transfer",
        "Mobile Money"
    )

    Scaffold(
        containerColor = Color(0xFFF9F9F9),

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
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(18.dp)
        ) {

            Text(
                "Select Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                "Choose how you want to pay securely.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            methods.forEach { method ->

                val isSelected = selectedMethod == method

                val icon = when (method) {
                    "Mobile Money" -> Icons.Default.PhonelinkRing
                    "Bank Transfer" -> Icons.Default.AccountBalance
                    else -> Icons.Default.CreditCard
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selectedMethod = method },

                    shape = RoundedCornerShape(18.dp),

                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected)
                            Color(0xFFFF6A00)
                        else Color(0xFFE0E0E0)
                    ),

                    color = if (isSelected)
                        Color(0xFFFFF3EC)
                    else Color.White
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected)
                                Color(0xFFFF6A00)
                            else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                method,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Text(
                                "Fast and secure payment option",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2D5BD0),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    Text(
                        "Payment Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentSummaryRow("Subtotal", total)
                    PaymentSummaryRow("Service Fee", "₦500")

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    PaymentSummaryRow("Total", total, isTotal = true)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPaySuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6A00)
                )
            ) {
                Text(
                    "Pay $total Now",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Payments are encrypted and secure.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PaymentSummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = if (isTotal) Color.Black else Color.Gray,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = if (isTotal) Color(0xFFFF6A00) else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentPreview() {
    BunnixTheme {
        PaymentMethodScreen(
            total = "₦75,000",
            onBack = {},
            onPaySuccess = {}
        )
    }
}
