package com.example.bunnix.frontend


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorProductScreen(
    vendorId: Int,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    viewModel: VendorProductViewModel = hiltViewModel()
) {
    val products by viewModel.vendorProducts.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(vendorId) {
        viewModel.loadProducts(vendorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Products") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                products.isEmpty() -> {
                    Text(
                        text = "No products yet",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            VendorProductItem(
                                product = product,
                                onEdit = { onEditProduct(product) },
                                onDelete = { viewModel.deleteProduct(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun VendorProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(product.description)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("₦${product.price}")
                Text("Qty: ${product.quantity}")

                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { onDelete() }
                )
            }
        }
    }
}
