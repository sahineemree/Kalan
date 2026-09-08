package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDarkTheme = compositionLocalOf { false }

private val DarkColorScheme =
  darkColorScheme(
    primary = MintAccent,
    onPrimary = ForestGreenDark,
    primaryContainer = Color(0xFF163226),
    onPrimaryContainer = MintSoft,
    secondary = EmeraldGlow,
    onSecondary = Color(0xFF062319),
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1D2822)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = MintLight,
    onPrimaryContainer = ForestGreenDark,
    secondary = ForestGreenPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F6EF),
    onSecondaryContainer = ForestGreenDark,
    background = WarmLightBackground,
    surface = SurfaceWhite,
    surfaceVariant = SurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = OutlineBorder,
    outlineVariant = Color(0xFFEDF3EE)
  )

object KalanTheme {
    val isDark: Boolean
        @Composable
        get() = LocalDarkTheme.current

    val background: Color
        @Composable
        get() = MaterialTheme.colorScheme.background

    val cardBackground: Color
        @Composable
        get() = MaterialTheme.colorScheme.surface

    val surfaceVariant: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant

    val outline: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline

    val textPrimary: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface

    val textSecondary: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurfaceVariant

    val textMuted: Color
        @Composable
        get() = if (LocalDarkTheme.current) DarkTextMuted else TextMuted
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
