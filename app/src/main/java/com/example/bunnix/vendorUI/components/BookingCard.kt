package com.example.bunnix.vendorUI.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bunnix.ui.theme.SuccessGreen
import com.example.bunnix.ui.theme.WarningYellow

@SuppressLint("DefaultLocale")
@Composable
fun BookingCard(
    bookingNumber: String,
    customerName: String,
    serviceName: String,
    price: Double,
    status: String,
    date: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BookingStatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "$date at $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    bookingNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "₦${String.format("%,.2f", price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BookingStatusBadge(status: String) {
    val (color, containerColor) = when (status.lowercase()) {
        "requested" -> Pair(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        "confirmed" -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        "in progress" -> Pair(WarningYellow, WarningYellow.copy(alpha = 0.1f))
        "completed" -> Pair(SuccessGreen, SuccessGreen.copy(alpha = 0.1f))
        "cancelled" -> Pair(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surfaceVariant)
        else -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}