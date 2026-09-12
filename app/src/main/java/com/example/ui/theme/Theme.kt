package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GharGoBlueLight,
    onPrimary = Color.White,
    primaryContainer = GharGoDarkCard,
    onPrimaryContainer = Color.White,
    secondary = GharGoOrange,
    onSecondary = Color.White,
    background = Color(0xFF0B0F17),
    surface = Color(0xFF111827),
    surfaceVariant = Color(0xFF1F2937),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GharGoBlue,
    onPrimary = Color.White,
    primaryContainer = GharGoBlueSoft,
    onPrimaryContainer = GharGoBlue,
    secondary = GharGoOrange,
    onSecondary = Color.White,
    secondaryContainer = GharGoOrangeSoft,
    onSecondaryContainer = GharGoOrange,
    tertiary = GharGoGreen,
    onTertiary = Color.White,
    background = SurfaceLight,
    surface = SurfaceCard,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextPrimary,
    onSurface = TextPrimary
  )

@Composable
fun GharGoTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun ShramConnectTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  GharGoTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  ShramConnectTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

