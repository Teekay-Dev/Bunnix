package com.example.bunnix.frontend



import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bunnix.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class OnboardingActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPrefs = UserPreferences(this)

        setContent {
            OnboardingPager(onFinish = {
                runBlocking { userPrefs.setFirstLaunch(false) }
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> OnboardingPage(
                title = "Get Help Fast",
                description = "Find and book service providers nearby with just a few taps.",
                buttonText = "Next",
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
            )
            1 -> OnboardingPage(
                title = "Book a Service",
                description = "Easily schedule electricians, plumbers, and more.",
                buttonText = "Next",
                onClick = { scope.launch { pagerState.animateScrollToPage(2) } }
            )
            2 -> OnboardingPage(
                title = "Discover Vendors",
                description = "Shop for products from local sellers.",
                buttonText = "Get Started",
                onClick = onFinish
            )
        }
    }
}

@Composable
fun OnboardingPage(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(R.drawable.bunnix_2),
            contentDescription = null,
            modifier = Modifier
                .width(300.dp)
                .height(200.dp)
                .padding(bottom = 20.dp)
        )

        Text(
            text = "Bunnix",
            fontSize = 60.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(
                shadow = Shadow(
                    color = Color(0x80000000),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )

        Text(
            text = title,
            fontSize = 25.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = description,
            fontSize = 20.sp,
            color = Color(0xFFCCCCCC),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7900)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp),
            contentPadding = PaddingValues(20.dp)
        ) {
            Text(text = buttonText, color = Color.White)
        }
    }
}

// PREVIEWS
@Preview(showBackground = true)
@Composable
fun OnboardingPagePreview1() {
    OnboardingPage(
        title = "Get Help Fast",
        description = "Find and book service providers nearby with just a few taps.",
        buttonText = "Next",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingPagePreview2() {
    OnboardingPage(
        title = "Book a Service",
        description = "Easily schedule electricians, plumbers, and more.",
        buttonText = "Next",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingPagePreview3() {
    OnboardingPage(
        title = "Discover Vendors",
        description = "Shop for products from local sellers.",
        buttonText = "Get Started",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingPagerPreview() {
    OnboardingPager(onFinish = {})
}
