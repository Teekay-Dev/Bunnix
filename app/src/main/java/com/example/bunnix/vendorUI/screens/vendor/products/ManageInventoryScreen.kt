package com.example.bunnix.vendorUI.screens.vendor.products

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.WarningYellow
import com.example.bunnix.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageInventoryScreen(
    onEditProduct: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "In Stock", "Low Stock", "Out of Stock")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Inventory") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search products...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )

            // Filter Chips
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                indicator = { }
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Stats Summary
            InventoryStatsCard()

            // Product List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleInventoryProducts) { product ->
                    InventoryProductCard(
                        product = product,
                        onEdit = { onEditProduct(product.id) },
                        onStockUpdate = { /* Update stock */ }
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryStatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InventoryStatItem("Total", "24", MaterialTheme.colorScheme.primary)
            InventoryStatItem("In Stock", "18", SuccessGreen)
            InventoryStatItem("Low Stock", "4", WarningYellow)
            InventoryStatItem("Out", "2", MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun InventoryStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryProductCard(
    product: InventoryProduct,
    onEdit: () -> Unit,
    onStockUpdate: () -> Unit
) {
    val stockStatus = when {
        product.stock == 0 -> "Out of Stock" to MaterialTheme.colorScheme.error
        product.stock < 5 -> "Low Stock" to WarningYellow
        else -> "In Stock" to SuccessGreen
    }

    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    "₦${String.format("%,.2f", product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = stockStatus.second.copy(alpha = 0.1f)
                ) {
                    Text(
                        stockStatus.first,
                        style = MaterialTheme.typography.labelSmall,
                        color = stockStatus.second,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${product.stock} units",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onStockUpdate) {
                    Text("Update")
                }
            }
        }
    }
}

data class InventoryProduct(
    val id: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String
)

val sampleInventoryProducts = listOf(
    InventoryProduct("1", "iPhone 15 Pro", 1200000.0, 5, "https://via.placeholder.com/150"),
    InventoryProduct("2", "Samsung Galaxy S24", 950000.0, 0, "https://via.placeholder.com/150"),
    InventoryProduct("3", "AirPods Pro 2", 250000.0, 3, "https://via.placeholder.com/150"),
    InventoryProduct("4", "iPad Air", 600000.0, 12, "https://via.placeholder.com/150"),
    InventoryProduct("5", "MacBook Pro", 1500000.0, 2, "https://via.placeholder.com/150")
)