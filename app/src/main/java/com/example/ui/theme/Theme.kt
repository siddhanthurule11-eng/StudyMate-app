package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SophisticatedDarkColorScheme = darkColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = BlueContainer,
  onPrimaryContainer = Color(0xFFE0E7FF),
  secondary = PurpleSecondary,
  onSecondary = Color.White,
  secondaryContainer = PurpleContainer,
  onSecondaryContainer = Color(0xFFEDE9FE),
  tertiary = PinkAccent,
  onTertiary = Color.White,
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = TextMutedDark
)

private val ModernLightColorScheme = lightColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = SurfaceVariantLight,
  onPrimaryContainer = BlueDark,
  secondary = PurpleSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFF3E8FF),
  onSecondaryContainer = PurpleDark,
  tertiary = PinkAccent,
  onTertiary = Color.White,
  background = BackgroundLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = TextMutedLight
)

@Composable
fun StudyMateTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> SophisticatedDarkColorScheme
    else -> ModernLightColorScheme
  }
  
  val view = LocalView.current

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

