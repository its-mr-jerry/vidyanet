package com.kastack.vidyanet.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBrand,
    onPrimary = Color(0xFF00288E), // Deep contrast for lightened blue
    primaryContainer = Color(0xFF003DAB),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFC0C7D0),
    onSecondary = Color(0xFF2A3139),
    background = DarkSurface,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkOutline,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryBrand,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = LightPrimaryBrand,
    secondary = Color(0xFF585F67),
    onSecondary = Color(0xFFFFFFFF),
    background = LightSurface,
    onBackground = Color(0xFF1A1B22),
    surface = LightSurface,
    onSurface = Color(0xFF1A1B22),
    surfaceVariant = LightSurfaceContainer,
    onSurfaceVariant = Color(0xFF444653),
    outline = LightOutline,
    error = AcademicError,
    onError = Color(0xFFFFFFFF)
)

@Composable
fun VidyaNetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
