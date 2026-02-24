package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.Product
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random // ADD THIS IMPORT for Random

// Modern Colors
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val PurpleAccent = Color(0xFF9B5DE5)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)
private val WarningYellow = Color(0xFFFFA000) // ADD THIS MISSING COLOR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    products: List<Product> = emptyList(),
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf("Popular") }
    var showSortMenu by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    // Get unique categories from products
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct()
    }

    // Filter and sort products
    val filteredProducts = remember(products, searchQuery, selectedCategory, sortBy) {
        products.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true) ||
                    product.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesCategory = selectedCategory == null ||
                    selectedCategory == "All" ||
                    product.category == selectedCategory

            matchesSearch && matchesCategory
        }.let { filtered ->
            when (sortBy) {
                "Price: Low to High" -> filtered.sortedBy { it.price }
                "Price: High to Low" -> filtered.sortedByDescending { it.price }
                "Newest" -> filtered.sortedByDescending { it.createdAt?.toDate()?.time ?: 0 }
                "Best Selling" -> filtered.sortedByDescending { it.sold }
                else -> filtered.sortedByDescending { it.views }
            }
        }
    }

    Scaffold(
        topBar = {
            ModernProductTopBar(
                productCount = filteredProducts.size,
                onBack = onBack,
                onSearchClick = { /* Focus search */ }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search products..."
                )

                // Category Chips
                CategoryChips(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it }
                )

                // Filter & Sort Bar
                FilterSortBar(
                    resultCount = filteredProducts.size,
                    sortBy = sortBy,
                    onSortClick = { showSortMenu = true },
                    viewMode = viewMode,
                    onViewModeToggle = { viewMode = it }
                )

                // Product Grid/List
                if (filteredProducts.isEmpty()) {
                    EmptyProductState(
                        query = searchQuery,
                        onClearSearch = {
                            searchQuery = ""
                            selectedCategory = null
                        }
                    )
                } else {
                    if (viewMode == ViewMode.GRID) {
                        ProductGrid(
                            products = filteredProducts,
                            onProductClick = onProductClick
                        )
                    } else {
                        ProductList(
                            products = filteredProducts,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    }

    // Sort Dropdown Menu
    DropdownMenu(
        expanded = showSortMenu,
        onDismissRequest = { showSortMenu = false }
    ) {
        listOf("Popular", "Price: Low to High", "Price: High to Low", "Newest", "Best Selling").forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    sortBy = option
                    showSortMenu = false
                },
                leadingIcon = {
                    if (sortBy == option) {
                        Icon(Icons.Default.Check, null, tint = OrangePrimary)
                    }
                }
            )
        }
    }
}

enum class ViewMode { GRID, LIST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernProductTopBar(
    productCount: Int,
    onBack: () -> Unit,
    onSearchClick: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "All Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "$productCount items",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = OrangePrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OrangePrimary
                    )
                }
                IconButton(onClick = { /* Filter */ }) {
                    BadgedBox(
                        badge = { Badge(containerColor = OrangePrimary) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = OrangePrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    placeholder,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceLight,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = OrangePrimary.copy(alpha = 0.5f)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory || (category == "All" && selectedCategory == null)

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onCategorySelect(if (category == "All") null else category)
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceLight
                    ),
                    shape = RoundedCornerShape(20.dp),
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
private fun FilterSortBar(
    resultCount: Int,
    sortBy: String,
    onSortClick: () -> Unit,
    viewMode: ViewMode,
    onViewModeToggle: (ViewMode) -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$resultCount results",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sort Button
                TextButton(
                    onClick = onSortClick,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Sort,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        sortBy,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = TextTertiary
                    )
                }

                // View Mode Toggle
                Row(
                    modifier = Modifier
                        .background(SurfaceLight, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { onViewModeToggle(ViewMode.GRID) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (viewMode == ViewMode.GRID) Color.White else Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Default.GridView,
                            null,
                            tint = if (viewMode == ViewMode.GRID) OrangePrimary else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { onViewModeToggle(ViewMode.LIST) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (viewMode == ViewMode.LIST) Color.White else Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Default.ViewList,
                            null,
                            tint = if (viewMode == ViewMode.LIST) OrangePrimary else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = products,
            key = { it.productId }
        ) { product ->
            ProductGridCard(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    // Find your product list display, wrap it with this check:

    if (products.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Inventory,
            title = "No Products Yet",
            message = "Products will appear here when vendors add them. Check back soon!",
            actionText = "Browse Vendors",
            onAction = { /* navigate to home/vendor list */ }
        )
    } else {
        // Your existing LazyColumn/Row of products

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = products,
                key = { it.productId }
            ) { product ->
                ProductListCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun ProductGridCard(
    product: Product,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrls.first())
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
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
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                                startY = 80f
                            )
                        )
                )

                // Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Discount badge
                    product.discountPrice?.let { discount ->
                        val discountPercent = ((product.price - discount) / product.price * 100).toInt()
                        Surface(
                            color = ErrorRed,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "-$discountPercent%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Stock badge
                    if (product.totalStock < 10) {
                        Surface(
                            color = if (product.totalStock == 0) ErrorRed else WarningYellow,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (product.totalStock == 0) "Out of Stock" else "${product.totalStock} left",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Category chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    color = OrangePrimary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        product.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    product.vendorName,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val displayPrice = product.discountPrice ?: product.price
                        Text(
                            formatCurrency(displayPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = OrangePrimary
                        )

                        product.discountPrice?.let {
                            Text(
                                formatCurrency(product.price),
                                fontSize = 12.sp,
                                color = TextTertiary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }

                    // Rating - FIXED: Use Random.nextDouble() instead of range.random()
                    Surface(
                        color = SurfaceLight,
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
                                String.format("%.1f", Random.nextDouble(3.5, 5.0)), // FIXED HERE
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    // Sold count
                    Text(
                        "${product.sold} sold",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductListCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrls.first())
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(SurfaceLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, null, tint = TextTertiary)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            product.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            product.vendorName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Price
                    val displayPrice = product.discountPrice ?: product.price
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatCurrency(displayPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = OrangePrimary
                        )
                        product.discountPrice?.let {
                            Text(
                                formatCurrency(product.price),
                                fontSize = 12.sp,
                                color = TextTertiary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tags and stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category & Stock
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = OrangeSoft,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                product.category,
                                fontSize = 10.sp,
                                color = OrangePrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (product.totalStock < 10) {
                            Text(
                                if (product.totalStock == 0) "Out of Stock" else "${product.totalStock} left",
                                fontSize = 11.sp,
                                color = if (product.totalStock == 0) ErrorRed else WarningYellow,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Rating & Sold - FIXED: Use Random.nextDouble() instead of range.random()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                String.format("%.1f", Random.nextDouble(3.5, 5.0)), // FIXED HERE
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Text(
                            "${product.sold} sold",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }

                // Description preview
                if (product.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        product.description,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyProductState(
    query: String,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextTertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (query.isEmpty()) "No products available" else "No products found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (query.isEmpty())
                "Check back later for new arrivals!"
            else
                "Try adjusting your search or filters",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClearSearch,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Clear, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Search")
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProductListScreenPreview() {
    val sampleProducts = listOf(
        Product(
            productId = "1",
            vendorId = "v1",
            vendorName = "Tech Hub",
            name = "Wireless Earbuds Pro",
            description = "Premium sound quality with active noise cancellation",
            price = 45000.0,
            discountPrice = 39999.0,
            category = "Tech",
            imageUrls = listOf("https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400  "),
            variants = emptyList(),
            totalStock = 15,
            inStock = true,
            tags = listOf("wireless", "audio", "bluetooth"),
            views = 1200,
            sold = 89,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        ),
        Product(
            productId = "2",
            vendorId = "v2",
            vendorName = "Fashion Store",
            name = "Classic Cotton T-Shirt",
            description = "Comfortable everyday wear",
            price = 8500.0,
            category = "Fashion",
            imageUrls = emptyList(),
            variants = emptyList(),
            totalStock = 5,
            inStock = true,
            tags = listOf("clothing", "casual"),
            views = 800,
            sold = 45,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        ),
        Product(
            productId = "3",
            vendorId = "v3",
            vendorName = "Home Decor",
            name = "Modern Table Lamp",
            description = "Elegant design for any room",
            price = 25000.0,
            category = "Home",
            imageUrls = listOf("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400  "),
            variants = emptyList(),
            totalStock = 0,
            inStock = false,
            tags = listOf("lighting", "decor"),
            views = 500,
            sold = 12,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
    )

    BunnixTheme {
        ProductListScreen(
            products = sampleProducts,
            onBack = {},
            onProductClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductGridCardPreview() {
    BunnixTheme {
        ProductGridCard(
            product = Product(
                productId = "1",
                vendorId = "v1",
                vendorName = "Tech Hub",
                name = "Wireless Earbuds Pro",
                description = "Premium sound",
                price = 45000.0,
                discountPrice = 39999.0,
                category = "Tech",
                imageUrls = listOf("https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400  "),
                variants = emptyList(),
                totalStock = 8,
                inStock = true,
                tags = emptyList(),
                views = 100,
                sold = 50,
                createdAt = null,
                updatedAt = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListCardPreview() {
    BunnixTheme {
        ProductListCard(
            product = Product(
                productId = "2",
                vendorId = "v2",
                vendorName = "Fashion Store",
                name = "Classic Cotton T-Shirt",
                description = "Comfortable everyday wear with premium cotton fabric",
                price = 8500.0,
                category = "Fashion",
                imageUrls = emptyList(),
                variants = emptyList(),
                totalStock = 25,
                inStock = true,
                tags = emptyList(),
                views = 200,
                sold = 75,
                createdAt = null,
                updatedAt = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyProductStatePreview() {
    BunnixTheme {
        EmptyProductState(
            query = "wireless",
            onClearSearch = {}
        )
    }
}