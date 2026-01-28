package com.example.bunnix.frontend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddBookingScreen(navController: NavController) {
    var customerName by remember { mutableStateOf("") }
    var serviceName by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Schedule New Service", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        BunnixTextField(value = customerName, onValueChange = { customerName = it }, label = "Customer Name")
        Spacer(Modifier.height(12.dp))
        BunnixTextField(value = serviceName, onValueChange = { serviceName = it }, label = "Service Type")
        Spacer(Modifier.height(12.dp))
        BunnixTextField(value = dateTime, onValueChange = { dateTime = it }, label = "Date & Time (e.g., Dec 7, 2:00 PM)")
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Booking", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }