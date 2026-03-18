package com.example.bunnix.frontend

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.R
import kotlinx.coroutines.delay



@Composable
fun AnimatedSplashScreen(onComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    // Scale animation with bounce
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    // Text fade in
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 400
        ),
        label = "alpha"
    )

    // Infinite pulsing effect for background
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Glow alpha animation
//    val glowAlpha by infiniteTransition.animateFloat(
//        initialValue = 0.2f,
//        targetValue = 0.6f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1500, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "glowAlpha"
//    )

    // Start animations and navigate after delay
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(6000) // 2.5 seconds
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFEAD3AA),
                        Color(0xFFF1DEA3)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing glow background
//            Box(
//                modifier = Modifier
//                    .size(300.dp)
//                    .scale(pulseScale)
////                    .alpha(glowAlpha)
//                    .background(
//                        Color.White.copy(alpha = 0.15f),
//                        shape = CircleShape
//                    )
//            )

            // Your Bunnix logo with animations
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bunnix_2),
                    contentDescription = "Bunnix Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(48.dp))

            // App name with fade in
            Text(
                "Bunnix",
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(Modifier.height(12.dp))

            // Tagline with fade in
            Text(
                "Shop & Book Services",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.95f),
                modifier = Modifier.alpha(alpha)
            )

            Spacer(Modifier.height(80.dp))

            // Animated loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.alpha(alpha)
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 600,
                                delayMillis = index * 200
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(dotAlpha)
                            .background(
                                Color.Black,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, device = "id:pixel_5", name = "Splash Screen")
@Composable
fun SplashScreenStaticPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFEAD3AA),
                        Color(0xFFF1DEA3)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(R.drawable.bunnix_2),
                contentDescription = "Bunnix Logo",
                modifier = Modifier.size(220.dp)
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Bunnix",
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Shop & Book Services",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.95f)
            )

            Spacer(Modifier.height(80.dp))

            // Loading dots
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Black, CircleShape)
                    )
                }
            }
        }
    }
}














