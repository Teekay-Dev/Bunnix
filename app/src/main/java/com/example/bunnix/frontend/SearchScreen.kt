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
import com.example.bunnix.data.ProductData

@Composable
fun SearchScreen(
    query: String,
    onProductClick: (Int) -> Unit,
    onServiceClick: (String, String) -> Unit
) {

    // ✅ Filter Products
    val filteredProducts = ProductData.products.filter {
        it.name.contains(query, ignoreCase = true)
    }

    // ✅ Fake Services List (replace with yours)
    val services = listOf(
        "Hair Styling" to "5000",
        "Makeup Booking" to "8000",
        "Photography" to "12000"
    )

    val filteredServices = services.filter {
        it.first.contains(query, ignoreCase = true)
    }

    Column(Modifier.padding(16.dp)) {

        Text(
            text = "Results for \"$query\"",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(20.dp))

        // ✅ PRODUCTS RESULTS
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
                                onProductClick(product.id)
                            }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("₦${product.price}")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ✅ SERVICES RESULTS
        if (filteredServices.isNotEmpty()) {
            Text("Services", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(10.dp))

            filteredServices.forEach { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            onServiceClick(service.first, service.second)
                        }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(service.first, fontWeight = FontWeight.Bold)
                        Text("₦${service.second}")
                    }
                }
            }
        }

        // ✅ No Results
        if (filteredProducts.isEmpty() && filteredServices.isEmpty()) {
            Text("No products or services found 😢")
        }
    }
}
