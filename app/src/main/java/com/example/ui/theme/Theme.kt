package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SalonDarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF422006),
    onPrimaryContainer = AmberLight,

    secondary = AccentBronze,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF431407),
    onSecondaryContainer = Color(0xFFFFEDD5),

    tertiary = AccentCyan,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF083344),
    onTertiaryContainer = Color(0xFFCFFAFE),

    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF1E293B)
)

private val SalonLightColorScheme = lightColorScheme(
    primary = AmberDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),

    secondary = AccentBronze,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),

    tertiary = AccentCyan,
    onTertiary = Color.White,

    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun SalonAppTheme(
    darkTheme: Boolean = true, // Default to sleek dark salon aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SalonDarkColorScheme else SalonLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
