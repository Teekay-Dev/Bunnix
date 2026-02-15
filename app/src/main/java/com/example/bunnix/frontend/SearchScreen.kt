package com.example.bunnix.frontend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Service

// ✅ BACKEND INTEGRATED - UI UNCHANGED
@Composable
fun SearchScreen(
    query: String,
    products: List<Product> = emptyList(),
    services: List<Service> = emptyList(),
    onProductClick: (String) -> Unit,
    onServiceClick: (String, String) -> Unit
) {

    // ✅ Filter Products - BACKEND
    val filteredProducts = products.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
    }

    // ✅ Filter Services - BACKEND
    val filteredServices = services.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
    }

    Column(Modifier.padding(16.dp)) {

        Text(
            text = "Results for \"$query\"",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(20.dp))

        // ✅ PRODUCTS RESULTS - UI UNCHANGED
        if (filteredProducts.isNotEmpty()) {
            Text("Products", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(10.dp))

            LazyColumn {
                items(filteredProducts) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clickable {
                                onProductClick(product.productId)
                            }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("₦${product.price.toInt()}")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ✅ SERVICES RESULTS - UI UNCHANGED
        if (filteredServices.isNotEmpty()) {
            Text("Services", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(10.dp))

            filteredServices.forEach { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            onServiceClick(service.name, service.price.toString())
                        }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(service.name, fontWeight = FontWeight.Bold)
                        Text("₦${service.price.toInt()}")
                    }
                }
            }
        }

        // ✅ No Results - UI UNCHANGED
        if (filteredProducts.isEmpty() && filteredServices.isEmpty()) {
            Text("No products or services found 😢")
        }
    }
}
