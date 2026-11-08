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

private val DarkColorScheme = darkColorScheme(
  primary = JobixBlueLight,
  onPrimary = Color.White,
  primaryContainer = JobixNavyElevated,
  onPrimaryContainer = Color(0xFFBFDBFE),
  secondary = JobixPurple,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF3B1F69),
  onSecondaryContainer = Color(0xFFE9D5FF),
  tertiary = JobixCyan,
  onTertiary = Color.Black,
  background = JobixNavyDark,
  onBackground = Color(0xFFF1F5F9),
  surface = JobixNavyCard,
  onSurface = Color(0xFFF1F5F9),
  surfaceVariant = JobixNavyElevated,
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
  primary = JobixBlue,
  onPrimary = Color.White,
  primaryContainer = PrimaryContainerBlue,
  onPrimaryContainer = OnPrimaryContainerBlue,
  secondary = JobixViolet,
  onSecondary = Color.White,
  secondaryContainer = SecondaryContainerViolet,
  onSecondaryContainer = OnSecondaryContainerViolet,
  tertiary = JobixCyan,
  onTertiary = Color.White,
  background = JobixBackgroundLight,
  onBackground = JobixTextDark,
  surface = JobixSurfaceLight,
  onSurface = JobixTextDark,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = JobixTextMuted,
  outline = JobixBorderLight
)

@Composable
fun JobixTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve brand palette by default
  content: @Composable () -> Unit,
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
