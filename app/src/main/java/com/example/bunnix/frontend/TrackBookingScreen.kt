package com.example.bunnix.frontend


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.firebase.collections.BookingCollection
import com.example.bunnix.database.models.Booking
import java.text.SimpleDateFormat
import java.util.*

private val OrangePrimary = Color(0xFFFF6B35)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF10B981)
private val TealAccent = Color(0xFF2EC4B6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackBookingScreen(
    bookingId: String,
    onBack: () -> Unit
) {
    // ✅ Observe Real-time Booking Data
    val booking by BookingCollection.getBookingByIdFlow(bookingId)
        .collectAsState(initial = null)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Track Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (booking == null) {
                CircularProgressIndicator(color = OrangePrimary)
            } else {
                BookingTrackingContent(booking = booking!!)
            }
        }
    }
}

@Composable
fun BookingTrackingContent(booking: Booking) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (booking.status) {
                        "Completed" -> Icons.Default.CheckCircle
                        "Cancelled" -> Icons.Default.Cancel
                        "Confirmed" -> Icons.Default.Check
                        else -> Icons.Default.Schedule
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = booking.status,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Booking #${booking.bookingNumber}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BookingDetailRow("Service", booking.serviceName)
                BookingDetailRow("Vendor", booking.vendorName)
                BookingDetailRow(
                    label = "Date",
                    value = booking.scheduledDate?.toDate()?.let {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                    } ?: "N/A"
                )
                BookingDetailRow("Time", booking.scheduledTime)
                BookingDetailRow("Payment", booking.paymentMethod)

                if (booking.customerNotes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Notes:", fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(booking.customerNotes, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary)
        Text(value, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}