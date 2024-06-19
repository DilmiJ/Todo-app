package com.example.daytask.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val mainColorScheme = darkColorScheme(
    primary = MainColor,
    secondary = Secondary,
    background = Background,
    tertiary = Tertiary,
    onSurface = White
)

@Composable
fun DayTaskTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = mainColorScheme,
        content = content
    )
}