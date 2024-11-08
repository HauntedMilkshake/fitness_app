package bg.zahov.app.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.compose.surfaceContainerHighestDarkHighContrast

private val lightScheme = lightColorScheme(
    primary = com.example.compose.primaryLight,
    onPrimary = com.example.compose.onPrimaryLight,
    primaryContainer = com.example.compose.primaryContainerLight,
    onPrimaryContainer = com.example.compose.onPrimaryContainerLight,
    secondary = com.example.compose.secondaryLight,
    onSecondary = com.example.compose.onSecondaryLight,
    secondaryContainer = com.example.compose.secondaryContainerLight,
    onSecondaryContainer = com.example.compose.onSecondaryContainerLight,
    tertiary = com.example.compose.tertiaryLight,
    onTertiary = com.example.compose.onTertiaryLight,
    tertiaryContainer = com.example.compose.tertiaryContainerLight,
    onTertiaryContainer = com.example.compose.onTertiaryContainerLight,
    error = com.example.compose.errorLight,
    onError = com.example.compose.onErrorLight,
    errorContainer = com.example.compose.errorContainerLight,
    onErrorContainer = com.example.compose.onErrorContainerLight,
    background = com.example.compose.backgroundLight,
    onBackground = com.example.compose.onBackgroundLight,
    surface = com.example.compose.surfaceLight,
    onSurface = com.example.compose.onSurfaceLight,
    surfaceVariant = com.example.compose.surfaceVariantLight,
    onSurfaceVariant = com.example.compose.onSurfaceVariantLight,
    outline = com.example.compose.outlineLight,
    outlineVariant = com.example.compose.outlineVariantLight,
    scrim = com.example.compose.scrimLight,
    inverseSurface = com.example.compose.inverseSurfaceLight,
    inverseOnSurface = com.example.compose.inverseOnSurfaceLight,
    inversePrimary = com.example.compose.inversePrimaryLight,
    surfaceDim = com.example.compose.surfaceDimLight,
    surfaceBright = com.example.compose.surfaceBrightLight,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestLight,
    surfaceContainerLow = com.example.compose.surfaceContainerLowLight,
    surfaceContainer = com.example.compose.surfaceContainerLight,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighLight,
    surfaceContainerHighest = com.example.compose.surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = com.example.compose.primaryDark,
    onPrimary = com.example.compose.onPrimaryDark,
    primaryContainer = com.example.compose.primaryContainerDark,
    onPrimaryContainer = com.example.compose.onPrimaryContainerDark,
    secondary = com.example.compose.secondaryDark,
    onSecondary = com.example.compose.onSecondaryDark,
    secondaryContainer = com.example.compose.secondaryContainerDark,
    onSecondaryContainer = com.example.compose.onSecondaryContainerDark,
    tertiary = com.example.compose.tertiaryDark,
    onTertiary = com.example.compose.onTertiaryDark,
    tertiaryContainer = com.example.compose.tertiaryContainerDark,
    onTertiaryContainer = com.example.compose.onTertiaryContainerDark,
    error = com.example.compose.errorDark,
    onError = com.example.compose.onErrorDark,
    errorContainer = com.example.compose.errorContainerDark,
    onErrorContainer = com.example.compose.onErrorContainerDark,
    background = com.example.compose.backgroundDark,
    onBackground = com.example.compose.onBackgroundDark,
    surface = com.example.compose.surfaceDark,
    onSurface = com.example.compose.onSurfaceDark,
    surfaceVariant = com.example.compose.surfaceVariantDark,
    onSurfaceVariant = com.example.compose.onSurfaceVariantDark,
    outline = com.example.compose.outlineDark,
    outlineVariant = com.example.compose.outlineVariantDark,
    scrim = com.example.compose.scrimDark,
    inverseSurface = com.example.compose.inverseSurfaceDark,
    inverseOnSurface = com.example.compose.inverseOnSurfaceDark,
    inversePrimary = com.example.compose.inversePrimaryDark,
    surfaceDim = com.example.compose.surfaceDimDark,
    surfaceBright = com.example.compose.surfaceBrightDark,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestDark,
    surfaceContainerLow = com.example.compose.surfaceContainerLowDark,
    surfaceContainer = com.example.compose.surfaceContainerDark,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighDark,
    surfaceContainerHighest = com.example.compose.surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = com.example.compose.primaryLightMediumContrast,
    onPrimary = com.example.compose.onPrimaryLightMediumContrast,
    primaryContainer = com.example.compose.primaryContainerLightMediumContrast,
    onPrimaryContainer = com.example.compose.onPrimaryContainerLightMediumContrast,
    secondary = com.example.compose.secondaryLightMediumContrast,
    onSecondary = com.example.compose.onSecondaryLightMediumContrast,
    secondaryContainer = com.example.compose.secondaryContainerLightMediumContrast,
    onSecondaryContainer = com.example.compose.onSecondaryContainerLightMediumContrast,
    tertiary = com.example.compose.tertiaryLightMediumContrast,
    onTertiary = com.example.compose.onTertiaryLightMediumContrast,
    tertiaryContainer = com.example.compose.tertiaryContainerLightMediumContrast,
    onTertiaryContainer = com.example.compose.onTertiaryContainerLightMediumContrast,
    error = com.example.compose.errorLightMediumContrast,
    onError = com.example.compose.onErrorLightMediumContrast,
    errorContainer = com.example.compose.errorContainerLightMediumContrast,
    onErrorContainer = com.example.compose.onErrorContainerLightMediumContrast,
    background = com.example.compose.backgroundLightMediumContrast,
    onBackground = com.example.compose.onBackgroundLightMediumContrast,
    surface = com.example.compose.surfaceLightMediumContrast,
    onSurface = com.example.compose.onSurfaceLightMediumContrast,
    surfaceVariant = com.example.compose.surfaceVariantLightMediumContrast,
    onSurfaceVariant = com.example.compose.onSurfaceVariantLightMediumContrast,
    outline = com.example.compose.outlineLightMediumContrast,
    outlineVariant = com.example.compose.outlineVariantLightMediumContrast,
    scrim = com.example.compose.scrimLightMediumContrast,
    inverseSurface = com.example.compose.inverseSurfaceLightMediumContrast,
    inverseOnSurface = com.example.compose.inverseOnSurfaceLightMediumContrast,
    inversePrimary = com.example.compose.inversePrimaryLightMediumContrast,
    surfaceDim = com.example.compose.surfaceDimLightMediumContrast,
    surfaceBright = com.example.compose.surfaceBrightLightMediumContrast,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = com.example.compose.surfaceContainerLowLightMediumContrast,
    surfaceContainer = com.example.compose.surfaceContainerLightMediumContrast,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = com.example.compose.surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = com.example.compose.primaryLightHighContrast,
    onPrimary = com.example.compose.onPrimaryLightHighContrast,
    primaryContainer = com.example.compose.primaryContainerLightHighContrast,
    onPrimaryContainer = com.example.compose.onPrimaryContainerLightHighContrast,
    secondary = com.example.compose.secondaryLightHighContrast,
    onSecondary = com.example.compose.onSecondaryLightHighContrast,
    secondaryContainer = com.example.compose.secondaryContainerLightHighContrast,
    onSecondaryContainer = com.example.compose.onSecondaryContainerLightHighContrast,
    tertiary = com.example.compose.tertiaryLightHighContrast,
    onTertiary = com.example.compose.onTertiaryLightHighContrast,
    tertiaryContainer = com.example.compose.tertiaryContainerLightHighContrast,
    onTertiaryContainer = com.example.compose.onTertiaryContainerLightHighContrast,
    error = com.example.compose.errorLightHighContrast,
    onError = com.example.compose.onErrorLightHighContrast,
    errorContainer = com.example.compose.errorContainerLightHighContrast,
    onErrorContainer = com.example.compose.onErrorContainerLightHighContrast,
    background = com.example.compose.backgroundLightHighContrast,
    onBackground = com.example.compose.onBackgroundLightHighContrast,
    surface = com.example.compose.surfaceLightHighContrast,
    onSurface = com.example.compose.onSurfaceLightHighContrast,
    surfaceVariant = com.example.compose.surfaceVariantLightHighContrast,
    onSurfaceVariant = com.example.compose.onSurfaceVariantLightHighContrast,
    outline = com.example.compose.outlineLightHighContrast,
    outlineVariant = com.example.compose.outlineVariantLightHighContrast,
    scrim = com.example.compose.scrimLightHighContrast,
    inverseSurface = com.example.compose.inverseSurfaceLightHighContrast,
    inverseOnSurface = com.example.compose.inverseOnSurfaceLightHighContrast,
    inversePrimary = com.example.compose.inversePrimaryLightHighContrast,
    surfaceDim = com.example.compose.surfaceDimLightHighContrast,
    surfaceBright = com.example.compose.surfaceBrightLightHighContrast,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = com.example.compose.surfaceContainerLowLightHighContrast,
    surfaceContainer = com.example.compose.surfaceContainerLightHighContrast,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = com.example.compose.surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = com.example.compose.primaryDarkMediumContrast,
    onPrimary = com.example.compose.onPrimaryDarkMediumContrast,
    primaryContainer = com.example.compose.primaryContainerDarkMediumContrast,
    onPrimaryContainer = com.example.compose.onPrimaryContainerDarkMediumContrast,
    secondary = com.example.compose.secondaryDarkMediumContrast,
    onSecondary = com.example.compose.onSecondaryDarkMediumContrast,
    secondaryContainer = com.example.compose.secondaryContainerDarkMediumContrast,
    onSecondaryContainer = com.example.compose.onSecondaryContainerDarkMediumContrast,
    tertiary = com.example.compose.tertiaryDarkMediumContrast,
    onTertiary = com.example.compose.onTertiaryDarkMediumContrast,
    tertiaryContainer = com.example.compose.tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = com.example.compose.onTertiaryContainerDarkMediumContrast,
    error = com.example.compose.errorDarkMediumContrast,
    onError = com.example.compose.onErrorDarkMediumContrast,
    errorContainer = com.example.compose.errorContainerDarkMediumContrast,
    onErrorContainer = com.example.compose.onErrorContainerDarkMediumContrast,
    background = com.example.compose.backgroundDarkMediumContrast,
    onBackground = com.example.compose.onBackgroundDarkMediumContrast,
    surface = com.example.compose.surfaceDarkMediumContrast,
    onSurface = com.example.compose.onSurfaceDarkMediumContrast,
    surfaceVariant = com.example.compose.surfaceVariantDarkMediumContrast,
    onSurfaceVariant = com.example.compose.onSurfaceVariantDarkMediumContrast,
    outline = com.example.compose.outlineDarkMediumContrast,
    outlineVariant = com.example.compose.outlineVariantDarkMediumContrast,
    scrim = com.example.compose.scrimDarkMediumContrast,
    inverseSurface = com.example.compose.inverseSurfaceDarkMediumContrast,
    inverseOnSurface = com.example.compose.inverseOnSurfaceDarkMediumContrast,
    inversePrimary = com.example.compose.inversePrimaryDarkMediumContrast,
    surfaceDim = com.example.compose.surfaceDimDarkMediumContrast,
    surfaceBright = com.example.compose.surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = com.example.compose.surfaceContainerLowDarkMediumContrast,
    surfaceContainer = com.example.compose.surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = com.example.compose.surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = com.example.compose.primaryDarkHighContrast,
    onPrimary = com.example.compose.onPrimaryDarkHighContrast,
    primaryContainer = com.example.compose.primaryContainerDarkHighContrast,
    onPrimaryContainer = com.example.compose.onPrimaryContainerDarkHighContrast,
    secondary = com.example.compose.secondaryDarkHighContrast,
    onSecondary = com.example.compose.onSecondaryDarkHighContrast,
    secondaryContainer = com.example.compose.secondaryContainerDarkHighContrast,
    onSecondaryContainer = com.example.compose.onSecondaryContainerDarkHighContrast,
    tertiary = com.example.compose.tertiaryDarkHighContrast,
    onTertiary = com.example.compose.onTertiaryDarkHighContrast,
    tertiaryContainer = com.example.compose.tertiaryContainerDarkHighContrast,
    onTertiaryContainer = com.example.compose.onTertiaryContainerDarkHighContrast,
    error = com.example.compose.errorDarkHighContrast,
    onError = com.example.compose.onErrorDarkHighContrast,
    errorContainer = com.example.compose.errorContainerDarkHighContrast,
    onErrorContainer = com.example.compose.onErrorContainerDarkHighContrast,
    background = com.example.compose.backgroundDarkHighContrast,
    onBackground = com.example.compose.onBackgroundDarkHighContrast,
    surface = com.example.compose.surfaceDarkHighContrast,
    onSurface = com.example.compose.onSurfaceDarkHighContrast,
    surfaceVariant = com.example.compose.surfaceVariantDarkHighContrast,
    onSurfaceVariant = com.example.compose.onSurfaceVariantDarkHighContrast,
    outline = com.example.compose.outlineDarkHighContrast,
    outlineVariant = com.example.compose.outlineVariantDarkHighContrast,
    scrim = com.example.compose.scrimDarkHighContrast,
    inverseSurface = com.example.compose.inverseSurfaceDarkHighContrast,
    inverseOnSurface = com.example.compose.inverseOnSurfaceDarkHighContrast,
    inversePrimary = com.example.compose.inversePrimaryDarkHighContrast,
    surfaceDim = com.example.compose.surfaceDimDarkHighContrast,
    surfaceBright = com.example.compose.surfaceBrightDarkHighContrast,
    surfaceContainerLowest = com.example.compose.surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = com.example.compose.surfaceContainerLowDarkHighContrast,
    surfaceContainer = com.example.compose.surfaceContainerDarkHighContrast,
    surfaceContainerHigh = com.example.compose.surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@SuppressLint("ObsoleteSdkInt")
@Composable
fun FitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

