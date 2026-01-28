package com.example.bunnix.frontend
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    relatedProducts: List<Product> // Added this to handle your relating products
) {
    var orderQuantity by remember { mutableIntStateOf(1) }
    var sizeExpanded by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf("M") }
    val availableSizes = listOf("S", "M", "L", "XL")

    Scaffold(
        bottomBar = { /* Your BunnixBottomNav() here */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // This allows the whole page to scroll
        ) {
            // --- IMAGE SECTION ---
            Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.White), contentAlignment = Alignment.Center) {
                Text("Image: ${product.image_url}")
            }

            // --- INFO SECTION (The Grey Box) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9D9D9))
                    .padding(16.dp)
                    .border(1.dp, Color(0xFF1B264F))
            ) {
                Text(text = product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = product.description, fontSize = 16.sp)
                Text(text = "Category: ${product.category}", fontSize = 14.sp, color = Color.Gray)
                Text(text = "₦${product.price}", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // Vendor & Contact Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    VendorCard(vendorId = product.vendor_id, location = product.location)
                    ContactActions()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- SIZE & QUANTITY CONTROLS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { sizeExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Size: $selectedSize ▾", color = Color.Black)
                        }
                        DropdownMenu(expanded = sizeExpanded, onDismissRequest = { sizeExpanded = false }) {
                            availableSizes.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text(size) },
                                    onClick = { selectedSize = size; sizeExpanded = false }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.weight(1f).height(40.dp).border(1.dp, Color.Black),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "—", modifier = Modifier.clickable { if (orderQuantity > 1) orderQuantity-- })
                        Text(text = "$orderQuantity", fontWeight = FontWeight.Bold)
                        Text(text = "+", modifier = Modifier.clickable { orderQuantity++ })
                    }

                    Button(
                        onClick = { /* Add to Cart Logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Add To Cart", fontSize = 12.sp)
                    }
                }
            }

            // --- RELATING PRODUCTS SECTION ---
            // This appears when the user scrolls up past the grey box
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Relating Products",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relatedProducts) { relatedItem ->
                    RelatedProductItem(relatedItem)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Other Things can come after",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- HELPER COMPONENTS TO FIX RED LINES ---

@Composable
fun VendorCard(vendorId: Int, location: String) {
    Column(
        modifier = Modifier
          //  .weight(1f)
            .background(Color.White.copy(alpha = 0.5f)).border(1.dp, Color.Black).padding(8.dp)
    ) {
        Text("Vendor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(Color.Gray))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("ID: $vendorId", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(location, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ContactActions() {
    Column(modifier = Modifier.width(100.dp).padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(0.dp), modifier = Modifier.height(35.dp)) {
            Icon(Icons.Default.Call, null, modifier = Modifier.size(14.dp))
            Text(" Call", fontSize = 11.sp)
        }
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(0.dp), modifier = Modifier.height(35.dp)) {
            Icon(Icons.Default.Chat, null, modifier = Modifier.size(14.dp))
            Text(" Chat", fontSize = 11.sp)
        }
    }
}

@Composable
fun RelatedProductItem(product: Product) {
    Card(
        modifier = Modifier.width(150.dp).border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF0F0F0))) // Placeholder for image
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text("₦${product.price}", color = Color(0xFFF2711C), fontSize = 13.sp)
        }
    }
}

//@Composable
//fun BunnixBottomNav() {
//    NavigationBar(containerColor = Color.White, modifier = Modifier.border(1.dp, Color.Black)) {
//        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
//        NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, null) }, label = { Text("Cart") }, selected = false, onClick = {})
//        NavigationBarItem(icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chat") }, selected = false, onClick = {})
//        NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = false, onClick = {})
//        NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = false, onClick = {})
//    }
//}

@Preview(showBackground = true)
@Composable
fun ProductDetailsWithRelatedPreview() {
    // 1. Create the main product based on your Room Model
    val mainProduct = Product(
        id = 1,
        image_url = "yellow_tshirt_url",
        name = "Classic T-Shirt",
        description = "Premium cotton yellow t-shirt with custom graphic. Comfortable for daily use.",
        category = "Fashion",
        price = "3500",
        vendor_id = 55,
        quantity = "100",
        location = "Lagos, Nigeria"
    )

    // 2. Create a list of related products to show at the bottom
    val relatedList = listOf(
        Product(
            id = 2,
            image_url = "item2",
            name = "Blue Jean",
            category = "Fashion",
            price = "5000",
            description = "",
            quantity = "10",
            location = "Lagos"
        ),
        Product(
            id = 3,
            image_url = "item3",
            name = "Sneakers",
            category = "Shoes",
            price = "12000",
            description = "",
            quantity = "5",
            location = "Abuja"
        ),
        Product(
            id = 4,
            image_url = "item4",
            name = "Wristwatch",
            category = "Accessories",
            price = "8500",
            description = "",
            quantity = "20",
            location = "Ibadan"
        ),
        Product(
            id = 5,
            image_url = "item5",
            name = "Sun Glasses",
            category = "Accessories",
            price = "2500",
            description = "",
            quantity = "15",
            location = "Lagos"
        )
    )

    // 3. Render the screen
    ProductDetailsScreen(
        product = mainProduct,
        relatedProducts = relatedList
    )
}