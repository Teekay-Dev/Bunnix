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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.R
import com.example.bunnix.model.Product
import com.example.bunnix.ui.theme.BunnixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    allProducts: List<Product>,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit
) {

    var quantity by remember { mutableStateOf(1) }

    // ✅ Related Products
    val relatedProducts = allProducts.filter {
        it.category == product.category && it.id != product.id
    }

    Scaffold(
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ✅ PRODUCT IMAGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                colors = CardDefaults.cardColors(Color.LightGray)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Product Image",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ✅ MAIN DETAILS CARD
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

                    Text(
                        "₦${product.price}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6A00)
                    )

                    Spacer(Modifier.height(16.dp))

                    // ✅ Vendor + Contact
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(
                                "Vendor #${product.vendor_id}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                product.location,
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

                    // ✅ Quantity Selector
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

                    // ✅ ACTION BUTTONS
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

            // ✅ RELATED PRODUCTS
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

/* ---------------- RELATED PRODUCT ITEM ---------------- */

@Composable
fun RelatedProductItem(product: Product) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(Color.LightGray, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color.DarkGray)
            }

            Spacer(Modifier.height(10.dp))

            Text(product.name, fontWeight = FontWeight.Bold)

            Text(
                "₦${product.price}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6A00)
            )
        }
    }
}

/* ---------------- PREVIEW FIXED ---------------- */

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview() {

    val mainProduct = Product(
        id = 1,
        image_url = R.drawable.shirt,
        name = "Classic T-Shirt",
        description = "Premium cotton shirt.",
        category = "Fashion",
        price = "3500",
        vendor_id = 1,
        quantity = 50,
        location = "Lagos"
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
