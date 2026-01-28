package com.example.bunnix.frontend

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.backend.TrackingViewModel
import com.example.bunnix.model.Product
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.graphics.DashPathEffect
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn

@Composable
fun TrackOrderScreen(
    orderedItems: List<Product>,
    // Change this to allow a null ViewModel for the Preview
    viewModel: TrackingViewModel? = null
) {
    val isPreview = LocalInspectionMode.current

    // If in Preview, use a static position. If on phone, use the ViewModel.
    val deliveryGuyPos = if (isPreview) {
        LatLng(6.5244, 3.3792)
    } else {
        // Fallback for real app if VM is somehow null
        viewModel?.deliveryLocation?.value ?: LatLng(6.5244, 3.3792)
    }

    val userHomePos = LatLng(6.5300, 3.3800)

    Scaffold(
        topBar = {
            Text(
                "Track Order",
                modifier = Modifier.padding(16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. ORDER CARDS
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                orderedItems.forEach { item ->
                    TrackOrderItemCard(item)
                }
            }

            // 2. LIVE MAP SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF1B264F), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isPreview) {
                    // This shows in your Preview window instead of crashing
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Live Map (View on Phone)", color = Color.Gray)
                    }
                } else {
                    // This runs on your real phone or emulator
                    OSMTrackingMap(
                        deliveryPos = deliveryGuyPos,
                        userPos = userHomePos
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
fun TrackOrderItemCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2711C)), // Bunnix Orange
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Product Image
                Box(modifier = Modifier.size(60.dp).background(Color.White, RoundedCornerShape(8.dp)))

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(product.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("ORDER 8888  Qty:1", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicators (Ordered -> Packed -> Shipped -> Delivered)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusStep("Ordered", true)
                StatusStep("Packed", true)
                StatusStep("Shipped", false)
                StatusStep("Delivered", false)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(50.dp)) {
                SmallActionButton("Contact Vendor")
                SmallActionButton("Cancel Order")
                SmallActionButton("Paid & Received")
            }
        }
    }
}

@Composable
fun StatusStep(label: String, isDone: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(8.dp)
                .background(if (isDone) Color.Black else Color.Gray, CircleShape)
        )
        Text(label, fontSize = 10.sp, color = Color.White)
    }
}

@Composable
fun SmallActionButton(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.White,
        modifier = Modifier.height(30.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun OSMTrackingMap(deliveryPos: LatLng, userPos: LatLng) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK) // Standard OSM look
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(deliveryPos.latitude, deliveryPos.longitude))
            }
        },
        update = { mapView ->
            val deliveryGeoPoint = GeoPoint(deliveryPos.latitude, deliveryPos.longitude)
            val userGeoPoint = GeoPoint(userPos.latitude, userPos.longitude)

            // Clear previous overlays to avoid ghosts
            mapView.overlays.clear()

            // 1. Delivery Guy Marker
            val marker = Marker(mapView)
            marker.position = deliveryGeoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Delivery Guy"
            mapView.overlays.add(marker)

            // 2. Dashed Path (Polyline) to match your slide
            val line = Polyline()
            line.setPoints(listOf(deliveryGeoPoint, userGeoPoint))
            line.outlinePaint.color = android.graphics.Color.RED
            line.outlinePaint.strokeWidth = 5f
            // This makes it dashed!
            line.outlinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)

            mapView.overlays.add(line)
            mapView.invalidate() // Refresh map
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun TrackOrderPreview() {
    val dummyItems = listOf(
        Product(name = "Face Cap", price = "2000", image_url = "", description = "", category = "", location = "", quantity = "1"),
        Product(name = "Classic T-Shirt", price = "3500", image_url = "", description = "", category = "", location = "", quantity = "1")
    )
    TrackOrderScreen(orderedItems = dummyItems)
}

