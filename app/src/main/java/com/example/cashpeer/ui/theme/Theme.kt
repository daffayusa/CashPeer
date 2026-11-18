package com.example.cashpeer.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.cashpeer.ui.theme.*

private val CashPeerLightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,

    secondary = GreenDark,
    onSecondary = TextOnPrimary,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenDark,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,

    outline = Outline,
    outlineVariant = OutlineLight,

    error = Error,
    onError = Color.White
)

private val CashPeerDarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color(0xFF00381B),
    primaryContainer = GreenDarkContainer,
    onPrimaryContainer = Color(0xFF90F9B4),

    secondary = GreenPrimaryDark,
    onSecondary = Color(0xFF00381B),
    secondaryContainer = GreenDarkContainer,
    onSecondaryContainer = Color(0xFF9DF5B8),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline = OutlineDark,
    outlineVariant = OutlineLightDark,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun CashPeerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CashPeerDarkColorScheme else CashPeerLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CashPeerTypography,
        content = content
    )
}
