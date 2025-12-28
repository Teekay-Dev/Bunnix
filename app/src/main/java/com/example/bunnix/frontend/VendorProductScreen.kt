package com.example.bunnix.frontend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.backend.VendorViewModel
import com.example.bunnix.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorProductScreen(
    vendorId: Int,
    viewModel: VendorViewModel = hiltViewModel(),
    onEditProduct: (Product) -> Unit
) {
    val products by viewModel.vendorProducts.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(vendorId) {
        viewModel.loadProducts(vendorId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Products") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEditProduct(Product(
                    image_url = "",
                    name = "",
                    description = "",
                    category = "",
                    price = "",
                    vendor_id = vendorId,
                    quantity = ""
                ))
            }) {
                Text("+")
            }
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditProduct(product) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(product.description)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Price: ${product.price}")
                                Text("Qty: ${product.quantity}")
                                Text(
                                    "Delete",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.clickable {
                                        viewModel.deleteProduct(product)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
