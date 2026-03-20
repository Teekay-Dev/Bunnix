package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.database.models.CartItem
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.firebase.collections.CartCollection
import com.example.bunnix.database.firebase.FirebaseManager
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

private val OrangePrimary = Color(0xFFFF6B35)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val CardBackground = Color(0xFFF8F9FA)
private val TextPrimary = Color(0xFF2C3E50)
private val TextSecondary = Color(0xFF7F8C8D)
private val StarYellow = Color(0xFFFFC107)
private val DiscountRed = Color(0xFFEF4444)
private val StockWarning = Color(0xFFFFA000)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    products: List<Product> = emptyList(),
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userId = FirebaseManager.getCurrentUserId()

    // Simple filtering
    val filteredProducts = remember(products, searchQuery, selectedFilter) {
        var result = if (searchQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.name.contains(searchQuery, true) ||
                        it.vendorName.contains(searchQuery, true) ||
                        it.category.contains(searchQuery, true)
            }
        }

        when (selectedFilter) {
            "Popular" -> result.sortedByDescending { it.sold }
            "Cheap" -> result.sortedBy { it.price }
            "Expensive" -> result.sortedByDescending { it.price }
            else -> result
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Product Lists",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Search, null, tint = TextSecondary,
                            modifier = Modifier.size(20.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 15.sp, color = TextPrimary),
                            singleLine = true,
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search products...", fontSize = 15.sp, color = TextSecondary)
                                }
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" },
                                modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, null, tint = TextSecondary,
                                    modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Filter button with ANCHORED dropdown
                Box {
                    Surface(
                        onClick = { showFilterMenu = true },
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Tune, null, tint = OrangePrimary,
                                modifier = Modifier.size(22.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("All") },
                            onClick = { selectedFilter = "All"; showFilterMenu = false })
                        DropdownMenuItem(text = { Text("Popular") },
                            onClick = { selectedFilter = "Popular"; showFilterMenu = false })
                        DropdownMenuItem(text = { Text("Cheap") },
                            onClick = { selectedFilter = "Cheap"; showFilterMenu = false })
                        DropdownMenuItem(text = { Text("Expensive") },
                            onClick = { selectedFilter = "Expensive"; showFilterMenu = false })
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "No products found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        if (searchQuery.isNotBlank()) {
                            TextButton(onClick = { searchQuery = "" }) {
                                Text("Clear search", color = OrangePrimary)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredProducts,
                        key = { it.productId }
                    ) { product ->
                        SimpleProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onAddToCart = { clickedProduct ->
                                if (userId != null) {
                                    val cartItem = CartItem(
                                        id = clickedProduct.productId,
                                        productId = clickedProduct.productId,
                                        name = clickedProduct.name,
                                        vendorId = clickedProduct.vendorId,
                                        vendorName = clickedProduct.vendorName,
                                        price = clickedProduct.discountPrice ?: clickedProduct.price,
                                        originalPrice = if(clickedProduct.discountPrice != null) clickedProduct.price else null,
                                        imageUrl = clickedProduct.imageUrls.firstOrNull() ?: "",
                                        quantity = 1
                                    )
                                    scope.launch {
                                        CartCollection.addToCart(userId, cartItem)
                                        onAddToCart()
                                    }
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = CardBackground
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        color = TextPrimary
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Search products...",
                                fontSize = 15.sp,
                                color = TextSecondary
                            )
                        }
                        innerTextField()
                    }
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Surface(
            onClick = onFilterClick,
            shape = RoundedCornerShape(12.dp),
            color = CardBackground,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = OrangePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


@Composable
private fun SimpleProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
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
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = OrangePrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                product.discountPrice?.let { discount ->
                    val discountPercent = ((product.price - discount) / product.price * 100).toInt()
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                        color = DiscountRed,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "-$discountPercent%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (product.totalStock < 10 && product.totalStock > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        color = StockWarning,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "${product.totalStock} left",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (product.totalStock == 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        color = DiscountRed,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Sold Out",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Surface(
                        color = OrangePrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            product.category,
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        product.vendorName,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "${product.sold} sold",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                formatCurrency(product.discountPrice ?: product.price),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )

                            product.discountPrice?.let {
                                Text(
                                    formatCurrency(product.price),
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onAddToCart(product) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier.height(40.dp),
                        enabled = product.totalStock > 0
                    ) {
                        Icon(
                            Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Add",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {
    val sampleProducts = listOf(
        Product(
            productId = "1",
            vendorId = "v1",
            vendorName = "Tech Hub Store",
            name = "Wireless Earbuds Pro",
            description = "Premium sound quality",
            price = 45000.0,
            discountPrice = null,
            category = "Tech",
            imageUrls = listOf("https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400 "),
            variants = emptyList(),
            totalStock = 8,
            inStock = true,
            tags = emptyList(),
            views = 1200,
            sold = 89,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        ),
        Product(
            productId = "2",
            vendorId = "v2",
            vendorName = "Fashion Boutique",
            name = "Classic Cotton T-Shirt",
            description = "Comfortable everyday wear",
            price = 8500.0,
            discountPrice = null,
            category = "Fashion",
            imageUrls = emptyList(),
            variants = emptyList(),
            totalStock = 25,
            inStock = true,
            tags = emptyList(),
            views = 800,
            sold = 145,
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