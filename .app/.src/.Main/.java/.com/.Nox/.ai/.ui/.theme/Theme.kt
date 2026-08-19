package com.nox.ai.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DeepBurgundy,
    secondary = WarmTerracotta,
    tertiary = VibrantPinkContainer,
    background = DarkCanvas,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onPrimary = WarmCanvas,
    onSecondary = SoftPinkContainer,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBurgundy,
    secondary = WarmTerracotta,
    tertiary = SoftPinkContainer,
    background = WarmCanvas,
    surface = LightSurface,
    surfaceVariant = SoftPinkContainer,
    onPrimary = WarmCanvas,
    onSecondary = DeepBurgundy,
    onBackground = NeutralTextDark,
    onSurface = NeutralTextDark,
    onSurfaceVariant = MutedTextWarm,
    outline = LightBorder
)

@Composable
fun NoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false for consistent AI Studio branded theme
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
