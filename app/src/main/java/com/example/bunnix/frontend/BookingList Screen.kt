package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.bunnix.database.models.Service
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    services: List<Service> = emptyList(),
    onBack: () -> Unit,
    onServiceClick: (Service) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPriceRange by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Recommended") }

    // Categories from services
    val categories = remember(services) {
        listOf("All") + services.map { it.category }.distinct()
    }

    // Filter and sort services
    val filteredServices = remember(services, searchQuery, selectedCategory, selectedPriceRange, sortBy) {
        services.filter { service ->
            val matchesSearch = service.name.contains(searchQuery, ignoreCase = true) ||
                    service.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null ||
                    selectedCategory == "All" ||
                    service.category == selectedCategory

            val matchesPrice = when (selectedPriceRange) {
                "Under ₦5k" -> service.price < 5000
                "₦5k - ₦20k" -> service.price in 5000.0..20000.0
                "₦20k - ₦50k" -> service.price in 20000.0..50000.0
                "Above ₦50k" -> service.price > 50000
                else -> true
            }

            matchesSearch && matchesCategory && matchesPrice
        }.let { filtered ->
            when (sortBy) {
                "Price: Low to High" -> filtered.sortedBy { it.price }
                "Price: High to Low" -> filtered.sortedByDescending { it.price }
                "Top Rated" -> filtered.sortedByDescending { it.rating }
                "Most Booked" -> filtered.sortedByDescending { it.totalBookings }
                else -> filtered.sortedByDescending { it.rating }
            }
        }
    }

    Scaffold(
        topBar = {
            ModernServiceTopBar(
                serviceCount = filteredServices.size,
                onBack = onBack
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
                    placeholder = "Search services..."
                )

                // Quick Filters Row
                QuickFiltersRow(
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    categories = categories,
                    onFilterClick = { showFilters = true }
                )

                // Results Header
                ResultsHeader(
                    count = filteredServices.size,
                    sortBy = sortBy,
                    onSortClick = { /* Show sort dropdown */ }
                )

                // Service List
                if (filteredServices.isEmpty()) {
                    EmptyServiceState(
                        query = searchQuery,
                        onClearFilters = {
                            searchQuery = ""
                            selectedCategory = null
                            selectedPriceRange = null
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredServices,
                            key = { it.serviceId }
                        ) { service ->
                            ServiceCard(
                                service = service,
                                onClick = { onServiceClick(service) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilters) {
        FilterBottomSheet(
            selectedPriceRange = selectedPriceRange,
            onPriceRangeSelect = { selectedPriceRange = it },
            onDismiss = { showFilters = false },
            onApply = { showFilters = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernServiceTopBar(
    serviceCount: Int,
    onBack: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Book a Service",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "$serviceCount services available",
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
            singleLine = true
        )
    }
}

@Composable
private fun QuickFiltersRow(
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    categories: List<String>,
    onFilterClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter Button
            Surface(
                onClick = onFilterClick,
                color = SurfaceLight,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = TextPrimary
                    )
                }
            }

            // Category Chips
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(5).forEach { category ->
                    val isSelected = category == selectedCategory ||
                            (category == "All" && selectedCategory == null)

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onCategorySelect(if (category == "All") null else category)
                        },
                        label = { Text(category, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceLight
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultsHeader(
    count: Int,
    sortBy: String,
    onSortClick: () -> Unit
) {
    Surface(
        color = SurfaceLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$count services found",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

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
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
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
            .padding(horizontal = 16.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Service Image
                Box(contentAlignment = Alignment.TopStart) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        if (service.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(service.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = service.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(OrangeSoft, OrangePrimary.copy(alpha = 0.3f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    // Category Badge
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        color = OrangePrimary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            service.category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Name
                    Text(
                        service.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Vendor
                    Text(
                        service.vendorName,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Duration & Rating Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Duration
                        Surface(
                            color = TealAccent.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${service.duration} mins",
                                    fontSize = 12.sp,
                                    color = TealAccent,
                                    fontWeight = FontWeight.Medium
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
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "%.1f".format(service.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "(${service.totalBookings})",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price & Book Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                formatCurrency(service.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = OrangePrimary
                            )
                            if (service.totalBookings > 50) {
                                Text(
                                    "🔥 Popular",
                                    fontSize = 11.sp,
                                    color = OrangePrimary
                                )
                            }
                        }

                        Button(
                            onClick = onClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                "Book Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Availability Preview (if available)
            if (service.availability.isNotEmpty()) {
                Divider(color = SurfaceLight, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Next available: ${service.availability.first()}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    selectedPriceRange: String?,
    onPriceRangeSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val priceRanges = listOf(
        "Under ₦5k",
        "₦5k - ₦20k",
        "₦20k - ₦50k",
        "Above ₦50k"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Filter Services",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                "Price Range",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                priceRanges.forEach { range ->
                    FilterChip(
                        selected = selectedPriceRange == range,
                        onClick = {
                            onPriceRangeSelect(if (selectedPriceRange == range) null else range)
                        },
                        label = { Text(range) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply Button
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    "Apply Filters",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EmptyServiceState(
    query: String,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(OrangeSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = OrangePrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            if (query.isEmpty()) "No services available" else "No services found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (query.isEmpty())
                "Check back later for new services!"
            else
                "Try adjusting your search or filters",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Clear, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Filters")
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
        val hGapPx = 10.dp.roundToPx()
        val vGapPx = 10.dp.roundToPx()

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

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

// ===== PREVIEWS =====

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ServiceListScreenPreview() {
    val sampleServices = listOf(
        Service(
            serviceId = "s1",
            vendorId = "v1",
            vendorName = "Glow Up Salon",
            name = "Premium Hair Styling",
            description = "Professional hair styling with premium products",
            price = 15000.0,
            duration = 90,
            category = "Beauty",
            imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
            availability = listOf("Today 3PM", "Tomorrow 10AM", "Tomorrow 2PM"),
            totalBookings = 234,
            rating = 4.8,
            isActive = true,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        ),
        Service(
            serviceId = "s2",
            vendorId = "v2",
            vendorName = "TechFix Pro",
            name = "iPhone Screen Repair",
            description = "Fast and reliable screen replacement",
            price = 45000.0,
            duration = 60,
            category = "Tech",
            imageUrl = "",
            availability = listOf("Today 5PM", "Tomorrow 9AM"),
            totalBookings = 567,
            rating = 4.9,
            isActive = true,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        ),
        Service(
            serviceId = "s3",
            vendorId = "v3",
            vendorName = "HomeClean Services",
            name = "Deep House Cleaning",
            description = "Complete home cleaning service",
            price = 25000.0,
            duration = 180,
            category = "Home",
            imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=400",
            availability = listOf("Saturday 10AM", "Sunday 2PM"),
            totalBookings = 89,
            rating = 4.6,
            isActive = true,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
    )

    BunnixTheme {
        ServiceListScreen(
            services = sampleServices,
            onBack = {},
            onServiceClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceCardPreview() {
    BunnixTheme {
        ServiceCard(
            service = Service(
                serviceId = "s1",
                vendorId = "v1",
                vendorName = "Glow Up Salon",
                name = "Premium Hair Styling",
                description = "Professional styling",
                price = 15000.0,
                duration = 90,
                category = "Beauty",
                imageUrl = "",
                availability = listOf("Today 3PM"),
                totalBookings = 234,
                rating = 4.8,
                isActive = true,
                createdAt = null,
                updatedAt = null
            ),
            onClick = {}
        )
    }
}