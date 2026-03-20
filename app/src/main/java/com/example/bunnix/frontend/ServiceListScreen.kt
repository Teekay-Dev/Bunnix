package com.example.bunnix.frontend

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
import com.example.bunnix.database.models.Service
import java.text.NumberFormat
import java.util.*

private val OrangePrimary = Color(0xFFFF6B35)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val CardBackground = Color(0xFFF8F9FA)
private val TextPrimary = Color(0xFF2C3E50)
private val TextSecondary = Color(0xFF7F8C8D)
private val StarYellow = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    services: List<Service> = emptyList(),
    onBack: () -> Unit,
    onServiceClick: (Service) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filteredServices = remember(services, searchQuery, selectedTab) {
        services.filter { service ->
            val matchesSearch = searchQuery.isBlank() ||
                    service.name.contains(searchQuery, true) ||
                    service.vendorName.contains(searchQuery, true) ||
                    service.category.contains(searchQuery, true)
            val matchesTab = when (selectedTab) {
                "Popular" -> service.rating >= 4.5
                else -> true
            }
            matchesSearch && matchesTab
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Service Lists", fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Search + Filter row with anchored dropdown
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
                                    Text("Search service..", fontSize = 15.sp, color = TextSecondary)
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
                            onClick = { selectedTab = "All"; showFilterMenu = false })
                        DropdownMenuItem(text = { Text("Popular") },
                            onClick = { selectedTab = "Popular"; showFilterMenu = false })
                        DropdownMenuItem(text = { Text("Nearby") },
                            onClick = { selectedTab = "Nearby"; showFilterMenu = false })
                    }
                }
            }

            if (services.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No services available yet", fontSize = 18.sp, color = TextSecondary)
                }
            } else if (filteredServices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, tint = TextSecondary,
                            modifier = Modifier.size(64.dp))
                        Text("No services found", fontSize = 18.sp,
                            fontWeight = FontWeight.Medium, color = TextPrimary)
                        if (searchQuery.isNotBlank()) {
                            TextButton(onClick = { searchQuery = "" }) { Text("Clear search") }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = filteredServices, key = { it.serviceId }) { service ->
                        SimpleServiceCard(service = service, onClick = { onServiceClick(service) })
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SimpleServiceCard(service: Service, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(12.dp),
                color = Color.White) {
                if (service.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(service.imageUrl).crossfade(true).build(),
                        contentDescription = service.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Build, null,
                            tint = OrangePrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(service.name, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = StarYellow,
                            modifier = Modifier.size(16.dp))
                        Text("%.1f".format(service.rating), fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Text(service.category, fontSize = 14.sp, color = TextSecondary, maxLines = 1)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = TextSecondary,
                        modifier = Modifier.size(14.dp))
                    Text(service.vendorName, fontSize = 13.sp, color = TextSecondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = OrangePrimary,
                                modifier = Modifier.size(14.dp))
                            Text("${service.duration} mins", fontSize = 13.sp,
                                color = TextSecondary, fontWeight = FontWeight.Medium)
                        }
                        Text(formatServiceCurrency(service.price), fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = OrangePrimary)
                    }
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Book", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatServiceCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    formatter.currency = java.util.Currency.getInstance("NGN")
    return formatter.format(amount).replace("NGN", "₦")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceListScreenPreview() {
    ServiceListScreen(services = emptyList(), onBack = {}, onServiceClick = {})
}