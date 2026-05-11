package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bunnix.ui.theme.OrangePrimaryModern
import com.example.bunnix.vendorUI.components.BunnixTopBar
import com.example.bunnix.viewmodel.OrdersViewModel

@Composable
fun BookingDetailScreen(navController: NavController, bookingId: String, viewModel: OrdersViewModel = hiltViewModel()) {
    val bookings by viewModel.serviceBookings.collectAsState()
    val booking = bookings.find { it.bookingId == bookingId }

    Scaffold(topBar = { BunnixTopBar("Booking Details", { navController.navigateUp() }) }, containerColor = Color(0xFFF8F9FE)) { padding ->
        if (booking == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), Arrangement.spacedBy(16.dp)) {
                Card(colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Booking #${booking.bookingNumber}", fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(Modifier.height(8.dp))
                        Text("Customer: ${booking.customerName}", color = Color.DarkGray)
                        Text("Date: ${booking.bookingDate}", color = Color.DarkGray)
                        Text("Price: ₦${booking.amount.toInt()}", color = Color.DarkGray)
                        Text("Status: ${booking.status}", color = Color.DarkGray)
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    if (booking.status == "Confirmed") {
                        Button({ viewModel.markCompleted(bookingId); navController.navigateUp() }, Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(OrangePrimaryModern)) { Text("Complete") }
                    } else if (booking.status == "Requested" || booking.status == "Booking Requested") {
                        Button({ viewModel.acceptBooking(bookingId); navController.navigateUp() }, Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(OrangePrimaryModern)) { Text("Accept") }
                        OutlinedButton({ viewModel.declineBooking(bookingId); navController.navigateUp() }, Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Decline") }
                    }
                }
            }
        }
    }
}