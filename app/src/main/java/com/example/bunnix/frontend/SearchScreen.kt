package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.ui.theme.OrangePrimary

private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun SearchScreen(
    query: String,
    products: List<Product> = emptyList(),
    services: List<Service> = emptyList(),
    vendors: List<VendorProfile> = emptyList(),
    onProductClick: (String) -> Unit,
    onServiceClick: (String, String) -> Unit,
    onVendorClick: (String) -> Unit
) {
    // Filter Logic
    val filteredProducts = products.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
    }

    val filteredServices = services.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }

    val filteredVendors = vendors.filter {
        it.businessName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
    }

    val hasResults = filteredProducts.isNotEmpty() || filteredServices.isNotEmpty() || filteredVendors.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Results for \"$query\"",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(20.dp))
        }

        // VENDORS
        if (filteredVendors.isNotEmpty()) {
            item {
                SectionHeader(title = "Vendors", icon = Icons.Default.Store)
                Spacer(Modifier.height(10.dp))
            }

            items(filteredVendors) { vendor ->
                SearchResultCard(
                    title = vendor.businessName,
                    subtitle = vendor.category,
                    imageUrl = vendor.coverPhotoUrl,
                    onClick = { onVendorClick(vendor.vendorId) }
                )
                Spacer(Modifier.height(10.dp))
            }
            item { Spacer(Modifier.height(10.dp)) }
        }

        // PRODUCTS
        if (filteredProducts.isNotEmpty()) {
            item {
                SectionHeader(title = "Products", icon = Icons.Default.ShoppingBag)
                Spacer(Modifier.height(10.dp))
            }

            items(filteredProducts) { product ->
                SearchResultCard(
                    title = product.name,
                    subtitle = "₦${product.price.toInt()}",
                    imageUrl = product.imageUrls.firstOrNull() ?: "",
                    onClick = { onProductClick(product.productId) }
                )
                Spacer(Modifier.height(10.dp))
            }
            item { Spacer(Modifier.height(10.dp)) }
        }

        // SERVICES
        if (filteredServices.isNotEmpty()) {
            item {
                SectionHeader(title = "Services", icon = Icons.Default.MiscellaneousServices)
                Spacer(Modifier.height(10.dp))
            }

            items(filteredServices) { service ->
                SearchResultCard(
                    title = service.name,
                    subtitle = "₦${service.price.toInt()}",
                    imageUrl = service.imageUrl,
                    onClick = { onServiceClick(service.serviceId, service.price.toString()) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        // No Results
        if (!hasResults) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No results found", color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text("Try a different keyword", color = TextSecondary.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SearchResultCard(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceLight
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, null, tint = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text(subtitle, color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

// Simple AsyncImage wrapper
@Composable
private fun AsyncImage(model: Any, contentDescription: String?, modifier: Modifier, contentScale: ContentScale) {
    coil.compose.AsyncImage(model = model, contentDescription = contentDescription, modifier = modifier, contentScale = contentScale)
}