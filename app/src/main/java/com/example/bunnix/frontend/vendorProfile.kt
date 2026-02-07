//package com.example.bunnix.frontend
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
////import com.example.bunnix.backend.VendorProfileViewModel
//import com.example.bunnix.ui.theme.OrangeStart
//import com.example.bunnix.ui.theme.OrangeEnd
//
//@Composable
//fun VendorProfileScreen(
//    navController: NavHostController,
//    viewModel: VendorProfileViewModel // Ensure this is exactly 'viewModel'
//){
//    val vendor by viewModel.vendorState.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // --- Header ---
//        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//            Text(
//                text = "‹",
//                fontSize = 30.sp,
//                color = OrangeEnd,
//                modifier = Modifier.clickable { navController.popBackStack() }
//            )
//            Spacer(Modifier.width(20.dp))
//            Text("Vendor Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold)
//        }
//
//        Spacer(modifier = Modifier.height(30.dp))
//
//        vendor?.let { data ->
//            // Profile Image Placeholder
//            Box(
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFFF5F5F5)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(data.firstName.take(1), fontSize = 40.sp, color = OrangeEnd, fontWeight = FontWeight.Bold)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Name and Business
//            Text("${data.firstName} ${data.surName}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//            Text(data.businessName, fontSize = 16.sp, color = Color.Gray)
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Stats Row (Rating/Distance)
//            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
//                InfoBadge(label = "Rating", value = "${data.rating}⭐")
//                InfoBadge(label = "Category", value = data.category)
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // About Section
//            Column(Modifier.fillMaxWidth()) {
//                Text("About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Spacer(Modifier.height(8.dp))
//                Text(data.about, color = Color.DarkGray, lineHeight = 20.sp)
//            }
//        }
//    }
//}
//
//@Composable
//fun InfoBadge(label: String, value: String) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(value, fontWeight = FontWeight.Bold, color = OrangeEnd)
//        Text(label, fontSize = 12.sp, color = Color.Gray)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ProfilePreview() {
//    val navController = rememberNavController()
//    val viewModel = VendorProfileViewModel()
//    VendorProfileScreen(navController = navController, viewModel = viewModel)
//}