package com.example.bunnix.frontend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.bunnix.model.ServiceItem
import com.example.bunnix.ui.theme.BunnixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(onBack: () -> Unit,
                      onServiceClick: (String, String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val services = listOf(
        ServiceItem("Personal Styling", "Fashion consultation", "2 hrs", "$75.00"),
        ServiceItem("Makeup Artistry", "Event glam", "1.5 hrs", "$50.00"),
        ServiceItem("Hair Braiding", "All styles", "4 hrs", "$120.00")
    ).filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            Column(Modifier.background(Color.White)) {
                // Back Button and Title Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text("Services", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search services...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD35400),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F8F8)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(50.dp).background(Color(0xFFFFF3EE), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ContentCut, null, tint = Color(0xFFD35400))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(service.title, fontWeight = FontWeight.Bold)
                            Text(service.price, color = Color(0xFF1A56BE), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                onServiceClick(service.title, service.price)
                            }
                        ) {
                            Text("Book")
                        }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Service List & Search")
@Composable
fun ServiceListPreview() {
    BunnixTheme {
        ServiceListScreen(
            onBack = {},
            onServiceClick = { service, price -> }
        )
    }
}