package com.openlauncher.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.openlauncher.app.data.AppFont

val LocalDayMode = staticCompositionLocalOf { false }

fun Color.lighter(factor: Float) = copy(
    red = red + factor,
    green = green + factor,
    blue = blue + factor
)

fun Color.darker(factor: Float) = copy(
    red = red - factor,
    green = green - factor,
    blue = blue - factor
)

@Composable
fun OpenLauncherTheme(
    controls: Color     = colorAmber,
    tiles: Color        = Color.LightGray,
    background1: Color  = Color.White,
    textColor: Color    = Color.Black,
    fontBold: Boolean   = false,
    textScale: Float    = 1.0f,
    appFont: AppFont    = AppFont.JETBRAINS_MONO,
    isDayMode: Boolean  = false,
    content: @Composable () -> Unit
) {
    // Contrast-aware: the accent is user-chosen and can be any brightness,
    // so a fixed onPrimary (white) goes invisible on light accents
    val onAccent = if (controls.luminance() > 0.5f) Color.Black else Color.White
    val colorScheme = if (isDayMode) lightColorScheme(
        primary          = textColor,
        onPrimary        = textColor.lighter( 0.2f ),
        secondary        = controls,
        onSecondary      = controls.darker( 0.2f ),
        background       = background1,
        surface          = tiles,
        onSurface        = tiles.darker(0.2f),
        onBackground     = textColor,
        surfaceVariant   = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFF666666),
        outline          = Color(0xFFCCCCCC)
    ) else darkColorScheme(
        primary          = textColor,
        onPrimary        = textColor.darker( 0.2f ),
        secondary        = controls,
        onSecondary      = controls.lighter( 0.2f ),
        background       = background1,
        surface          = tiles,
        onSurface        = tiles.lighter(0.2f),
        onBackground     = textColor,
        surfaceVariant   = DimSurface,
        onSurfaceVariant = TextMuted,
        outline          = DividerGray
    )
    CompositionLocalProvider(LocalDayMode provides isDayMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = launcherTypography(fontBold, textScale, appFont.toFontFamily()),
            content     = content
        )
    }
}
