package com.tebmed.inventory.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

// Use the platform default font until bundled font assets are added.
val VazirmatnFamily = FontFamily.Default

private val LightColorScheme = lightColorScheme(
    primary = NavyDark,
    onPrimary = SoftWhite,
    primaryContainer = NavyMedium,
    onPrimaryContainer = SoftWhite,
    secondary = ChampagneGold,
    onSecondary = CharcoalBlack,
    secondaryContainer = GoldMuted,
    onSecondaryContainer = CharcoalBlack,
    tertiary = NavyLight,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = ColorSchemeDefaults,
    error = DangerCrimson,
    outline = ChampagneGold.copy(alpha = 0.3f),
    scrim = CharcoalBlack.copy(alpha = 0.5f)
)

// Fix: define properly
private val DarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = CharcoalBlack,
    primaryContainer = GoldDark,
    onPrimaryContainer = SoftWhite,
    secondary = GoldLight,
    onSecondary = CharcoalBlack,
    secondaryContainer = NavyMedium,
    onSecondaryContainer = SoftWhite,
    tertiary = NavyLight,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkCard,
    error = DangerCrimson,
    outline = ChampagneGold.copy(alpha = 0.2f)
)

// Helper
private val ColorSchemeDefaults = LightCard

@Composable
fun TebMedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val typography = Typography(
        displayLarge = Typography().displayLarge.copy(fontFamily = VazirmatnFamily),
        displayMedium = Typography().displayMedium.copy(fontFamily = VazirmatnFamily),
        displaySmall = Typography().displaySmall.copy(fontFamily = VazirmatnFamily),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = VazirmatnFamily),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = VazirmatnFamily),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = VazirmatnFamily),
        titleLarge = Typography().titleLarge.copy(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold),
        titleMedium = Typography().titleMedium.copy(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Medium),
        titleSmall = Typography().titleSmall.copy(fontFamily = VazirmatnFamily),
        bodyLarge = Typography().bodyLarge.copy(fontFamily = VazirmatnFamily),
        bodyMedium = Typography().bodyMedium.copy(fontFamily = VazirmatnFamily),
        bodySmall = Typography().bodySmall.copy(fontFamily = VazirmatnFamily),
        labelLarge = Typography().labelLarge.copy(fontFamily = VazirmatnFamily),
        labelMedium = Typography().labelMedium.copy(fontFamily = VazirmatnFamily),
        labelSmall = Typography().labelSmall.copy(fontFamily = VazirmatnFamily)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = LuxuryShapes,
        content = content
    )
}

val LuxuryShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8f),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12f),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16f),
    large = androidx.compose.foundation.shape.RoundedCornerShape(20f),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28f)
)
