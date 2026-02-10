package com.example.bunnix.frontend


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.model.*
import com.example.bunnix.ui.theme.BunnixTheme
import com.example.bunnix.R

@Composable
fun VendorDetailScreen(
    vendor: Vendor,
    onBack: () -> Unit,
    onBookService: (String, String) -> Unit,
    onViewProducts: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6)),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // ✅ HEADER IMAGE
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    Image(
                        painter = painterResource(vendor.coverImage),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // ✅ BACK BUTTON
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    // ✅ CHAT BUTTON
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat")
                    }
                }
            }

            // ✅ INFO CARD
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .offset(y = (-40).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(vendor.logoImage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                vendor.businessName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Text(vendor.category, color = Color.Gray)

                            Spacer(Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    "${vendor.rating} (${vendor.reviewCount})",
                                    fontSize = 13.sp
                                )

                                Spacer(Modifier.width(10.dp))

                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    vendor.distance,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // ✅ ABOUT SECTION
            item {
                Section("About") {
                    Text(
                        vendor.about,
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }
            }

            // ✅ SERVICES SECTION (Dynamic)
            if (vendor.services.isNotEmpty()) {
                item {
                    Section("Services") {
                        vendor.services.forEach { service ->
                            ServiceCard(service, onBookService)
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }

            // ✅ PRODUCTS SECTION (Dynamic)
            if (vendor.products.isNotEmpty()) {
                item {
                    Section("Products") {
                        vendor.products.forEach { product ->
                            ProductCard(product)
                            Spacer(Modifier.height(14.dp))
                        }

                        Button(
                            onClick = onViewProducts,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("View All Products")
                        }
                    }
                }
            }

            // ✅ REVIEWS SECTION (Dynamic)
            if (vendor.reviews.isNotEmpty()) {
                item {
                    Section("Reviews") {
                        vendor.reviews.forEachIndexed { index, review ->
                            ReviewCard(review)

                            if (index != vendor.reviews.lastIndex) {
                                Divider(
                                    Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




// --- Updated to accept ServiceItem Object ---
@Composable
fun ServiceCard(
    item: ServiceItem,
    onBookNow: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(item.title, fontWeight = FontWeight.Bold)

            Text(item.description, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Text("Duration: ${item.duration}")

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(item.price, fontWeight = FontWeight.Bold)

                Button(
                    onClick = {
                        onBookNow(item.title, item.price)
                    }
                ) {
                    Text("Book")
                }
            }
        }
    }
}


// --- Updated to accept ProductItem Object ---
@Composable
fun ProductCard(item: ProductItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {

                Text(item.title, fontWeight = FontWeight.Bold)

                Text(item.description, color = Color.Gray)

                Text(item.price, fontWeight = FontWeight.Bold)
            }

            Button(onClick = {}) {
                Text("Add")
            }
        }
    }
}


@Composable
fun ReviewCard(review: ReviewItem) {
    Column {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(review.userName, fontWeight = FontWeight.Bold)

            Row {
                repeat(review.rating) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(review.comment)

        Text(
            review.timeAgo,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}


// --- Sections and Nav ---
@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun VendorDetailPreview() {
    val mockVendor = com.example.bunnix.model.Vendor(
        id = 1, businessName = "Style Hub", category = "Fashion",
        coverImage = R.drawable.hero_pic, logoImage = R.drawable.style,
        rating = 4.8, reviewCount = 254, distance = "1.5 km",
        isService = true, about = "Style Hub info..."
    )

    BunnixTheme {
        VendorDetailScreen(
            vendor = mockVendor,
            onBack = {},
            onBookService = { _, _ -> },
            onViewProducts = {}
        )
    }
}
