package com.example.bunnix.vendorUI.screens.vendor.products

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bunnix.OrangeSoft
import com.example.bunnix.TealAccent
import com.example.bunnix.ui.theme.*
import com.example.bunnix.viewmodel.ProductsViewModel
import com.example.bunnix.viewmodel.ServicesViewModel
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Service
import com.google.firebase.Timestamp
import com.example.bunnix.ui.theme.BunnixTheme

enum class InventoryTab {
    Products, Services
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageInventoryScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel = hiltViewModel(),
    servicesViewModel: ServicesViewModel = hiltViewModel()
) {
    val products by productsViewModel.products.collectAsState()
    val services by servicesViewModel.services.collectAsState()
    val isLoadingProducts by productsViewModel.isLoading.collectAsState()
    val isLoadingServices by servicesViewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(InventoryTab.Products) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Pair<String, String>?>(null) } // (id, type)

    LaunchedEffect(Unit) {
        productsViewModel.loadProducts()
        servicesViewModel.loadServices()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Inventory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OrangePrimaryModern,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == InventoryTab.Products) {
                        navController.navigate("vendor/product/add")
                    } else {
                        navController.navigate("vendor/service/add")
                    }
                },
                containerColor = OrangePrimaryModern,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = LightGrayBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InventoryTab.values().forEach { tab ->
                        val isSelected = tab == selectedTab
                        Surface(
                            onClick = { selectedTab = tab },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) OrangePrimaryModern else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (tab == InventoryTab.Products)
                                        Icons.Default.Inventory else Icons.Default.Build,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    tab.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Content
            when (selectedTab) {
                InventoryTab.Products -> {
                    if (isLoadingProducts) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangePrimaryModern)
                        }
                    } else if (products.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Inventory,
                            message = "No products yet",
                            buttonText = "Add Product",
                            onButtonClick = { navController.navigate("vendor/product/add") }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(products) { product ->
                                ProductInventoryCard(
                                    name = product.name,
                                    price = product.price,
                                    stock = product.totalStock,
                                    imageUrl = product.imageUrls.firstOrNull() ?: "",
                                    onEdit = {
                                        navController.navigate("vendor/product/edit/${product.productId}")
                                    },
                                    onDelete = {
                                        itemToDelete = Pair(product.productId, "product")
                                        showDeleteDialog = true
                                    },
                                    onToggleAvailability = { isAvailable ->
                                        productsViewModel.toggleProductAvailability(
                                            product.productId,
                                            isAvailable
                                        )
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }

                InventoryTab.Services -> {
                    if (isLoadingServices) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangePrimaryModern)
                        }
                    } else if (services.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Build,
                            message = "No services yet",
                            buttonText = "Add Service",
                            onButtonClick = { navController.navigate("vendor/service/add") }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(services) { service ->
                                ServiceInventoryCard(
                                    name = service.name,
                                    price = service.price,
                                    duration = service.duration,
                                    imageUrl = service.imageUrl,
                                    onEdit = {
                                        navController.navigate("vendor/service/edit/${service.serviceId}")
                                    },
                                    onDelete = {
                                        itemToDelete = Pair(service.serviceId, "service")
                                        showDeleteDialog = true
                                    },
                                    onToggleAvailability = { isActive ->
                                        servicesViewModel.toggleServiceAvailability(
                                            service.serviceId,
                                            isActive
                                        )
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Delete ${if (itemToDelete?.second == "product") "Product" else "Service"}?") },
            text = { Text("Are you sure you want to delete this item? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (itemToDelete?.second == "product") {
                            productsViewModel.deleteProduct(itemToDelete?.first ?: "")
                        } else {
                            servicesViewModel.deleteService(itemToDelete?.first ?: "")
                        }
                        showDeleteDialog = false
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    itemToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProductInventoryCard(
    name: String,
    price: Double,
    stock: Int,
    imageUrl: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAvailability: (Boolean) -> Unit
) {
    var isAvailable by remember { mutableStateOf(stock > 0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = OrangeSoft
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = OrangePrimaryModern,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatCurrency(price),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryModern
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        tint = if (stock > 0) SuccessGreen else Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        if (stock > 0) "$stock in stock" else "Out of stock",
                        fontSize = 13.sp,
                        color = if (stock > 0) SuccessGreen else Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = OrangePrimaryModern,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Switch(
                    checked = isAvailable,
                    onCheckedChange = {
                        isAvailable = it
                        onToggleAvailability(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SuccessGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}

@Composable
private fun ServiceInventoryCard(
    name: String,
    price: Double,
    duration: Int,
    imageUrl: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAvailability: (Boolean) -> Unit
) {
    var isActive by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = TealAccent.copy(alpha = 0.1f)
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatCurrency(price),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryModern
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "$duration mins",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = OrangePrimaryModern,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = {
                        isActive = it
                        onToggleAvailability(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SuccessGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(buttonText, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}





// ===== PREVIEW DATA =====
private val sampleProducts = listOf(
    Product(
        productId = "1",
        vendorId = "v1",
        vendorName = "Tech Store",
        name = "Wireless Headphones",
        description = "Premium sound quality",
        price = 45000.0,
        discountPrice = null,
        category = "Electronics",
        imageUrls = emptyList(),
        variants = emptyList(),
        totalStock = 15,
        inStock = true,
        tags = emptyList(),
        views = 120,
        sold = 45,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    ),
    Product(
        productId = "2",
        vendorId = "v1",
        vendorName = "Tech Store",
        name = "Smart Watch",
        description = "Track your fitness",
        price = 85000.0,
        discountPrice = 75000.0,
        category = "Electronics",
        imageUrls = emptyList(),
        variants = emptyList(),
        totalStock = 0,
        inStock = false,
        tags = emptyList(),
        views = 89,
        sold = 23,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )
)

private val sampleServices = listOf(
    Service(
        serviceId = "1",
        vendorId = "v1",
        vendorName = "Beauty Salon",
        name = "Hair Cut & Styling",
        description = "Professional haircut",
        price = 15000.0,
        duration = 60,
        category = "Beauty & Spa",
        imageUrl = "",
        availability = listOf("Mon-Fri: 9AM-5PM"),
        totalBookings = 45,
        rating = 4.8,
        isActive = true,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    ),
    Service(
        serviceId = "2",
        vendorId = "v1",
        vendorName = "Beauty Salon",
        name = "Manicure & Pedicure",
        description = "Full nail treatment",
        price = 12000.0,
        duration = 90,
        category = "Beauty & Spa",
        imageUrl = "",
        availability = listOf("Mon-Fri: 9AM-5PM"),
        totalBookings = 67,
        rating = 4.9,
        isActive = true,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )
)

// ===== PREVIEWS =====
@Preview(showBackground = true, showSystemUi = true, name = "Product Card")
@Composable
fun ProductInventoryCardPreview() {
    BunnixTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProductInventoryCard(
                name = "Wireless Headphones",
                price = 45000.0,
                stock = 15,
                imageUrl = "",
                onEdit = {},
                onDelete = {},
                onToggleAvailability = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Service Card")
@Composable
fun ServiceInventoryCardPreview() {
    BunnixTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ServiceInventoryCard(
                name = "Hair Cut & Styling",
                price = 15000.0,
                duration = 60,
                imageUrl = "",
                onEdit = {},
                onDelete = {},
                onToggleAvailability = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty State")
@Composable
fun EmptyStatePreview() {
    BunnixTheme {
        EmptyState(
            icon = Icons.Default.Inventory,
            message = "No products yet",
            buttonText = "Add Product",
            onButtonClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Products Tab - With Items")
@Composable
fun InventoryScreenProductsPreview() {
    BunnixTheme {
        var selectedTab by remember { mutableStateOf(InventoryTab.Products) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "My Inventory",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = OrangePrimaryModern,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = OrangePrimaryModern,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            containerColor = LightGrayBg
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tab Selector
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InventoryTab.values().forEach { tab ->
                            val isSelected = tab == selectedTab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) OrangePrimaryModern else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (tab == InventoryTab.Products)
                                            Icons.Default.Inventory else Icons.Default.Build,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        tab.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Product List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleProducts) { product ->
                        ProductInventoryCard(
                            name = product.name,
                            price = product.price,
                            stock = product.totalStock,
                            imageUrl = "",
                            onEdit = {},
                            onDelete = {},
                            onToggleAvailability = {}
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Services Tab - With Items")
@Composable
fun InventoryScreenServicesPreview() {
    BunnixTheme {
        var selectedTab by remember { mutableStateOf(InventoryTab.Services) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "My Inventory",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = OrangePrimaryModern,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = OrangePrimaryModern,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            containerColor = LightGrayBg
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tab Selector
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InventoryTab.values().forEach { tab ->
                            val isSelected = tab == selectedTab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) OrangePrimaryModern else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (tab == InventoryTab.Products)
                                            Icons.Default.Inventory else Icons.Default.Build,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        tab.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Service List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleServices) { service ->
                        ServiceInventoryCard(
                            name = service.name,
                            price = service.price,
                            duration = service.duration,
                            imageUrl = "",
                            onEdit = {},
                            onDelete = {},
                            onToggleAvailability = {}
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Empty Products")
@Composable
fun InventoryScreenEmptyProductsPreview() {
    BunnixTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "My Inventory",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = OrangePrimaryModern,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = OrangePrimaryModern,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            containerColor = LightGrayBg
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyState(
                    icon = Icons.Default.Inventory,
                    message = "No products yet",
                    buttonText = "Add Product",
                    onButtonClick = {}
                )
            }
        }
    }
}