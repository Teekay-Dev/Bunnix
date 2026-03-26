package com.example.bunnix.vendorUI.screens.vendor.orders

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.*
import com.example.bunnix.vendorUI.components.*
import com.example.bunnix.vendorUI.navigation.VendorRoutes
import com.example.bunnix.viewmodel.OrdersViewModel
//
//@Composable
//fun ServiceBookingsScreen(
//    navController: NavController,
//    viewModel: OrdersViewModel = hiltViewModel()
//) {
//    val bookings by viewModel.serviceBookings.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val successMessage by viewModel.successMessage.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadServiceBookings()
//    }
//
//    // Show success message
//    LaunchedEffect(successMessage) {
//        successMessage?.let {
//            viewModel.clearMessages()
//        }
//    }
//
//    // Show error message
//    LaunchedEffect(error) {
//        error?.let {
//            viewModel.clearMessages()
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        if (isLoading && bookings.isEmpty()) {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(5) {
//                    ShimmerOrderCard()
//                }
//            }
//        } else if (bookings.isEmpty()) {
//            EmptyState(
//                icon = Icons.Default.CalendarToday,
//                title = "No Service Bookings",
//                message = "When customers book your services,\nthey will appear here"
//            )
//        } else {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(bookings) { booking ->
//                    ServiceBookingCard(
//                        booking = booking,
//                        viewModel = viewModel,
//                        onClick = {
//                            navController.navigate(VendorRoutes.bookingDetail(booking.bookingId))
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun ServiceBookingCard(
    booking: ServiceBooking,
    viewModel: OrdersViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Customer Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (booking.customerImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = booking.customerImageUrl,
                        contentDescription = "Customer",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Customer",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Booking Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Booking Number & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.bookingNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    BookingStatusBadge(status = booking.status)
                }

                // Customer Name
                Text(
                    text = booking.customerName,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                // Booking Date
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = booking.bookingDate,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Service Name
                Text(
                    text = "Service:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "• ${booking.serviceName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₦${String.format("%,.2f", booking.amount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryModern
                    )

                    // ✅ WORKING ACTION BUTTONS
                    if (booking.status.lowercase() == "pending") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.declineBooking(booking.bookingId)
                                },
                                modifier = Modifier.height(36.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFF44336)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFF44336)),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text("Decline", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.acceptBooking(booking.bookingId)
                                },
                                modifier = Modifier.height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OrangePrimaryModern
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text("Accept", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0) to OrangePrimaryModern
        "confirmed" -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        "in progress" -> Color(0xFFF3E5F5) to Color(0xFF9C27B0)
        "completed" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        "declined" -> Color(0xFFFFEBEE) to Color(0xFFF44336)
        else -> Color.LightGray to Color.DarkGray
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// Data Class
data class ServiceBooking(
    val bookingId: String,
    val bookingNumber: String,
    val customerName: String,
    val customerImageUrl: String,
    val bookingDate: String,
    val serviceName: String,
    val amount: Double,
    val status: String
)