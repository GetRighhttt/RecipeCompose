import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.recipe_app_compose.ui.theme.AppTypography
import com.example.recipe_app_compose.ui.theme.backgroundDark
import com.example.recipe_app_compose.ui.theme.backgroundDarkHighContrast
import com.example.recipe_app_compose.ui.theme.backgroundDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.backgroundLight
import com.example.recipe_app_compose.ui.theme.backgroundLightHighContrast
import com.example.recipe_app_compose.ui.theme.backgroundLightMediumContrast
import com.example.recipe_app_compose.ui.theme.errorContainerDark
import com.example.recipe_app_compose.ui.theme.errorContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.errorContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.errorContainerLight
import com.example.recipe_app_compose.ui.theme.errorContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.errorContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.errorDark
import com.example.recipe_app_compose.ui.theme.errorDarkHighContrast
import com.example.recipe_app_compose.ui.theme.errorDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.errorLight
import com.example.recipe_app_compose.ui.theme.errorLightHighContrast
import com.example.recipe_app_compose.ui.theme.errorLightMediumContrast
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceDark
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceDarkHighContrast
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceLight
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceLightHighContrast
import com.example.recipe_app_compose.ui.theme.inverseOnSurfaceLightMediumContrast
import com.example.recipe_app_compose.ui.theme.inversePrimaryDark
import com.example.recipe_app_compose.ui.theme.inversePrimaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.inversePrimaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.inversePrimaryLight
import com.example.recipe_app_compose.ui.theme.inversePrimaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.inversePrimaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.inverseSurfaceDark
import com.example.recipe_app_compose.ui.theme.inverseSurfaceDarkHighContrast
import com.example.recipe_app_compose.ui.theme.inverseSurfaceDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.inverseSurfaceLight
import com.example.recipe_app_compose.ui.theme.inverseSurfaceLightHighContrast
import com.example.recipe_app_compose.ui.theme.inverseSurfaceLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onBackgroundDark
import com.example.recipe_app_compose.ui.theme.onBackgroundDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onBackgroundDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onBackgroundLight
import com.example.recipe_app_compose.ui.theme.onBackgroundLightHighContrast
import com.example.recipe_app_compose.ui.theme.onBackgroundLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onErrorContainerDark
import com.example.recipe_app_compose.ui.theme.onErrorContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onErrorContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onErrorContainerLight
import com.example.recipe_app_compose.ui.theme.onErrorContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.onErrorContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onErrorDark
import com.example.recipe_app_compose.ui.theme.onErrorDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onErrorDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onErrorLight
import com.example.recipe_app_compose.ui.theme.onErrorLightHighContrast
import com.example.recipe_app_compose.ui.theme.onErrorLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerDark
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerLight
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryDark
import com.example.recipe_app_compose.ui.theme.onPrimaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryLight
import com.example.recipe_app_compose.ui.theme.onPrimaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.onPrimaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerDark
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerLight
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryDark
import com.example.recipe_app_compose.ui.theme.onSecondaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryLight
import com.example.recipe_app_compose.ui.theme.onSecondaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.onSecondaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceDark
import com.example.recipe_app_compose.ui.theme.onSurfaceDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceLight
import com.example.recipe_app_compose.ui.theme.onSurfaceLightHighContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantDark
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantLight
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantLightHighContrast
import com.example.recipe_app_compose.ui.theme.onSurfaceVariantLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerDark
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerLight
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryDark
import com.example.recipe_app_compose.ui.theme.onTertiaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryLight
import com.example.recipe_app_compose.ui.theme.onTertiaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.onTertiaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.outlineDark
import com.example.recipe_app_compose.ui.theme.outlineDarkHighContrast
import com.example.recipe_app_compose.ui.theme.outlineDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.outlineLight
import com.example.recipe_app_compose.ui.theme.outlineLightHighContrast
import com.example.recipe_app_compose.ui.theme.outlineLightMediumContrast
import com.example.recipe_app_compose.ui.theme.outlineVariantDark
import com.example.recipe_app_compose.ui.theme.outlineVariantDarkHighContrast
import com.example.recipe_app_compose.ui.theme.outlineVariantDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.outlineVariantLight
import com.example.recipe_app_compose.ui.theme.outlineVariantLightHighContrast
import com.example.recipe_app_compose.ui.theme.outlineVariantLightMediumContrast
import com.example.recipe_app_compose.ui.theme.primaryContainerDark
import com.example.recipe_app_compose.ui.theme.primaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.primaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.primaryContainerLight
import com.example.recipe_app_compose.ui.theme.primaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.primaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.primaryDark
import com.example.recipe_app_compose.ui.theme.primaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.primaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.primaryLight
import com.example.recipe_app_compose.ui.theme.primaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.primaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.scrimDark
import com.example.recipe_app_compose.ui.theme.scrimDarkHighContrast
import com.example.recipe_app_compose.ui.theme.scrimDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.scrimLight
import com.example.recipe_app_compose.ui.theme.scrimLightHighContrast
import com.example.recipe_app_compose.ui.theme.scrimLightMediumContrast
import com.example.recipe_app_compose.ui.theme.secondaryContainerDark
import com.example.recipe_app_compose.ui.theme.secondaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.secondaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.secondaryContainerLight
import com.example.recipe_app_compose.ui.theme.secondaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.secondaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.secondaryDark
import com.example.recipe_app_compose.ui.theme.secondaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.secondaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.secondaryLight
import com.example.recipe_app_compose.ui.theme.secondaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.secondaryLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceBrightDark
import com.example.recipe_app_compose.ui.theme.surfaceBrightDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceBrightDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceBrightLight
import com.example.recipe_app_compose.ui.theme.surfaceBrightLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceBrightLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerDark
import com.example.recipe_app_compose.ui.theme.surfaceContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighDark
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighLight
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestDark
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestLight
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerHighestLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLight
import com.example.recipe_app_compose.ui.theme.surfaceContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowDark
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowLight
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestDark
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestLight
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceContainerLowestLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceDark
import com.example.recipe_app_compose.ui.theme.surfaceDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceDimDark
import com.example.recipe_app_compose.ui.theme.surfaceDimDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceDimDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceDimLight
import com.example.recipe_app_compose.ui.theme.surfaceDimLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceDimLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceLight
import com.example.recipe_app_compose.ui.theme.surfaceLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceLightMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceVariantDark
import com.example.recipe_app_compose.ui.theme.surfaceVariantDarkHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceVariantDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.surfaceVariantLight
import com.example.recipe_app_compose.ui.theme.surfaceVariantLightHighContrast
import com.example.recipe_app_compose.ui.theme.surfaceVariantLightMediumContrast
import com.example.recipe_app_compose.ui.theme.tertiaryContainerDark
import com.example.recipe_app_compose.ui.theme.tertiaryContainerDarkHighContrast
import com.example.recipe_app_compose.ui.theme.tertiaryContainerDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.tertiaryContainerLight
import com.example.recipe_app_compose.ui.theme.tertiaryContainerLightHighContrast
import com.example.recipe_app_compose.ui.theme.tertiaryContainerLightMediumContrast
import com.example.recipe_app_compose.ui.theme.tertiaryDark
import com.example.recipe_app_compose.ui.theme.tertiaryDarkHighContrast
import com.example.recipe_app_compose.ui.theme.tertiaryDarkMediumContrast
import com.example.recipe_app_compose.ui.theme.tertiaryLight
import com.example.recipe_app_compose.ui.theme.tertiaryLightHighContrast
import com.example.recipe_app_compose.ui.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
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

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

