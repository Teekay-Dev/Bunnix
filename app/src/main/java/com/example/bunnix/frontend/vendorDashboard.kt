package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.bunnix.ui.theme.OrangeStart
import com.example.bunnix.ui.theme.OrangeEnd

@Composable
fun VendorDashboardScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Text("Vestimate", color = OrangeEnd, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(30.dp))
        Text("Choose your area", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        val menuItems = listOf("Buying", "Selling", "Trades", "Videos", "Deals", "Case Study")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems) { item ->
                val isBuying = item == "Buying"
                val bg = if (isBuying) Brush.verticalGradient(listOf(OrangeStart, OrangeEnd))
                else Brush.linearGradient(listOf(Color.White, Color.White))

                Card(
                    modifier = Modifier.height(130.dp).clickable {
                        if (isBuying) navController.navigate(Screen.ManageInventory.route)
                    },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(bg), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if(isBuying) "🛒" else "📁", fontSize = 30.sp)
                            Text(item, color = if(isBuying) Color.White else Color.Gray, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val navController = rememberNavController()
    VendorDashboardScreen(navController = navController)
}