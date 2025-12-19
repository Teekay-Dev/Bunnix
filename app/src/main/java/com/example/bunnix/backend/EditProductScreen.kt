package com.example.bunnix.backend


import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: ProductViewModel,
    product: Product,
    onBackToView: () -> Unit
) {
    var image_url by remember { mutableStateOf(Uri.parse(product.image_url)) }
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price) }
    var quantity by remember { mutableStateOf(product.quantity) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            image_url = it
        }
    }

    val cyan = Color(0xFF00FFFF)
    val purple = Color(0xFF9C27B0)
    val backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF1E1E2E)))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛠 Edit Product", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //Image Picker
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(3.dp, Brush.linearGradient(listOf(cyan, purple)), CircleShape)
                    .shadow(8.dp, CircleShape)
                    .background(Color(0xFF2A2A2A))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(image_url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            //Input Fields
            val fieldModifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))

            @Composable
            fun fancyField(value: String, label: String, onChange: (String) -> Unit) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label, color = Color.LightGray) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = fieldModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF232323),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = cyan,
                        unfocusedLabelColor = Color.LightGray,
                        focusedIndicatorColor = cyan,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = cyan
                    )
                )
            }

            fancyField(name, "Product Name") { name = it }
            fancyField(description, "Description") { description = it }
            fancyField(price, "Price") { price = it }
            fancyField(quantity, "Quantity") { quantity = it }

            //Update Product Button
            Button(
                onClick = {
                    viewModel.updateProduct(
                        product.copy(
                            image_url = image_url.toString(),
                            name = name,
                            description = description,
                            price = price,
                            quantity = quantity
                        )
                    )
                    onBackToView()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(cyan, purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Update Product",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            //Cancel Button
            TextButton(onClick = { onBackToView() }) {
                Text(
                    "Cancel & Go Back",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}