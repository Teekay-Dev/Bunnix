package com.example.bunnix.frontend


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bunnix.model.Product
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.backend.VendorViewModel

@Composable
fun VendorAddEditProductScreen(
    product: Product,
    viewModel: VendorViewModel = hiltViewModel(),
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price) }
    var quantity by remember { mutableStateOf(product.quantity) }
    var category by remember { mutableStateOf(product.category) }
    var imageUrl by remember { mutableStateOf(product.image_url) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })

        Button(onClick = {
            val newProduct = product.copy(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                category = category,
                image_url = imageUrl
            )

            if (product.id == 0) {
                viewModel.addProduct(newProduct) { onSaved() }
            } else {
                viewModel.updateProduct(newProduct) { onSaved() }
            }
        }) {
            Text("Save")
        }
    }
}
