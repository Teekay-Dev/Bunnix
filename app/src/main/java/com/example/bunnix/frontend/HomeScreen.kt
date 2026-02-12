package com.example.bunnix.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.R
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bunnix.model.Vendor
import com.example.bunnix.model.vendorList


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}
private val OrangeStart = Color(0xFFFF8A00)
private val OrangeEnd = Color(0xFFFF5A00)



@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onVendorClick: (Int) -> Unit,
    onBookServiceClick: () -> Unit,
    onShopProductClick: () -> Unit,
    onSearchClick: (String) -> Unit
)
 {

    val scrollState = rememberScrollState()

    val showStickySearch by remember {
        derivedStateOf { scrollState.value > 180 }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            if (showStickySearch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearchClick = { text ->
                            onSearchClick(text)
                        }
                    )

                }
            }
        },

        containerColor = Color(0xFFF8F8F8)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
        ) {

            // ---- HEADER (with search inside)
            HeaderSection(searchQuery, onQueryChange = { searchQuery = it })

            ActionButtons(
                onBookClick = onBookServiceClick,
                onShopClick = onShopProductClick
            )


            CategoriesSection(
                selectedCategory = selectedCategory,
                onCategoryClick = { selectedCategory = it }
            )

            if (selectedCategory != null) {
                ClearCategoryButton { selectedCategory = null }
            }

            SpecialOffer()

            TopVendorsSection(
                selectedCategory = selectedCategory,
                onVendorClick = onVendorClick
            )


            Spacer(Modifier.height(100.dp))
        }
    }
}



@Composable
private fun HeaderSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    listOf(OrangeStart, OrangeEnd)
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.bunnix_2),
                contentDescription = "Bunnix Logo",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(1.dp))

            Column {
                Text("Welcome to", color = Color.White, fontSize = 16.sp)
                Text("Bunnix", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            }
        }

        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.2f))
                .padding(10.dp)
        )

        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearchClick = { } // leave empty here
        )

    }
}



@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search products or services...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (query.isNotBlank()) {
                    onSearchClick(query)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeEnd)
        ) {
            Text("Search", color = Color.White)
        }
    }
}




@Composable
private fun ActionButtons(
    onBookClick: () -> Unit,
    onShopClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ActionCard(
            title = "Book a Service",
            icon = Icons.Default.Event,
            onClick = onBookClick
        )

        ActionCard(
            title = "Shop Products",
            icon = Icons.Default.ShoppingBag,
            onClick = onShopClick
        )
    }
}


@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() } // ✅ CLICKABLE
            .background(
                Brush.linearGradient(
                    listOf(OrangeStart, OrangeEnd)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
private fun SpecialOffer() {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .height(180.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.sales_img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        ) {
            Text("Special Offers", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Up to 30% off this week", color = Color.White)
        }
    }
}


@Composable
private fun CategoriesSection(
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    Text(
        "Categories",
        modifier = Modifier.padding(start = 20.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .height(220.dp)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categoryList) { category ->

            val isSelected = category.name == selectedCategory

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onCategoryClick(category.name) }
            ){
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) Color(0xFFFF8A00) else Color.White
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(category.icon, fontSize = 28.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(category.name,
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFFFF8A00) else Color.Black)
            }
        }
    }
}

@Composable
fun ClearCategoryButton(onClear: () -> Unit) {
    Button(
        onClick = onClear,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text("Clear Filter", color = Color.White)
    }
}



@Composable
private fun TopVendorsSection(
    selectedCategory: String?,
    onVendorClick: (Int) -> Unit
) {
    val vendors = vendorList.filter {
        selectedCategory == null || it.category == selectedCategory
    }

    Text(
        text = selectedCategory?.let { "$it Vendors" } ?: "Top Vendors",
        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    vendors.forEach { vendor ->
        VendorCard(
            vendor = vendor,
            onViewClick = {
                onVendorClick(vendor.id)
            }
        )
        Spacer(Modifier.height(30.dp))
    }

}


@Composable
fun VendorCard(
    vendor: Vendor,
    onViewClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {
        Column {

            Image(
                painter = painterResource(vendor.coverImage),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = vendor.businessName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = vendor.category,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))

                    Text(
                        "${vendor.rating} (${vendor.reviewCount})",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))

                    Text(
                        vendor.distance,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onViewClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeEnd),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View", color = Color.White)
                }
            }
        }
    }
}






data class Category(val name: String, val icon: String)

val categoryList = listOf(
    Category("Food", "🍔"),
    Category("Fashion", "👗"),
    Category("Events", "🎉"),
    Category("Home", "🏠"),
    Category("Beauty", "💄"),
    Category("Tech", "💻"),
    Category("Sports", "⚽"),
    Category("Health", "🏥")
)







@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MaterialTheme {
        HomeScreen(
            onVendorClick = { },
            onBookServiceClick = { },
            onShopProductClick = { },
            onSearchClick = { }
        )
    }
}
