package com.example.bunnix.frontend

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.BunnixTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceName = intent.getStringExtra("service") ?: "Service"
        val price = intent.getStringExtra("price") ?: "$0"

        setContent {
            BookingScreen(
                serviceName = serviceName,
                price = price,
                onContinue = {
                    // Later: go to Payment Screen
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    serviceName: String,
    price: String,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("Dec 8") }
    var selectedTime by remember { mutableStateOf("10:00 AM") }

    val dates = listOf("Dec 7", "Dec 8", "Dec 9", "Dec 10", "Dec 11")
    val times = listOf(
        "09:00 AM", "10:00 AM", "11:00 AM",
        "01:00 PM", "02:00 PM", "03:00 PM"
    )

    Scaffold(

        // ✅ TOP BAR FIXED
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Book Service", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as Activity).finish()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },

        // ✅ BUTTON NOW WORKS
        bottomBar = {
            Button(
                onClick = { onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE44F26)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Continue to Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // ✅ SERVICE SUMMARY CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        price,
                        color = Color(0xFF2D5BD0),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ DATE SELECT
            Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Row(
                Modifier
                    .padding(vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                dates.forEach { date ->
                    FilterChip(
                        selected = selectedDate == date,
                        onClick = { selectedDate = date },
                        label = { Text(date) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ TIME SELECT
            Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 12.dp)
            ) {
                items(times) { time ->
                    val isSelected = selectedTime == time

                    Surface(
                        modifier = Modifier.clickable {
                            selectedTime = time
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected)
                            Color(0xFFE44F26)
                        else Color.White,
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Box(
                            Modifier.padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                time,
                                color = if (isSelected)
                                    Color.White
                                else Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingPreview() {
    BunnixTheme {
        BookingScreen(
            serviceName = "Personal Styling",
            price = "$75",
            onContinue = {}
        )
    }
}
