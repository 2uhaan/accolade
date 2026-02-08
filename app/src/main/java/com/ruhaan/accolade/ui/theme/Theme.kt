package com.ruhaan.accolade.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = SkyBlue,
        onPrimary = White,
        background = White,
        surface = White,
        onBackground = DarkText,
        onSurface = DarkText,
    )

@Composable
fun AccoladeTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = LightColorScheme, typography = Typography, content = content)
}
