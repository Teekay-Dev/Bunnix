<<<<<<< HEAD

=======
package com.example.bunnix.frontend


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.model.Product
import com.example.bunnix.model.VendorViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductScreen(navController: NavController, viewModel: VendorViewModel = viewModel(), productId: String? = null) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }



    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(productId) {
        if (productId != null) {
            val product = viewModel.vendorProducts.value.find { it.id.toString() == productId }
            product?.let {
                productName = it.name
                productPrice = it.price.toString() // Or clean digits for your formatter
                productDescription = it.description
                productCategory = it.category
                // imageUri = it.image_url // You'll handle URI vs URL logic here
            }
        }
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(if (productId == null) "Add New Product" else "Edit Product", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- IMAGE UPLOAD BOX ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(16.dp))
                    .clickable { galleryLauncher.launch("image/*") }, // Opens the gallery
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    // Display the selected image using Coil
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Product Image",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray)
                        Text("Tap to Upload Product Image", color = Color.Gray)
                    }
                }
            }

            // --- INPUT FIELDS ---
            BunnixTextField(value = productName, onValueChange = { productName = it }, label = "Product Name")
            BunnixTextField(
                value = if (productPrice.isEmpty()) "" else formatNaira(productPrice),
                onValueChange = { newValue ->
                    // We only want to save the actual numbers (digits) to the variable
                    val cleanNumber = newValue.filter { it.isDigit() }
                    if (cleanNumber.length <= 9) {
                        productPrice = cleanNumber
                    }
                },
                label = "Price",
                isNumber = true
            )
            BunnixTextField(value = productCategory, onValueChange = { productCategory = it }, label = "Category")
            BunnixTextField(value = productDescription, onValueChange = { productDescription = it }, label = "Description", isSingleLine = false)

            Spacer(modifier = Modifier.weight(1f))

            // --- SAVE BUTTON ---
            Button(
                onClick = {
                    if (productName.isNotBlank() && productPrice.isNotBlank()) {
                        scope.launch {
                            isLoading = true // 1. Start loading immediately

                            // 2. Upload to Supabase Storage first to get the URL
                            val finalImageUrl = if (imageUri != null) {
                                viewModel.uploadImage(imageUri!!, context)
                            } else ""

                            // 3. Create the Product object NOW (so it's available for use)
                            val productData = Product(
                                id = productId?.toIntOrNull() ?: 0, // Set ID if editing
                                name = productName,
                                price = productPrice.toDoubleOrNull() ?: 0.0,
                                image_url = finalImageUrl ?: "",
                                description = productDescription,
                                category = productCategory,
                                location = "Vendor Shop",
                                quantity = "1",
                                vendor_id = ""
                            )//red here

                            // 4. Decide whether to Update or Save
                            if (productId == null) {
                                viewModel.saveProduct(productData)
                            } else {
                                viewModel.updateProduct(productId, productData)
                            }

                            isLoading = false
                            navController.popBackStack()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (productId == null) "Upload Product" else "Update Product",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BunnixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isNumber: Boolean = false,
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = isSingleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFF2711C),
            focusedLabelColor = Color(0xFFF2711C)
        )
    )
}

fun formatNaira(input: String): String {
    // Remove everything that isn't a digit
    val digits = input.filter { it.isDigit() }
    if (digits.isEmpty()) return ""

    // Convert to Long to handle large numbers, then format with commas
    val number = digits.toLong()
    val formatted = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(number)

    return "₦$formatted"
}
>>>>>>> 3e8a2de235349208f7d0ce387a237c0a485cf30a
