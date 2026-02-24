package com.example.bunnix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Your custom colors
val OrangeStart = Color(0xFFFF8C00)
val OrangeEnd = Color(0xFFFF4500)
val OrangePrimaryModern = Color(0xFFFF6B35)
val LightGrayBg = Color(0xFFF8F9FE)
val SurfaceLight = Color(0xFFFAFAFA)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)

// FIXED: Proper dark color scheme with explicit text colors
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimaryModern,
    onPrimary = Color.White,
    secondary = PurpleGrey80,
    onSecondary = Color.White,
    tertiary = Pink80,
    onTertiary = Color.White,
    background = Color(0xFF1A1A2E),       // Dark background
    onBackground = Color.White,           // CRITICAL: White text on dark bg
    surface = Color(0xFF2D2D3A),          // Dark surface
    onSurface = Color.White,              // CRITICAL: White text on surface
    onSurfaceVariant = Color(0xFFB0B0B0), // Secondary text
    error = Color(0xFFFFB4A9),
    onError = Color.Black
)

// FIXED: Proper light color scheme with explicit text colors
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimaryModern,
    onPrimary = Color.White,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    tertiary = Pink40,
    onTertiary = Color.White,
    background = SurfaceLight,            // Light background
    onBackground = TextPrimary,           // CRITICAL: Dark text on light bg
    surface = Color.White,                // White surface
    onSurface = TextPrimary,              // CRITICAL: Dark text on surface
    onSurfaceVariant = TextSecondary,     // Secondary text
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun BunnixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // CHANGED: Disabled dynamic color (causes issues)
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // DISABLED: Dynamic color causes text visibility issues
        // dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        //     val context = LocalContext.current
        //     if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        // }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // FIXED: Ensure proper window insets for edge-to-edge
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}