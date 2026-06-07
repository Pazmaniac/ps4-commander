package com.example.ui.theme

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
    primary = ElectricCyan,
    secondary = GoldHenGold,
    tertiary = GlowBlue,
    background = AbyssBlue,
    surface = NexusNavy,
    onPrimary = AbyssBlue,
    onSecondary = AbyssBlue,
    onBackground = OffWhite,
    onSurface = OffWhite,
    surfaceVariant = CardSlate
)

private val LightColorScheme = lightColorScheme(
    primary = GlowBlue,
    secondary = GoldHenGold,
    tertiary = ElectricCyan,
    background = OffWhite,
    surface = OffWhite,
    outline = CoolGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force stunning dark theme by default for PS4 aesthetics
    dynamicColor: Boolean = false, // Use our handcrafted luxury layout colors instead of dynamic slop
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
