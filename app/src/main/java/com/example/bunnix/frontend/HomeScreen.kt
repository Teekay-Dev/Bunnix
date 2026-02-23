package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.R
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.models.VendorProfile // FIXED: Use VendorProfile
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// ===== MODERN COLOR SYSTEM =====
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeDark = Color(0xFFE85A24)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)

// ===== CATEGORY DATA =====
data class Category(
    val name: String,
    val icon: String
)

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

// ===== SPECIAL OFFER DATA =====
data class SpecialOffer(
    val id: String,
    val title: String,
    val subtitle: String,
    val discount: String,
    val backgroundColor: List<Color>,
    val imageUrl: String? = null,
    val actionText: String = "Shop Now"
)

val specialOffers = listOf(
    SpecialOffer(
        id = "1",
        title = "Weekend Special!",
        subtitle = "Get 30% off all spa services",
        discount = "-30%",
        backgroundColor = listOf(Color(0xFFFF6B35), Color(0xFFFF8C61)),
        actionText = "Book Now"
    ),
    SpecialOffer(
        id = "2",
        title = "New Vendors!",
        subtitle = "Discover 50+ new businesses",
        discount = "NEW",
        backgroundColor = listOf(Color(0xFF2EC4B6), Color(0xFF00BBF9)),
        actionText = "Explore"
    ),
    SpecialOffer(
        id = "3",
        title = "Flash Sale",
        subtitle = "Limited time deals on electronics",
        discount = "-50%",
        backgroundColor = listOf(Color(0xFF9B5DE5), Color(0xFF00F5D4)),
        actionText = "Grab Deal"
    ),
    SpecialOffer(
        id = "4",
        title = "Free Delivery",
        subtitle = "On all orders above ₦10,000",
        discount = "FREE",
        backgroundColor = listOf(Color(0xFFFFBE0B), Color(0xFFFF6B35)),
        actionText = "Order Now"
    )
)

// ===== UI MODEL FOR VENDORS (Maps VendorProfile to UI needs) =====
data class VendorUiModel(
    val id: String, // Changed to String to match vendorId
    val businessName: String,
    val category: String,
    val coverImageRes: Int, // Drawable resource ID
    val logoImageRes: Int, // Drawable resource ID
    val rating: Double,
    val reviewCount: Int,
    val distance: String,
    val isVerified: Boolean = false
)

// Extension to convert VendorProfile to VendorUiModel
fun VendorProfile.toUiModel(
    coverRes: Int = R.drawable.ic_launcher_background,
    logoRes: Int = R.drawable.ic_launcher_foreground,
    distanceKm: String = "2.0 km"
): VendorUiModel {
    return VendorUiModel(
        id = this.vendorId,
        businessName = this.businessName,
        category = this.category,
        coverImageRes = coverRes,
        logoImageRes = logoRes,
        rating = this.rating,
        reviewCount = this.totalReviews,
        distance = distanceKm,
        isVerified = this.rating > 4.0
    )
}

// Mock vendor list - REPLACE with your actual data source
val mockVendorList = listOf(
    VendorUiModel(
        id = "1",
        businessName = "TechHub Store",
        category = "Tech",
        coverImageRes = R.drawable.ic_launcher_background,
        logoImageRes = R.drawable.ic_launcher_foreground,
        rating = 4.8,
        reviewCount = 128,
        distance = "2.3 km",
        isVerified = true
    ),
    VendorUiModel(
        id = "2",
        businessName = "Fashion Hub",
        category = "Fashion",
        coverImageRes = R.drawable.ic_launcher_background,
        logoImageRes = R.drawable.ic_launcher_foreground,
        rating = 4.5,
        reviewCount = 85,
        distance = "1.5 km"
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onVendorClick: (String) -> Unit, // CHANGED: Int to String
    onBookServiceClick: () -> Unit,
    onShopProductClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    featuredProducts: List<Product> = emptyList(),
    featuredServices: List<Service> = emptyList(),
    bottomBar: @Composable () -> Unit = {}, // ADD THIS
    vendors: List<VendorUiModel> = mockVendorList // NEW: Accept vendors list

) {
    val scrollState = rememberScrollState()

    val pagerState = rememberPagerState(pageCount = { specialOffers.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % specialOffers.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(800, easing = EaseInOutCubic)
            )
        }
    }

    val showStickySearch by remember {
        derivedStateOf { scrollState.value > 200 }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showStickySearch,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                StickySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { onSearchClick(searchQuery) }
                )
            }
        },
        bottomBar = bottomBar,
        containerColor = SurfaceLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            HeroSection(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { onSearchClick(searchQuery) }
            )

            QuickActionSection(
                onBookService = onBookServiceClick,
                onShopProducts = onShopProductClick
            )

            SpecialOffersCarousel(
                pagerState = pagerState,
                offers = specialOffers,
                onOfferClick = { offer ->
                    when (offer.id) {
                        "1" -> onBookServiceClick()
                        "2" -> onSearchClick("new vendors")
                        "3" -> onShopProductClick()
                        else -> onShopProductClick()
                    }
                }
            )

            CategoriesSection(
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it }
            )

            AnimatedVisibility(
                visible = selectedCategory != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ClearFilterButton(
                    category = selectedCategory ?: "",
                    onClear = { selectedCategory = null }
                )
            }

            FeaturedVendorsSection(
                vendors = vendors, // Pass vendors list
                selectedCategory = selectedCategory,
                onVendorClick = onVendorClick
            )

            if (featuredProducts.isNotEmpty()) {
                FeaturedProductsSection(
                    products = featuredProducts,
                    onProductClick = { }
                )
            }

            if (featuredServices.isNotEmpty()) {
                FeaturedServicesSection(
                    services = featuredServices,
                    onServiceClick = { }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ===== HERO SECTION =====
@Composable
private fun HeroSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(OrangePrimary, OrangeDark)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "logo")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .scale(scale)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bunnix_2),
                        contentDescription = "Bunnix",
                        modifier = Modifier.size(70.dp) // Adjust size as needed
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Bunnix",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Shop • Book • Connect",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFEF4444),
                                modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                            ) {
                                Text("3", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome To Bunnix! 👋",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "How may we be of help today?",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernSearchBar(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = onSearch
            )
        }
    }
}

// ===== MODERN SEARCH BAR =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Search products, services, vendors...",
                color = TextTertiary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = OrangePrimary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextTertiary
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = OrangePrimary,
            focusedLabelColor = OrangePrimary
        ),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

// ===== STICKY SEARCH BAR =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StickySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = OrangePrimary)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = SurfaceLight,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSearch,
                modifier = Modifier
                    .background(OrangePrimary, CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}

// ===== QUICK ACTIONS =====
@Composable
private fun QuickActionSection(
    onBookService: () -> Unit,
    onShopProducts: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionCard(
            title = "Book Service",
            subtitle = "Find experts",
            icon = Icons.Default.CalendarToday,
            gradient = listOf(TealAccent, Color(0xFF00BBF9)),
            onClick = onBookService,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            title = "Shop Products",
            subtitle = "Buy & sell",
            icon = Icons.Default.ShoppingBag,
            gradient = listOf(OrangePrimary, OrangeLight),
            onClick = onShopProducts,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }

    Card(
        onClick = {
            kotlinx.coroutines.GlobalScope.launch {
                scale.animateTo(0.95f, tween(100))
                scale.animateTo(1f, tween(100))
                onClick()
            }
        },
        modifier = modifier
            .height(120.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ===== SLIDING SPECIAL OFFERS CAROUSEL =====
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpecialOffersCarousel(
    pagerState: androidx.compose.foundation.pager.PagerState,
    offers: List<SpecialOffer>,
    onOfferClick: (SpecialOffer) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp),
            pageSpacing = 16.dp
        ) { page ->
            val offer = offers[page]
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
            val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

            OfferCard(
                offer = offer,
                onClick = { onOfferClick(offer) },
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            offers.forEachIndexed { index, _ ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "indicator"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(width)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) OrangePrimary
                            else TextTertiary.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
private fun OfferCard(
    offer: SpecialOffer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(offer.backgroundColor))
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = offer.discount,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = offer.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = offer.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = offer.actionText,
                        color = offer.backgroundColor.first(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 150.dp, y = (-20).dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 180.dp, y = 40.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            )
        }
    }
}

// ===== CATEGORIES SECTION =====
@Composable
private fun CategoriesSection(
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

//            TextButton(onClick = { }) {
//                Text(
//                    "See All",
//                    color = OrangePrimary,
//                    fontWeight = FontWeight.Medium
//                )
//            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoryList) { category ->
                val isSelected = category.name == selectedCategory

                CategoryItem(
                    category = category,
                    isSelected = isSelected,
                    onClick = { onCategorySelect(category.name) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) OrangePrimary else Color.White,
        animationSpec = tween(300),
        label = "bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextPrimary,
        animationSpec = tween(300),
        label = "content"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(
                    elevation = if (isSelected) 8.dp else 2.dp,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(backgroundColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.icon,
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) OrangePrimary else TextSecondary
        )
    }
}

// ===== CLEAR FILTER BUTTON =====
@Composable
private fun ClearFilterButton(
    category: String,
    onClear: () -> Unit
) {
    Button(
        onClick = onClear,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Clear $category Filter")
    }
}

// ===== FEATURED VENDORS SECTION =====
@Composable
private fun FeaturedVendorsSection(
    vendors: List<VendorUiModel>, // CHANGED: Accept list as parameter
    selectedCategory: String?,
    onVendorClick: (String) -> Unit // CHANGED: Int to String
) {
    val filteredVendors = if (selectedCategory != null) {
        vendors.filter { it.category == selectedCategory }
    } else {
        vendors
    }

    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory?.let { "$it Vendors" } ?: "Top Vendors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            TextButton(onClick = { }) {
                Text(
                    "See All",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredVendors.forEach { vendor ->
            ModernVendorCard(
                vendor = vendor,
                onClick = { onVendorClick(vendor.id) } // Pass String id
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModernVendorCard(
    vendor: VendorUiModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(vendor.coverImageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 100f
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    color = OrangePrimary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        vendor.category,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(vendor.logoImageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        vendor.businessName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${vendor.rating}",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            " (${vendor.reviewCount} reviews)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            vendor.distance,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    )
                ) {
                    Text("View")
                }
            }
        }
    }
}

// ===== FEATURED PRODUCTS SECTION =====
@Composable
private fun FeaturedProductsSection(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Products",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            TextButton(onClick = { }) {
                Text(
                    "See All",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.take(10)) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(SurfaceLight)
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrls.first())
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (product.totalStock < 10) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        color = Color(0xFFEF4444),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Low Stock",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "₦${product.price.toInt()}",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${product.sold} sold",
                    fontSize = 12.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

// ===== FEATURED SERVICES SECTION =====
@Composable
private fun FeaturedServicesSection(
    services: List<Service>,
    onServiceClick: (Service) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Services",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            TextButton(onClick = { }) {
                Text(
                    "See All",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        services.take(5).forEach { service ->
            ServiceListItem(
                service = service,
                onClick = { onServiceClick(service) }
            )
        }
    }
}

@Composable
private fun ServiceListItem(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight)
            ) {
                if (service.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    service.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${service.duration} mins",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "₦${service.price.toInt()}",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    )
                ) {
                    Text("Book")
                }
            }
        }
    }
}

// ===== PREVIEWS =====
@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    BunnixTheme {
        HomeScreen(
            onVendorClick = {},
            onBookServiceClick = {},
            onShopProductClick = {},
            onSearchClick = {}
        )
    }
}