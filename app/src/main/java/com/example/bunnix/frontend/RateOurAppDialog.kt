package com.example.bunnix.frontend
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun RateOurAppDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B264F))
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Rate Our App", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Stars
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(5) { index ->
                        val isSelected = index < rating
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star ${index + 1}",
                            tint = if (isSelected) Color(0xFFF2711C) else Color.Gray,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { rating = index + 1 } // Updates the state
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Review", fontSize = 20.sp, fontWeight = FontWeight.Medium)

                // Actual Input Field for the review
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Tell us what you think...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = { onSubmit(rating, reviewText) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                    enabled = rating > 0 // Only allow submit if a star is picked
                ) {
                    Text("Submit Review", color = Color.White)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RateOurAppDialogPreview() {
    // We wrap it in a Box to simulate the app screen behind the popup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        RateOurAppDialog(
            onDismiss = {},
            onSubmit = { rating, review ->
                println("Preview Submit: $rating stars")
            }
        )
    }
}