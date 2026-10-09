package com.example.cashpeer.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.cashpeer.ui.theme.BackgroundLight
import com.example.cashpeer.ui.theme.Error
import com.example.cashpeer.ui.theme.GreenContainer
import com.example.cashpeer.ui.theme.GreenDark
import com.example.cashpeer.ui.theme.GreenLight
import com.example.cashpeer.ui.theme.GreenPrimary
import com.example.cashpeer.ui.theme.Outline
import com.example.cashpeer.ui.theme.OutlineLight
import com.example.cashpeer.ui.theme.SurfaceVariantLight
import com.example.cashpeer.ui.theme.SurfaceWhite
import com.example.cashpeer.ui.theme.TextOnPrimary
import com.example.cashpeer.ui.theme.TextPrimary
import com.example.cashpeer.ui.theme.TextSecondary

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

@Composable
fun CashPeerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CashPeerLightColorScheme,
        typography = CashPeerTypography,
        content = content
    )
}