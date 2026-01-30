//package com.example.bunnix.frontend
//
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.*
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.bunnix.R
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import com.example.bunnix.model.Vendor
//
////import com.example.bunnix.model.Vendor
//
//class HomeActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            HomeScreen()
//        }
//    }
//}
//private val OrangeStart = Color(0xFFFF8A00)
//private val OrangeEnd = Color(0xFFFF5A00)
//
//@Composable
//fun HomeScreen() {
//
//    val scrollState = rememberScrollState()
//
//    val showStickySearch by remember {
//        derivedStateOf { scrollState.value > 180 }
//    }
//
//    var searchQuery by remember { mutableStateOf("") }
//    var selectedCategory by remember { mutableStateOf<String?>(null) }
//
//    Scaffold(
//        topBar = {
//            if (showStickySearch) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White)
//                        .padding(8.dp)
//                ) {
//                    SearchBar(
//                        query = searchQuery,
//                        onQueryChange = { searchQuery = it }
//                    )
//                }
//            }
//        },
//        bottomBar = { BottomNavBar() },
//        containerColor = Color(0xFFF8F8F8)
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .verticalScroll(scrollState)
//        ) {
//
//            // ---- HEADER (with search inside)
//            HeaderSection(searchQuery, onQueryChange = { searchQuery = it })
//
//            ActionButtons()
//
//            CategoriesSection(
//                selectedCategory = selectedCategory,
//                onCategoryClick = { selectedCategory = it }
//            )
//
//            if (selectedCategory != null) {
//                ClearCategoryButton { selectedCategory = null }
//            }
//
//            SpecialOffer()
//
//            TopVendorsSection(
//                selectedCategory = selectedCategory
//            )
//
//            Spacer(Modifier.height(100.dp))
//        }
//    }
//}
//
//
//
//@Composable
//private fun HeaderSection(
//    query: String,
//    onQueryChange: (String) -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .background(
//                Brush.verticalGradient(
//                    listOf(OrangeStart, OrangeEnd)
//                )
//            )
//            .padding(20.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.bunnix_2),
//                contentDescription = "Bunnix Logo",
//                colorFilter = ColorFilter.tint(Color.White),
//                modifier = Modifier.size(70.dp)
//            )
//
//            Spacer(modifier = Modifier.width(1.dp))
//
//            Column {
//                Text("Welcome to", color = Color.White, fontSize = 16.sp)
//                Text("Bunnix", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
//            }
//        }
//
//        Icon(
//            Icons.Default.AutoAwesome,
//            contentDescription = null,
//            tint = Color.White,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .size(52.dp)
//                .clip(CircleShape)
//                .background(Color.White.copy(0.2f))
//                .padding(10.dp)
//        )
//
//        SearchBar(
//            query = query,
//            onQueryChange = onQueryChange,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .offset(y = 0.dp)
//        )
//    }
//}
//
//
//
//@Composable
//private fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    modifier: Modifier = Modifier
//
//) {
//    TextField(
//        value = query,
//        onValueChange = onQueryChange,
//        placeholder = { Text("Search services & products...") },
//        leadingIcon = { Icon(Icons.Default.Search, null) },
//        shape = RoundedCornerShape(30),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 10.dp),
//        colors = TextFieldDefaults.colors(
//            focusedContainerColor = Color.White,
//            unfocusedContainerColor = Color.White,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent
//        )
//    )
//}
//
//
//
//@Composable
//private fun ActionButtons() {
//    Row(
//        modifier = Modifier
//            .padding(horizontal = 10.dp, vertical = 20.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        ActionCard("Book a Service", Icons.Default.Event)
//        ActionCard("Shop Products", Icons.Default.ShoppingBag)
//    }
//}
//
//@Composable
//private fun ActionCard(title: String, icon: ImageVector) {
//    Box(
//        modifier = Modifier
//            .width(170.dp)
//            .height(100.dp)
//            .clip(RoundedCornerShape(20.dp))
//            .background(
//                Brush.linearGradient(
//                    listOf(OrangeStart, OrangeEnd)
//                )
//            )
//            .padding(16.dp)
//    ) {
//        Column {
//            Icon(icon, null, tint = Color.White)
//            Spacer(Modifier.height(8.dp))
//            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
//        }
//    }
//}
//
//
//@Composable
//private fun SpecialOffer() {
//    Box(
//        modifier = Modifier
//            .padding(20.dp)
//            .height(180.dp)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(24.dp))
//    ) {
//        Image(
//            painter = painterResource(R.drawable.sales_img),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Column(
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(20.dp)
//        ) {
//            Text("Special Offers", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
//            Text("Up to 30% off this week", color = Color.White)
//        }
//    }
//}
//
//
//@Composable
//private fun CategoriesSection(
//    selectedCategory: String?,
//    onCategoryClick: (String) -> Unit
//) {
//    Text(
//        "Categories",
//        modifier = Modifier.padding(start = 20.dp),
//        fontSize = 20.sp,
//        fontWeight = FontWeight.Bold
//    )
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(4),
//        modifier = Modifier
//            .height(220.dp)
//            .padding(20.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(categoryList) { category ->
//
//            val isSelected = category.name == selectedCategory
//
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .clickable { onCategoryClick(category.name) }
//            ){
//                Box(
//                    modifier = Modifier
//                        .size(64.dp)
//                        .clip(RoundedCornerShape(18.dp))
//                        .background(
//                            if (isSelected) Color(0xFFFF8A00) else Color.White
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(category.icon, fontSize = 28.sp)
//                }
//                Spacer(Modifier.height(6.dp))
//                Text(category.name,
//                    fontSize = 12.sp,
//                    color = if (isSelected) Color(0xFFFF8A00) else Color.Black)
//            }
//        }
//    }
//}
//
//@Composable
//fun ClearCategoryButton(onClear: () -> Unit) {
//    Button(
//        onClick = onClear,
//        modifier = Modifier
//            .padding(horizontal = 20.dp)
//            .fillMaxWidth(),
//        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
//        shape = RoundedCornerShape(20.dp)
//    ) {
//        Text("Clear Filter", color = Color.White)
//    }
//}
//
//
//
//@Composable
//private fun TopVendorsSection(
//    selectedCategory: String?
//) {
//
//    val vendors = vendorList.filter {
//        selectedCategory == null || it.category == selectedCategory
//    }
//
//    Text(
//        text = selectedCategory?.let { "$it Vendors" } ?: "Top Vendors",
//        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
//        fontSize = 20.sp,
//        fontWeight = FontWeight.Bold
//    )
//
//    vendors.forEach { vendor ->
//        VendorCard(vendor)
//        Spacer(Modifier.height(30.dp))
//    }
//
//
//
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.bites),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(modifier = Modifier.padding(16.dp)) {
////
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Gourmet Bites",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Food", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("354 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $15",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("1.2 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 4.8")
////        }
////    }
////
////
////
////
////
////    Spacer(Modifier.height(100.dp))
////
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.style),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(Modifier.padding(16.dp)) {
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Style Hub",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Fashion", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("189 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $25",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("2.5 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 4.0")
////        }
////    }
////
////    Spacer(Modifier.height(100.dp))
////
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.beauty),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(Modifier.padding(16.dp)) {
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Beauty Bliss",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Beauty", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("412 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $30",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("0.8 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 5.0")
////        }
////    }
////
////    Spacer(Modifier.height(100.dp))
////
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.event),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(Modifier.padding(16.dp)) {
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Event Masters",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Event", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("256 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $200",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("3.0 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 4.5")
////        }
////    }
////
////    Spacer(Modifier.height(100.dp))
////
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.home),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(Modifier.padding(16.dp)) {
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Home Essentials",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Home", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("178 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $20",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("1.8 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 4.8")
////        }
////    }
////
////    Spacer(Modifier.height(100.dp))
////
////    Box(
////        modifier = Modifier
////            .padding(horizontal = 20.dp)
////            .clip(RoundedCornerShape(24.dp))
////            .background(Color.White)
////    ) {
////        Column {
////            Image(
////                painter = painterResource(R.drawable.tech),
////                contentDescription = null,
////                modifier = Modifier
////                    .height(200.dp)
////                    .fillMaxWidth(),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(Modifier.padding(16.dp)) {
////                Row(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalArrangement = Arrangement.SpaceBetween
////                ) {
////
////                    /* LEFT SIDE — your original content + From $15 */
////                    Column {
////                        Text(
////                            "Tech Solutions",
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 18.sp
////                        )
////
////                        Text("Tech", color = Color.Gray, fontSize = 14.sp)
////
////                        Text("298 reviews", color = Color.Gray, fontSize = 13.sp)
////
////                        Spacer(modifier = Modifier.height(6.dp))
////
////                        Text(
////                            "From $50",
////                            color = Color(0xFFFF6A00),
////                            fontWeight = FontWeight.Bold,
////                            fontSize = 15.sp
////                        )
////                    }
////
////                    /* RIGHT SIDE — location + view button */
////                    Column(
////                        horizontalAlignment = Alignment.End
////                    ) {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Icon(
////                                imageVector = Icons.Default.LocationOn,
////                                contentDescription = null,
////                                tint = Color.Gray,
////                                modifier = Modifier.size(16.dp)
////                            )
////                            Spacer(Modifier.width(4.dp))
////                            Text("2.2 km", color = Color.Gray, fontSize = 13.sp)
////                        }
////
////                        Spacer(modifier = Modifier.height(10.dp))
////
////                        Button(
////                            onClick = {},
////                            shape = RoundedCornerShape(20.dp),
////                            colors = ButtonDefaults.buttonColors(
////                                containerColor = Color(0xFFFF6A00)
////                            ),
////                            contentPadding = PaddingValues(
////                                horizontal = 20.dp,
////                                vertical = 6.dp
////                            )
////                        ) {
////                            Text("View", color = Color.White, fontSize = 13.sp)
////                        }
////                    }
////                }
////            }
////        }
////
////        Box(
////            modifier = Modifier
////                .align(Alignment.TopEnd)
////                .padding(12.dp)
////                .clip(RoundedCornerShape(20.dp))
////                .background(Color.White)
////                .padding(horizontal = 10.dp, vertical = 4.dp)
////        ) {
////            Text("⭐ 4.8")
////        }
////    }
//
////    Spacer(Modifier.height(100.dp))
//
//}
//
//@Composable
//fun BottomNavBar() {
//    NavigationBar {
//        NavigationBarItem(
//            selected = true,
//            onClick = { },
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//            label = { Text("Home") }
//        )
//
//        NavigationBarItem(
//            selected = false,
//            onClick = { },
//            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
//            label = { Text("Cart") }
//        )
//
//        NavigationBarItem(
//            selected = false,
//            onClick = { },
//            icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
//            label = { Text("Chats") }
//        )
//
//        NavigationBarItem(
//            selected = false,
//            onClick = { },
//            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
//            label = { Text("Alerts") }
//        )
//
//        NavigationBarItem(
//            selected = false,
//            onClick = { },
//            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
//            label = { Text("Profile") }
//        )
//    }
//}
//
//@Composable
//fun VendorCard(vendor: Vendor) {
//    Box(
//        modifier = Modifier
//            .padding(horizontal = 20.dp)
//            .clip(RoundedCornerShape(24.dp))
//            .background(Color.White)
//    ) {
//        Column {
//            Image(
//                painter = painterResource(vendor.image),
//                contentDescription = null,
//                modifier = Modifier
//                    .height(180.dp)
//                    .fillMaxWidth(),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(Modifier.padding(16.dp)) {
//                Text(vendor.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Text(vendor.category, color = Color.Gray, fontSize = 14.sp)
//                Spacer(Modifier.height(6.dp))
//                Text(vendor.price, color = Color(0xFFFF6A00), fontWeight = FontWeight.Bold)
//
//                Spacer(Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(vendor.rating)
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.LocationOn,
//                            contentDescription = null,
//                            tint = Color.Gray,
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(Modifier.width(4.dp))
//                        Text(vendor.distance, color = Color.Gray)
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//
//
//data class Category(val name: String, val icon: String)
//
//val categoryList = listOf(
//    Category("Food", "🍔"),
//    Category("Fashion", "👗"),
//    Category("Events", "🎉"),
//    Category("Home", "🏠"),
//    Category("Beauty", "💄"),
//    Category("Tech", "💻"),
//    Category("Sports", "⚽"),
//    Category("Health", "🏥")
//)
//
//
//val vendorList = listOf(
//    Vendor(
//        "Gourmet Bites",
//        "Food",
//        R.drawable.bites,
//        "From $15",
//        "⭐ 4.8",
//        "1.2 km",
//        "gourmentbites@gmail.com"
//    ),
//    Vendor(
//        "Style Hub",
//        "Fashion",
//        R.drawable.style,
//        "From $25",
//        "⭐ 4.0",
//        "2.5 km",
//        "stylehub@gmail.com"
//    ),
//    Vendor(
//        "Beauty Bliss",
//        "Beauty",
//        R.drawable.beauty,
//        "From $30",
//        "⭐ 5.0",
//        "0.8 km",
//        "beautybliss@gmail.com"
//    ),
//    Vendor(
//        "Tech Solutions",
//        "Tech",
//        R.drawable.tech,
//        "From $50",
//        "⭐ 4.8",
//        "2.2 km",
//        "techsolutions@gmail.com"
//    )
//)
//
//
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    HomeScreen()
//}
