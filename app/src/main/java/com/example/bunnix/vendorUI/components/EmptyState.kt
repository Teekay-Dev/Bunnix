package com.example.bunnix.vendorUI.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.ui.theme.OrangePrimary

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon background
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = OrangePrimary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = OrangePrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun EmptyOrders(onAddProduct: () -> Unit) {
    EmptyState(
        icon = Icons.Default.ShoppingBag,
        title = "No Orders Yet",
        description = "You don't have any orders yet. Add products to start selling!",
        actionLabel = "Add Product",
        onAction = onAddProduct
    )
}

@Composable
fun EmptyMessages() {
    EmptyState(
        icon = Icons.AutoMirrored.Filled.Message,
        title = "No Messages",
        description = "Your inbox is empty. Start chatting with customers!"
    )
}

@Composable
fun EmptyProducts(onAddProduct: () -> Unit) {
    EmptyState(
        icon = Icons.Default.Inventory,
        title = "No Products",
        description = "You haven't added any products yet. Add your first product!",
        actionLabel = "Add Product",
        onAction = onAddProduct
    )
}

@Composable
fun EmptyBookings() {
    EmptyState(
        icon = Icons.Default.CalendarToday,
        title = "No Bookings",
        description = "You don't have any service bookings yet."
    )
}