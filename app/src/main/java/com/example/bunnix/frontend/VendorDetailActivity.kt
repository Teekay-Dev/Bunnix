package com.example.bunnix.frontend

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bunnix.R
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.ui.theme.BunnixTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

// MODERN COLORS
private val OrangePrimary = Color(0xFFFF6B35)
private val OrangeLight = Color(0xFFFF8C61)
private val OrangeSoft = Color(0xFFFFF0EB)
private val TealAccent = Color(0xFF2EC4B6)
private val SurfaceLight = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val TextTertiary = Color(0xFF9CA3AF)
private val StarYellow = Color(0xFFFFB800)
private val SuccessGreen = Color(0xFF10B981)

enum class VendorTab {
    Products, Services, Reviews, About
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendor: VendorProfile,
    products: List<Product> = emptyList(),
    services: List<Service> = emptyList(),
    onBack: () -> Unit = {},
    onChat: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onServiceClick: (Service) -> Unit = {},
    onBookService: () -> Unit = {},
    @DrawableRes fallbackCoverRes: Int = R.drawable.bites_background_pic // ✅ ADDED HERE
) {
    var isVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(VendorTab.Products) }
    var isLiked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            VendorHeader(
                vendor = vendor,
                onBack = onBack,
                isLiked = isLiked,
                onLikeToggle = { isLiked = it },
                fallbackCoverRes = fallbackCoverRes // ✅ PASS IT HERE
            )

            Spacer(modifier = Modifier.height(20.dp)) // ✅ FIXED: Was 120.dp, now 20.dp

            ActionButtonsRow(onChat = onChat)

            TabSelection(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            when (selectedTab) {
                VendorTab.Products -> ProductsTab(products, onProductClick)
                VendorTab.Services -> ServicesTab(services, onServiceClick)
                VendorTab.Reviews -> ReviewsTab(vendor)
                VendorTab.About -> AboutTab(vendor = vendor)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (selectedTab == VendorTab.Services && services.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = onBookService,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = { Icon(Icons.Default.CalendarToday, null) },
                text = { Text("Book Now") },
                containerColor = OrangePrimary,
                contentColor = Color.White
            )
        }
    }
}

@Composable
private fun VendorHeader(
    vendor: VendorProfile,
    onBack: () -> Unit,
    isLiked: Boolean,
    onLikeToggle: (Boolean) -> Unit,
    @DrawableRes fallbackCoverRes: Int = R.drawable.bites_background_pic
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Cover Photo with Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            // Background Image - supports URL or drawable
            val context = LocalContext.current

            when {
                // 1. Try URL first if available
                vendor.coverPhotoUrl.isNotEmpty() -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(vendor.coverPhotoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cover Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                // 2. Use drawable resource (R.drawable.xxx)
                fallbackCoverRes != 0 -> {
                    Image(
                        painter = painterResource(id = fallbackCoverRes),
                        contentDescription = "Cover Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                // 3. Fallback gradient
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(OrangePrimary, OrangeLight)))
                    )
                }
            }

            // Gradient overlay for better text contrast (always applied when there's an image)
            if (vendor.coverPhotoUrl.isNotEmpty() || fallbackCoverRes != 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }

            // Top Controls (Back button, Like button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                IconButton(onClick = { onLikeToggle(!isLiked) }) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isLiked) Color.Red else TextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Vendor Info Card - Horizontal layout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 160.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo/Avatar
                // In VendorHeader, replace the logo section:

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(2.dp, Color.White),
                    shadowElevation = 4.dp
                ) {
                    // Just show the placeholder letter, remove the AsyncImage for logoUrl
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().background(OrangeSoft)
                    ) {
                        Text(
                            vendor.businessName.take(1).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Business Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        vendor.businessName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        vendor.category,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating & Reviews Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            vendor.rating.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            "(${vendor.totalReviews})",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            vendor.address.split(",").firstOrNull() ?: "2.5 km",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // "Open now" Status Badge
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (vendor.isAvailable) "Open now" else "Closed",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(onChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onChat,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chat Now", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TabSelection(selectedTab: VendorTab, onTabSelected: (VendorTab) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            VendorTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) OrangePrimary else Color.Transparent
                ) {
                    Text(
                        tab.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsTab(products: List<Product>, onProductClick: (Product) -> Unit) {
    if (products.isEmpty()) {
        EmptyTabState("No products available from this vendor.")
    } else {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            products.forEach { product ->
                ProductRow(product, onProductClick)
                Divider(color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun ServicesTab(services: List<Service>, onServiceClick: (Service) -> Unit) {
    if (services.isEmpty()) {
        EmptyTabState("No services available from this vendor.")
    } else {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            services.forEach { service ->
                ServiceRow(service, onServiceClick)
                Divider(color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun ProductRow(product: Product, onClick: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (product.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrls.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.background(OrangeSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory, null, tint = OrangePrimary)
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(formatCurrency(product.price), color = OrangePrimary, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextTertiary)
    }
}

@Composable
private fun ServiceRow(service: Service, onClick: (Service) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(service) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(12.dp),
            color = TealAccent.copy(alpha = 0.1f)
        ) {
            Icon(Icons.Default.Build, null, tint = TealAccent, modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(service.name, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                "${service.duration} mins • ${formatCurrency(service.price)}",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextTertiary)
    }
}

@Composable
private fun ReviewsTab(vendor: VendorProfile) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Reviews coming soon from backend...",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AboutTab(vendor: VendorProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("About Vendor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                vendor.description,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(Icons.Default.LocationOn, "Address", vendor.address)
            InfoRow(Icons.Default.Phone, "Contact", vendor.phone)
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextTertiary)
            Text(value, fontSize = 14.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun EmptyTabState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = TextTertiary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// PREVIEW DATA
private val sampleVendor = VendorProfile(
    businessName = "Bunny's Pet Grooming",
    category = "Pet Services",
    rating = 4.8,
    totalReviews = 124,
    description = "Professional pet grooming services with over 5 years of experience. We offer bathing, haircuts, nail trimming, and spa treatments for your furry friends.",
    address = "123 Pet Street, Lagos, Nigeria",
    phone = "+234 801 234 5678",
    coverPhotoUrl = ""
)

private val sampleProducts = listOf(
    Product(name = "Premium Dog Shampoo", price = 4500.0, imageUrls = emptyList()),
    Product(name = "Pet Nail Clippers", price = 3200.0, imageUrls = emptyList()),
    Product(name = "Flea Collar", price = 1800.0, imageUrls = emptyList())
)

private val sampleServices = listOf(
    Service(name = "Full Grooming", price = 15000.0, duration = 120),
    Service(name = "Bath & Brush", price = 8000.0, duration = 60),
    Service(name = "Nail Trimming", price = 3000.0, duration = 30)
)

// PREVIEWS - ✅ FIXED: Use R.drawable.default_cover (make sure this exists in your res/drawable)
@Preview(showBackground = true, showSystemUi = true, name = "Products Tab")
@Composable
fun VendorDetailScreenPreviewProducts() {
    BunnixTheme {
        VendorDetailScreen(
            vendor = sampleVendor,
            products = sampleProducts,
            services = sampleServices,
            onBack = {},
            onChat = {},
            fallbackCoverRes = R.drawable.beauty // ✅ Use your actual drawable name here
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Services Tab")
@Composable
fun VendorDetailScreenPreviewServices() {
    BunnixTheme {
        VendorDetailScreen(
            vendor = sampleVendor,
            products = sampleProducts,
            services = sampleServices,
            onBack = {},
            onChat = {},
            fallbackCoverRes = R.drawable.tech
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty State")
@Composable
fun VendorDetailScreenPreviewEmpty() {
    BunnixTheme {
        VendorDetailScreen(
            vendor = sampleVendor.copy(businessName = "New Vendor"),
            products = emptyList(),
            services = emptyList(),
            onBack = {},
            onChat = {},
            fallbackCoverRes = R.drawable.hero_pic
        )
    }
}