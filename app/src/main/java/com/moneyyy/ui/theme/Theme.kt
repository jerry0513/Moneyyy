package com.moneyyy.ui.theme

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
    primary = Yellow80,
    onPrimary = Neutral0,
    primaryContainer = Yellow30,
    onPrimaryContainer = Yellow95,
    secondary = Orange80,
    onSecondary = Neutral0,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    error = Red80,
    onError = Neutral0
)

private val LightColorScheme = lightColorScheme(
    primary = Yellow40,
    onPrimary = Neutral0,
    primaryContainer = Yellow90,
    onPrimaryContainer = Yellow20,
    secondary = Orange40,
    onSecondary = Neutral100,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange30,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    error = Red40,
    onError = Neutral100
)

@Composable
fun MoneyyyTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}