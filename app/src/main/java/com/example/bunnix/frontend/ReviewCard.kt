//package com.example.bunnix.frontend
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.bunnix.model.ReviewItem
//
//@Composable
//fun ReviewCard(review: ReviewItem) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//    ) {
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(review.userName, fontWeight = FontWeight.Bold)
//
//            Row {
//                repeat(review.rating) {   // ✅ FIXED
//                    Icon(
//                        Icons.Default.Star,
//                        contentDescription = null,
//                        tint = Color(0xFFFFC107),
//                        modifier = Modifier.size(14.dp)
//                    )
//                }
//            }
//        }
//
//        Spacer(Modifier.height(4.dp))
//        Text(review.comment, color = Color.DarkGray)
//        Text(review.timeAgo, fontSize = 12.sp, color = Color.Gray)
//    }
//}
