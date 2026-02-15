package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.database.models.Product  // ✅ BACKEND MODEL
import com.example.bunnix.ui.theme.BunnixTheme
import coil.compose.AsyncImage  // ✅ For backend images

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,  // ✅ BACKEND: Product type
    allProducts: List<Product>,  // ✅ BACKEND: Product type
    onAddToCart: (Product) -> Unit,  // ✅ BACKEND: Product type
    onBuyNow: (Product) -> Unit  // ✅ BACKEND: Product type
) {

    var quantity by remember { mutableStateOf(1) }

    // ✅ Related Products - BACKEND: Use productId
    val relatedProducts = allProducts.filter {
        it.category == product.category && it.productId != product.productId
    }

    Scaffold(
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ✅ PRODUCT IMAGE - UI UNCHANGED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                colors = CardDefaults.cardColors(Color.LightGray)
            ) {
                // ✅ BACKEND: Show first image from imageUrls
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrls.first(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "Product Image",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ✅ MAIN DETAILS CARD - UI UNCHANGED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(18.dp)) {

                    Text(
                        product.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        product.description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))

                    // ✅ BACKEND: price is Double, format it
                    Text(
                        "₦${product.price.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6A00)
                    )

                    Spacer(Modifier.height(16.dp))

                    // ✅ Vendor + Contact - UI UNCHANGED
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            // ✅ BACKEND: Show vendorName
                            Text(
                                product.vendorName,
                                fontWeight = FontWeight.Bold
                            )
                            // ✅ BACKEND: Show vendorId (first 8 chars)
                            Text(
                                "ID: ${product.vendorId.take(8)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .background(Color.Black, CircleShape)
                                    .size(42.dp)
                            ) {
                                Icon(Icons.Default.Call, null, tint = Color.White)
                            }

                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .background(Color.Black, CircleShape)
                                    .size(42.dp)
                            ) {
                                Icon(Icons.Default.Chat, null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ✅ Quantity Selector - UI UNCHANGED
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "Quantity",
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    Color.LightGray,
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "−",
                                fontSize = 20.sp,
                                modifier = Modifier.clickable {
                                    if (quantity > 1) quantity--
                                }
                            )

                            Spacer(Modifier.width(18.dp))

                            Text(
                                quantity.toString(),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.width(18.dp))

                            Text(
                                "+",
                                fontSize = 20.sp,
                                modifier = Modifier.clickable {
                                    quantity++
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ✅ ACTION BUTTONS - UI UNCHANGED
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            onClick = { onAddToCart(product) },
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, Color(0xFFFF6A00))
                        ) {
                            Text(
                                "Add to Cart",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6A00)
                            )
                        }

                        Button(
                            onClick = { onBuyNow(product) },
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6A00)
                            )
                        ) {
                            Text(
                                "Buy Now",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            // ✅ RELATED PRODUCTS - UI UNCHANGED
            Text(
                "Related Products",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 18.dp)
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relatedProducts) {
                    RelatedProductItem(it)
                }
            }

            Spacer(Modifier.height(50.dp))
        }
    }
}

/* ---------------- RELATED PRODUCT ITEM - UI UNCHANGED ---------------- */

@Composable
fun RelatedProductItem(product: Product) {  // ✅ BACKEND: Product type
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {

            // ✅ BACKEND: Show image from imageUrls
            if (product.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrls.first(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(Color.LightGray, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image", color = Color.DarkGray)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(product.name, fontWeight = FontWeight.Bold)

            // ✅ BACKEND: price is Double
            Text(
                "₦${product.price.toInt()}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6A00)
            )
        }
    }
}

/* ---------------- PREVIEW - BACKEND DATA ---------------- */

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview() {

    val mainProduct = Product(
        productId = "1",
        vendorId = "vendor123",
        vendorName = "Fashion Store",
        name = "Classic T-Shirt",
        description = "Premium cotton shirt.",
        price = 3500.0,
        discountPrice = null,
        category = "Fashion",
        imageUrls = emptyList(),
        variants = emptyList(),
        totalStock = 50,
        inStock = true,
        tags = emptyList(),
        views = 0,
        sold = 0,
        createdAt = null,
        updatedAt = null
    )

    val allProducts = listOf(mainProduct)

    BunnixTheme {
        ProductDetailsScreen(
            product = mainProduct,
            allProducts = allProducts,
            onAddToCart = {},
            onBuyNow = {}
        )
    }
}
