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

// Light color scheme using your orange theme
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OrangeOnPrimary,
    primaryContainer = OrangePrimaryContainer,
    onPrimaryContainer = OrangeOnPrimaryContainer,
    secondary = OrangeSecondary,
    onSecondary = OrangeOnSecondary,
    secondaryContainer = OrangeSecondaryContainer,
    onSecondaryContainer = OrangeOnSecondaryContainer,
    tertiary = OrangeTertiary,
    onTertiary = OrangeOnTertiary,
    background = BackgroundLight,
    onBackground = Color(0xFF201A19),
    surface = SurfaceLight,
    onSurface = Color(0xFF201A19),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF534341),
    outline = Color(0xFF857370),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// Dark color scheme using your orange theme
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = OrangeOnPrimary,
    primaryContainer = Color(0xFF7D2C12),
    onPrimaryContainer = OrangePrimaryContainer,
    secondary = Color(0xFFFFB4A4),
    onSecondary = Color(0xFF5E2314),
    secondaryContainer = Color(0xFF7D2C12),
    onSecondaryContainer = Color(0xFFFFDAD3),
    tertiary = Color(0xFFDBC3A4),
    onTertiary = Color(0xFF3E2E16),
    background = BackgroundDark,
    onBackground = Color(0xFFEDE0DD),
    surface = SurfaceDark,
    onSurface = Color(0xFFEDE0DD),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFD8C2BE),
    outline = Color(0xFFA08C89)
)

@Composable
fun BunnixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
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