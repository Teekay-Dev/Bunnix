package com.example.bunnix.frontend

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.model.Product

@Composable
fun CartScreen(
    cartItems: List<Product>,
    onProceed: () -> Unit,
    onContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("My Cart", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = { BunnixBottomNav() } // Reusing your navigation
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            // 1. SCROLLABLE LIST OF ITEMS
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item)
                }
            }

            Divider(thickness = 1.dp, color = Color.Black)

            // 2. ORDER SUMMARY SECTION
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal")
                    Text("₦5,500") // We can calculate this dynamically later
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("₦5,500", fontWeight = FontWeight.Bold)
                }
            }

            Divider(thickness = 1.dp, color = Color.Black)

            // 3. PAYMENT & ACTION SECTION
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = true, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFF2711C)))
                    Text("Pay on Delivery/Home Delivery", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onProceed,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Proceed To Order", color = Color.White, fontSize = 18.sp)
                }

                TextButton(onClick = onContinue) {
                    Text("Continue Shopping", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(product: Product) {
    var count by remember { mutableIntStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Image with "Remove" Overlay ---
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, Color.LightGray)
                .background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Placeholder for the actual Image
            Text(
                text = "Product Image",
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            // Remove Button Overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Remove Logic */ },
                color = Color.White.copy(alpha = 0.8f) // Slight transparency
            ) {
                Text(
                    text = "Remove",
                    color = Color(0xFFF2711C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // --- Details Section ---
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "₦${product.price}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Selector (- 1 +)
            Row(
                modifier = Modifier
                    .width(130.dp)
                    .height(36.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "—",
                    modifier = Modifier.clickable { if (count > 1) count-- },
                    fontWeight = FontWeight.Bold
                )
                Text(text = "$count", fontWeight = FontWeight.Bold)
                Text(
                    text = "+",
                    modifier = Modifier.clickable { count++ },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    // Creating dummy data based on your UI slide
    val dummyCartItems = listOf(
        Product(
            name = "Face Cap",
            price = "2,000",
            image_url = "cap_url",
            description = "",
            category = "Accessories",
            location = "Lagos",
            quantity = "1"
        ),
        Product(
            name = "Classic T-Shirt",
            price = "3,500",
            image_url = "shirt_url",
            description = "",
            category = "Fashion",
            location = "Lagos",
            quantity = "1"
        )
    )

    CartScreen(
        cartItems = dummyCartItems,
        onProceed = {},
        onContinue = {}
    )
}
@Composable
fun BunnixBottomNav() {
    NavigationBar(containerColor = Color.White, modifier = Modifier.border(1.dp, Color.Black)) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, null) }, label = { Text("Cart") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chat") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = false, onClick = {})
    }
}