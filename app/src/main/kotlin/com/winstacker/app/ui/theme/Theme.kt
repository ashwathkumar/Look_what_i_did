package com.winstacker.app.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.winstacker.app.data.repository.AppAccent

private val BaseLightColorScheme = lightColorScheme(
    surface = Surface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Color(0xFF49454F),
    background = Background,
    onBackground = Color(0xFF1C1B1F),
    outline = Color(0xFF79747E),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val BaseDarkColorScheme = darkColorScheme(
    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCAC4D0),
    background = BackgroundDark,
    onBackground = Color(0xFFE6E1E5),
    outline = Color(0xFF938F99),
    error = Color(0xFFCF6679),
    onError = Color(0xFF690020)
)

/** True when the platform can derive a palette from the wallpaper (Android 12+). */
val supportsDynamicColor: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

private fun ColorScheme.withAccent(tones: AccentTones): ColorScheme = copy(
    primary = tones.primary,
    onPrimary = tones.onPrimary,
    primaryContainer = tones.primaryContainer,
    onPrimaryContainer = tones.onPrimaryContainer,
    secondary = tones.secondary,
    onSecondary = tones.onSecondary,
    secondaryContainer = tones.secondaryContainer,
    onSecondaryContainer = tones.onSecondaryContainer,
    tertiary = tones.tertiary,
    onTertiary = tones.onTertiary,
    tertiaryContainer = tones.tertiaryContainer
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun dynamicScheme(darkTheme: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

@Composable
fun WinsTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: AppAccent = AppAccent.GREEN,
    content: @Composable () -> Unit
) {
    val fixedScheme = remember(darkTheme, accent) {
        val palette = paletteFor(accent)
        if (darkTheme) {
            BaseDarkColorScheme.withAccent(palette.dark)
        } else {
            BaseLightColorScheme.withAccent(palette.light)
        }
    }

    val colorScheme = if (accent == AppAccent.DYNAMIC && supportsDynamicColor) {
        dynamicScheme(darkTheme)
    } else {
        fixedScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
