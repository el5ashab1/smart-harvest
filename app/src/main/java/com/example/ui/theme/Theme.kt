package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HarvestDarkPrimary,
    onPrimary = Color(0xFF00391C),
    primaryContainer = HarvestPrimaryDark,
    onPrimaryContainer = Color(0xFF86FBB3),
    secondary = HarvestTertiary,
    onSecondary = Color(0xFF003823),
    secondaryContainer = Color(0xFF005235),
    onSecondaryContainer = Color(0xFF8CF8C3),
    tertiary = Color(0xFF7DD3FC),
    background = HarvestDarkBackground,
    surface = HarvestDarkSurface,
    surfaceVariant = HarvestDarkSurfaceVariant,
    onBackground = HarvestDarkOnSurface,
    onSurface = HarvestDarkOnSurface,
    onSurfaceVariant = Color(0xFFB5CCC0)
)

private val LightColorScheme = lightColorScheme(
    primary = HarvestPrimary,
    onPrimary = Color.White,
    primaryContainer = HarvestSecondaryContainer,
    onPrimaryContainer = HarvestOnSecondaryContainer,
    secondary = HarvestSecondary,
    onSecondary = Color.White,
    secondaryContainer = HarvestSecondaryContainer,
    onSecondaryContainer = HarvestOnSecondaryContainer,
    tertiary = HarvestAccentWater,
    onTertiary = Color.White,
    background = HarvestBackground,
    surface = HarvestSurface,
    surfaceVariant = HarvestSurfaceVariant,
    onBackground = HarvestOnSurface,
    onSurface = HarvestOnSurface,
    onSurfaceVariant = HarvestOnSurfaceVariant
)

@Composable
fun SmartHarvestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
