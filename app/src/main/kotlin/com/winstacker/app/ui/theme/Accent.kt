package com.winstacker.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.winstacker.app.data.repository.AppAccent

/**
 * The colour roles an accent overrides on top of the base light/dark schemes.
 * Surfaces, outlines and error colours are shared across accents, so only the
 * brand roles change when the user picks a different palette.
 */
data class AccentTones(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color
)

data class AccentPalette(
    val accent: AppAccent,
    val label: String,
    /** Colour shown in the settings picker. */
    val swatch: Color,
    val light: AccentTones,
    val dark: AccentTones
)

private val GreenPalette = AccentPalette(
    accent = AppAccent.GREEN,
    label = "Green",
    swatch = Color(0xFF3E8752),
    light = AccentTones(
        primary = Color(0xFF3E8752),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF0E2E18),
        secondary = Color(0xFF26A69A),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0F2F1),
        onSecondaryContainer = Color(0xFF00352F),
        tertiary = Color(0xFFFF7043),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFBE9E7)
    ),
    dark = AccentTones(
        primary = Color(0xFF81C784),
        onPrimary = Color(0xFF003314),
        primaryContainer = Color(0xFF20512F),
        onPrimaryContainer = Color(0xFFC8E6C9),
        secondary = Color(0xFF80CBC4),
        onSecondary = Color(0xFF00352F),
        secondaryContainer = Color(0xFF00695C),
        onSecondaryContainer = Color(0xFFE0F2F1),
        tertiary = Color(0xFFFF8A65),
        onTertiary = Color(0xFF5D1A00),
        tertiaryContainer = Color(0xFFBF360C)
    )
)

private val BluePalette = AccentPalette(
    accent = AppAccent.BLUE,
    label = "Blue",
    swatch = Color(0xFF2E6FD4),
    light = AccentTones(
        primary = Color(0xFF2E6FD4),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD3E3FD),
        onPrimaryContainer = Color(0xFF0B2A5B),
        secondary = Color(0xFF0B6E9E),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD2E9F5),
        onSecondaryContainer = Color(0xFF04293C),
        tertiary = Color(0xFF7E57C2),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFEDE7F6)
    ),
    dark = AccentTones(
        primary = Color(0xFFA8C7FA),
        onPrimary = Color(0xFF0A2D63),
        primaryContainer = Color(0xFF1B4584),
        onPrimaryContainer = Color(0xFFD3E3FD),
        secondary = Color(0xFF8FD0EE),
        onSecondary = Color(0xFF04293C),
        secondaryContainer = Color(0xFF0C5579),
        onSecondaryContainer = Color(0xFFD2E9F5),
        tertiary = Color(0xFFC5B0F0),
        onTertiary = Color(0xFF2C1D5C),
        tertiaryContainer = Color(0xFF4B3A86)
    )
)

private val PurplePalette = AccentPalette(
    accent = AppAccent.PURPLE,
    label = "Purple",
    swatch = Color(0xFF6D4BC7),
    light = AccentTones(
        primary = Color(0xFF6D4BC7),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE6DDFF),
        onPrimaryContainer = Color(0xFF23105E),
        secondary = Color(0xFFA6478F),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFBDDF0),
        onSecondaryContainer = Color(0xFF3A0A2D),
        tertiary = Color(0xFFEC7A4F),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFCE5DC)
    ),
    dark = AccentTones(
        primary = Color(0xFFC7B2FF),
        onPrimary = Color(0xFF30156F),
        primaryContainer = Color(0xFF4A2C96),
        onPrimaryContainer = Color(0xFFE6DDFF),
        secondary = Color(0xFFF0AFD8),
        onSecondary = Color(0xFF4D1140),
        secondaryContainer = Color(0xFF74215F),
        onSecondaryContainer = Color(0xFFFBDDF0),
        tertiary = Color(0xFFFFB59A),
        onTertiary = Color(0xFF5A2411),
        tertiaryContainer = Color(0xFF7E3A1F)
    )
)

private val OrangePalette = AccentPalette(
    accent = AppAccent.ORANGE,
    label = "Orange",
    swatch = Color(0xFFC75B14),
    light = AccentTones(
        primary = Color(0xFFC75B14),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDCC4),
        onPrimaryContainer = Color(0xFF3D1800),
        secondary = Color(0xFF8A6A3C),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF6E6CF),
        onSecondaryContainer = Color(0xFF2C1F0B),
        tertiary = Color(0xFF4F8A5B),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFD9EFDD)
    ),
    dark = AccentTones(
        primary = Color(0xFFFFB68A),
        onPrimary = Color(0xFF4D1E00),
        primaryContainer = Color(0xFF7A3708),
        onPrimaryContainer = Color(0xFFFFDCC4),
        secondary = Color(0xFFDFC29A),
        onSecondary = Color(0xFF40300F),
        secondaryContainer = Color(0xFF5C4723),
        onSecondaryContainer = Color(0xFFF6E6CF),
        tertiary = Color(0xFF9ED6A8),
        onTertiary = Color(0xFF0C3A17),
        tertiaryContainer = Color(0xFF2A5733)
    )
)

private val RosePalette = AccentPalette(
    accent = AppAccent.ROSE,
    label = "Rose",
    swatch = Color(0xFFC2185B),
    light = AccentTones(
        primary = Color(0xFFC2185B),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFD9E2),
        onPrimaryContainer = Color(0xFF3F0019),
        secondary = Color(0xFF7D5260),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF3E0E6),
        onSecondaryContainer = Color(0xFF2F131C),
        tertiary = Color(0xFF7A5B2E),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFF7E6CC)
    ),
    dark = AccentTones(
        primary = Color(0xFFFFB1C8),
        onPrimary = Color(0xFF5E1133),
        primaryContainer = Color(0xFF8C1E4C),
        onPrimaryContainer = Color(0xFFFFD9E2),
        secondary = Color(0xFFE6BDC9),
        onSecondary = Color(0xFF452932),
        secondaryContainer = Color(0xFF5E3F48),
        onSecondaryContainer = Color(0xFFF3E0E6),
        tertiary = Color(0xFFE8C48D),
        onTertiary = Color(0xFF44300B),
        tertiaryContainer = Color(0xFF5E4621)
    )
)

/**
 * Wallpaper-derived palette. The swatch is only used for the settings picker;
 * the real colours come from the platform dynamic colour schemes.
 */
private val DynamicPalette = AccentPalette(
    accent = AppAccent.DYNAMIC,
    label = "Wallpaper",
    swatch = Color(0xFF7D7A86),
    light = GreenPalette.light,
    dark = GreenPalette.dark
)

/** Fixed palettes in picker order. DYNAMIC is appended by the UI when supported. */
val accentPalettes: List<AccentPalette> = listOf(
    GreenPalette,
    BluePalette,
    PurplePalette,
    OrangePalette,
    RosePalette
)

val dynamicAccentPalette: AccentPalette = DynamicPalette

fun paletteFor(accent: AppAccent): AccentPalette =
    accentPalettes.firstOrNull { it.accent == accent }
        ?: if (accent == AppAccent.DYNAMIC) DynamicPalette else GreenPalette
