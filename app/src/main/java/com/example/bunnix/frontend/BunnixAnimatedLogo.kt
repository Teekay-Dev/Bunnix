package com.example.bunnix.frontend

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.R
import kotlinx.coroutines.delay


// Make sure you have this font in res/font/
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

val Baskerville = FontFamily(Font(R.font.baskerville_old_face, FontWeight.Normal))


@Composable
fun BunnixAnimatedLogo() {
    val letters = listOf("B", "u", "n", "n", "ı", "x")
    val letterOffsets = letters.map { remember { Animatable(-300f) } }

    // Icon animation values
    val iconOffsetX = remember { Animatable(-200f) }
    val iconOffsetY = remember { Animatable(-150f) }

    LaunchedEffect(Unit) {

        // 1️⃣ Animate letters
        letters.forEachIndexed { index, letter ->
            letterOffsets[index].animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        // 2️⃣ Icon bounces in from the left
        iconOffsetX.animateTo(
            targetValue = 140f, // overshoot past the "i"
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // 3️⃣ Icon drops onto the "i" dot
        iconOffsetY.animateTo(
            targetValue = 80f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            letters.forEachIndexed { index, letter ->
                Text(
                    text = letter,
                    fontFamily = Baskerville,
                    fontSize = 64.sp,
                    color = Color.White,
                    modifier = Modifier.offset(
                        x = letterOffsets[index].value.dp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Icon positioned RELATIVE to the row
        Image(
            painter = painterResource(R.drawable.bunnix_2),
            contentDescription = "dot icon",
            modifier = Modifier
                .size(70.dp)
                .offset(
                    x = 58.dp,   // relative to logo center
                    y = (-38).dp // above the "i"
                )
        )
    }

}





@Preview(showBackground = true, widthDp = 400, heightDp = 200)
@Composable
fun BunnixAnimatedLogoPreview() {
    BunnixAnimatedLogo()
}
