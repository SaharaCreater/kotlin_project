package com.app.dopp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary              = Violet800,
    onPrimary            = Color.White,
    primaryContainer     = Violet100,
    onPrimaryContainer   = Violet900,
    secondary            = Sky500,
    onSecondary          = Color.White,
    secondaryContainer   = Sky100,
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary             = Amber500,
    onTertiary           = Color.White,
    tertiaryContainer    = Amber100,
    onTertiaryContainer  = Color(0xFF78350F),
    error                = Rose500,
    onError              = Color.White,
    errorContainer       = Rose100,
    onErrorContainer     = Color(0xFF881337),
    background           = Violet50,
    onBackground         = Color(0xFF1C1B1F),
    surface              = Color.White,
    onSurface            = Color(0xFF1C1B1F),
    surfaceVariant       = Violet100,
    onSurfaceVariant     = Color(0xFF625B71),
    outline              = Violet200,
    outlineVariant       = Violet100,
)

@Composable
fun DoPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
