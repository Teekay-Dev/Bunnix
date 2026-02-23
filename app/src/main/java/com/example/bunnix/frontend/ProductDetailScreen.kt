package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Review
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFF8F8F8)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)
private val WarningYellow = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    allProducts: List<Product>,
    onAddToCart: (Product, Int) -> Unit,
    onBuyNow: (Product, Int) -> Unit,
    onBack: () -> Unit,
    onChatWithVendor: (String) -> Unit,
    reviews: List<Review> = emptyList()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // States
    var selectedQuantity by remember { mutableIntStateOf(1) }
    var selectedVariant by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var showAddedToCart by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Description", "Reviews (${reviews.size})", "Vendor")

    // Image pager
    val imagePagerState = rememberPagerState(pageCount = {
        product.imageUrls.size.coerceAtLeast(1)
    })

    // Auto-scroll images
    LaunchedEffect(imagePagerState) {
        while (true) {
            delay(5000)
            val nextPage = (imagePagerState.currentPage + 1) % imagePagerState.pageCount
            imagePagerState.animateScrollToPage(nextPage)
        }
    }

    // Related products
    val relatedProducts = remember(allProducts, product) {
        allProducts.filter {
            it.category == product.category && it.productId != product.productId
        }.take(10)
    }

    Scaffold(
        topBar = {
            ProductDetailTopBar(
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite },
                onShareClick = { /* Share */ },
                onBack = onBack
            )
        },
        bottomBar = {
            ModernBottomBar(
                product = product,
                quantity = selectedQuantity,
                onQuantityChange = { selectedQuantity = it },
                onAddToCart = {
                    onAddToCart(product, selectedQuantity)
                    showAddedToCart = true
                    scope.launch {
                        delay(2000)
                        showAddedToCart = false
                    }
                },
                onBuyNow = { onBuyNow(product, selectedQuantity) }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Image Gallery
                ImageGallery(
                    product = product,
                    pagerState = imagePagerState
                )

                // Product Info Card
                ProductInfoCard(
                    product = product,
                    reviews = reviews
                )

                // Variant Selection
                if (product.variants.isNotEmpty()) {
                    VariantSelector(
                        variants = product.variants.map { it["name"] as? String ?: "" },
                        selectedVariant = selectedVariant,
                        onVariantSelect = { selectedVariant = it }
                    )
                }

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = OrangePrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = OrangePrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> DescriptionTab(product.description, product.tags)
                    1 -> ReviewsTab(reviews)
                    2 -> VendorTab(
                        vendorId = product.vendorId,
                        vendorName = product.vendorName,
                        onChatClick = { onChatWithVendor(product.vendorId) }
                    )
                }

                // Related Products
                if (relatedProducts.isNotEmpty()) {
                    RelatedProductsSection(
                        products = relatedProducts,
                        onProductClick = { /* Navigate */ }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Added to Cart Snackbar
            AnimatedVisibility(
                visible = showAddedToCart,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Surface(
                    color = SuccessGreen,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Added to cart!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailTopBar(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Favorite
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) ErrorRed else TextPrimary
                    )
                }

                // Share
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGallery(
    product: Product,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(SurfaceLight)
    ) {
        if (product.imageUrls.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        ).absoluteValue

                val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrls[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "${product.name} - Image ${page + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Placeholder
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No Images Available",
                        color = TextTertiary
                    )
                }
            }
        }

        // Page Indicator
        if (product.imageUrls.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(product.imageUrls.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "indicator"
                    )

                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) OrangePrimary else Color.White.copy(alpha = 0.6f)
                            )
                    )
                }
            }
        }

        // Discount Badge
        product.discountPrice?.let { discount ->
            val discountPercent = ((product.price - discount) / product.price * 100).toInt()
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 80.dp),
                color = ErrorRed,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    "-$discountPercent% OFF",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ProductInfoCard(
    product: Product,
    reviews: List<Review>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Category & Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = OrangeSoft,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        product.category,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Stock Status
                val stockColor = when {
                    product.totalStock == 0 -> ErrorRed
                    product.totalStock < 10 -> WarningYellow
                    else -> SuccessGreen
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(stockColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        when {
                            product.totalStock == 0 -> "Out of Stock"
                            product.totalStock < 10 -> "Only ${product.totalStock} left"
                            else -> "In Stock"
                        },
                        color = stockColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                product.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Vendor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    product.vendorName,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating & Sold
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rating
                Surface(
                    color = Color(0xFFFFC107).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val avgRating = if (reviews.isNotEmpty()) {
                            reviews.map { it.rating }.average()
                        } else 4.5
                        Text(
                            "%.1f".format(avgRating),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "(${reviews.size})",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Sold
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${product.sold} sold",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Views
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${product.views} views",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Price
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val displayPrice = product.discountPrice ?: product.price
                Text(
                    formatCurrency(displayPrice),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangePrimary
                )

                product.discountPrice?.let {
                    Text(
                        formatCurrency(product.price),
                        fontSize = 18.sp,
                        color = TextTertiary,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VariantSelector(
    variants: List<String>,
    selectedVariant: String?,
    onVariantSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            "Select Variant",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            variants.forEach { variant ->
                val isSelected = variant == selectedVariant
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "scale"
                )

                FilterChip(
                    selected = isSelected,
                    onClick = { onVariantSelect(variant) },
                    label = { Text(variant) },
                    modifier = Modifier.scale(scale),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                Icons.Default.Check,
                                null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun DescriptionTab(description: String, tags: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Description
        Text(
            "About this product",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            description.ifEmpty { "No description available." },
            color = TextSecondary,
            lineHeight = 24.sp,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tags
        if (tags.isNotEmpty()) {
            Text(
                "Tags",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        color = SurfaceLight,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "#$tag",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsTab(reviews: List<Review>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (reviews.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 40.dp),  // FIXED: Changed from vertical = 40.dp
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.RateReview,
                    null,
                    modifier = Modifier.size(64.dp),
                    tint = TextTertiary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No reviews yet",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
                Text(
                    "Be the first to review!",
                    color = TextTertiary,
                    fontSize = 14.sp
                )
            }
        } else {
            // Rating Summary
            val avgRating = reviews.map { it.rating }.average()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "%.1f".format(avgRating),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = if (index < avgRating.toInt()) Color(0xFFFFC107) else SurfaceLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${reviews.size} reviews",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                    Text(
                        "${reviews.count { it.isVerifiedPurchase }} verified",
                        color = SuccessGreen,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Review List
            reviews.forEach { review ->
                ReviewCard(review = review)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(OrangeSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            review.customerName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            review.customerName,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                .format(review.createdAt?.toDate() ?: Date()),
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }

                // Rating
                Surface(
                    color = Color(0xFFFFC107).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            review.rating.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comment
            Text(
                review.comment,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            // Images if any
            if (review.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    review.images.take(3).forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Vendor Response
            if (review.vendorResponse.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            "Vendor Response",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = OrangePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            review.vendorResponse,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Verified badge
            if (review.isVerifiedPurchase) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Verified,
                        null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Verified Purchase",
                        fontSize = 12.sp,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun VendorTab(
    vendorId: String,
    vendorName: String,
    onChatClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vendor Avatar
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(OrangeSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Store,
                        null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        vendorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "Vendor ID: ${vendorId.take(8)}...",
                        fontSize = 13.sp,
                        color = TextTertiary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.8", fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fast Response", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Button
        Button(
            onClick = onChatClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Icon(Icons.Default.Chat, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Chat with Vendor",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun RelatedProductsSection(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            "You May Also Like",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
//            modifier = Modifier.padding(horizontal = 16.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            products.forEach { product ->
                RelatedProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun RelatedProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrls.first(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = TextTertiary)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val price = product.discountPrice ?: product.price
                Text(
                    formatCurrency(price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun ModernBottomBar(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Quantity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quantity",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                            enabled = quantity > 1
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                null,
                                tint = if (quantity > 1) TextPrimary else TextTertiary
                            )
                        }

                        Text(
                            quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.widthIn(min = 32.dp),
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = { onQuantityChange(quantity + 1) },
                            enabled = quantity < product.totalStock
                        ) {
                            Icon(
                                Icons.Default.Add,
                                null,
                                tint = if (quantity < product.totalStock) TextPrimary else TextTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add to Cart
                OutlinedButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = product.inStock && product.totalStock > 0
                ) {
                    Icon(Icons.Default.ShoppingCart, null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add to Cart",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Buy Now
                Button(
                    onClick = onBuyNow,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        disabledContainerColor = SurfaceLight
                    ),
                    enabled = product.inStock && product.totalStock > 0
                ) {
                    Text(
                        if (product.inStock) "Buy Now" else "Out of Stock",
                        color = if (product.inStock) Color.White else TextTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// FlowRow implementation - FIXED with proper imports and explicit types
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<androidx.compose.ui.layout.Measurable>, constraints: androidx.compose.ui.unit.Constraints ->
        val hGapPx = 8.dp.roundToPx()
        val vGapPx = 8.dp.roundToPx()

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()

        var row = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var rowHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (row.isNotEmpty() && rowWidth + hGapPx + placeable.width > constraints.maxWidth) {
                rows.add(row)
                rowWidths.add(rowWidth)
                rowHeights.add(rowHeight)
                row = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }

            row.add(placeable)
            rowWidth += if (row.size == 1) placeable.width else hGapPx + placeable.width
            rowHeight = max(rowHeight, placeable.height)
        }

        if (row.isNotEmpty()) {
            rows.add(row)
            rowWidths.add(rowWidth)
            rowHeights.add(rowHeight)
        }

        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth) ?: constraints.minWidth
        val height = rowHeights.sum() + (rows.size - 1).coerceAtLeast(0) * vGapPx

        layout(width, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, rowPlaceables ->
                var x = when (horizontalArrangement) {
                    Arrangement.End -> width - rowWidths[rowIndex]
                    Arrangement.Center -> (width - rowWidths[rowIndex]) / 2
                    else -> 0
                }

                rowPlaceables.forEachIndexed { placeableIndex, placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + if (placeableIndex < rowPlaceables.size - 1) hGapPx else 0
                }
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProductDetailsScreenPreview() {
    val sampleProduct = Product(
        productId = "1",
        vendorId = "vendor_123",
        vendorName = "Tech Hub Store",
        name = "Premium Wireless Earbuds Pro with Active Noise Cancellation",
        description = "Experience crystal-clear audio with our latest wireless earbuds. Featuring active noise cancellation, 30-hour battery life, and premium comfort for all-day wear. Perfect for workouts, commuting, or relaxing at home.",
        price = 75000.0,
        discountPrice = 59999.0,
        category = "Tech",
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600 ",
            "https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=600 "
        ),
        variants = listOf(
            mapOf("name" to "Black"),
            mapOf("name" to "White"),
            mapOf("name" to "Blue")
        ),
        totalStock = 15,
        inStock = true,
        tags = listOf("wireless", "bluetooth", "audio", "noise-cancelling", "premium"),
        views = 1250,
        sold = 89,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )

    val sampleReviews = listOf(
        Review(
            reviewId = "r1",
            vendorId = "vendor_123",
            customerId = "cust_1",
            customerName = "Sarah Johnson",
            orderId = "ord_1",
            bookingId = "",
            rating = 5,
            comment = "Absolutely love these earbuds! The noise cancellation is amazing and the battery lasts forever. Highly recommend!",
            images = emptyList(),
            vendorResponse = "Thank you Sarah! We're so glad you're enjoying them. Don't forget to register for warranty!",
            vendorResponseAt = Timestamp.now(),
            isVerifiedPurchase = true,
            createdAt = Timestamp.now()
        ),
        Review(
            reviewId = "r2",
            vendorId = "vendor_123",
            customerId = "cust_2",
            customerName = "Michael Chen",
            orderId = "ord_2",
            bookingId = "",
            rating = 4,
            comment = "Great sound quality, but the fit took some getting used to. Overall very satisfied with my purchase.",
            images = emptyList(),
            vendorResponse = "",
            vendorResponseAt = null,
            isVerifiedPurchase = true,
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000))
        )
    )

    BunnixTheme {
        ProductDetailsScreen(
            product = sampleProduct,
            allProducts = listOf(sampleProduct),
            onAddToCart = { _, _ -> },
            onBuyNow = { _, _ -> },
            onBack = {},
            onChatWithVendor = {},
            reviews = sampleReviews
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewCardPreview() {
    BunnixTheme {
        ReviewCard(
            review = Review(
                reviewId = "r1",
                vendorId = "v1",
                customerId = "c1",
                customerName = "Jane Doe",
                orderId = "o1",
                bookingId = "",
                rating = 5,
                comment = "Amazing product! Exactly what I needed.",
                images = emptyList(),
                vendorResponse = "Thanks for your feedback!",
                vendorResponseAt = Timestamp.now(),
                isVerifiedPurchase = true,
                createdAt = Timestamp.now()
            )
        )
    }
}