package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.models.Product  // ✅ BACKEND MODEL
import com.example.bunnix.ui.theme.OrangeEnd
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage  // ✅ For backend images

// ✅ Product Card Item - UI UNCHANGED
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ✅ BACKEND: Show first image from imageUrls
            if (product.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrls.first(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback placeholder
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No\nImage", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)

                // ✅ BACKEND: price is Double, format it
                Text(
                    "₦${product.price.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = OrangeEnd
                )

                // ✅ BACKEND: Show vendorName instead of location
                Text(
                    product.vendorName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ✅ Product List Screen - UI UNCHANGED
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    products: List<Product> = emptyList(),  // ✅ BACKEND: Accept products
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit  // ✅ BACKEND: Product type
) {

    var searchQuery by remember { mutableStateOf("") }

    // ✅ Filter Products by Search
    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {

                // ✅ Top AppBar - UNCHANGED
                TopAppBar(
                    title = { Text("All Products") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                )

                // ✅ Search Bar - UNCHANGED
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F8F8)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ✅ Show filtered products
            items(filteredProducts) { product ->
                ProductItem(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}


// ✅ Preview - UNCHANGED
@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    ProductListScreen(
        products = emptyList(),
        onBack = {},
        onProductClick = {}
    )
}
