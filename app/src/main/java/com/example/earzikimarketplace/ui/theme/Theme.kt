package com.example.earzikimarketplace.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import com.example.earzikimarketplace.R

val JosefinFontFamily = FontFamily(
    Font(R.font.josefinsans_bold, FontWeight.Bold),
    Font(R.font.josefinsans_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.josefinsans_extralight, FontWeight.ExtraLight),
    Font(R.font.josefinsans_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.josefinsans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.josefinsans_light, FontWeight.Light),
    Font(R.font.josefinsans_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.josefinsans_medium, FontWeight.Medium),
    Font(R.font.josefinsans_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.josefinsans_regular, FontWeight.Normal),
    Font(R.font.josefinsans_semibold, FontWeight.SemiBold),
    Font(R.font.josefinsans_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.josefinsans_thin, FontWeight.Thin),
    Font(R.font.josefinsans_thinitalic, FontWeight.Thin, FontStyle.Italic)
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFD5A0F),
    surfaceTint = Color(0xFFFD7232),
    secondary = Color(0xFFCAA470),
    tertiary = Color(0xFFB8B9A0),
    background = Color(0xFFFFFFFF),
    error = Color(0xFFCC4828),
    surface = Color(0xFF4A8694)

)

@Composable
fun EArzikiMarketplaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicLightColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }


    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusBarColor = LightColorScheme.primary
            window.statusBarColor = statusBarColor.toArgb()

            // Determine the appropriate system icon color based on the status bar color
            val isLightStatusBar = isColorLight(statusBarColor)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                isLightStatusBar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Function to determine whether a color is light or dark
fun isColorLight(color: Color): Boolean {
    // Calculate the perceived brightness of the color
    val brightness = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)
    return brightness > 0.5 // You can adjust the threshold as needed
}