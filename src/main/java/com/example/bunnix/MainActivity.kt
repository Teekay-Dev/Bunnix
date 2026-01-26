package com.example.bunnix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.frontend.ProductViewModel
import com.example.bunnix.model.Product
import com.example.bunnix.ui.theme.BunnixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BunnixTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = viewModel()
) {
    val products by productViewModel.products.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    if (products.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading products...")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductItem(product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = product.description)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "₦${product.price}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductItem() {
    BunnixTheme {
        ProductItem(
            product = Product(
                id = 1,
                imageUrl = "",
                name = "Sample Product",
                description = "Preview product description",
                category = "Test",
                price = 2500.0,
                quantity = 5,
                vendorId = 1,
                image_url = TODO(),
                vendor_id = TODO(),
                created_at = TODO()
            )
        )
    }
}
