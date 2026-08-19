package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeId

/**
 * Complete Theme Palette definition for Qibla Finder.
 */
data class AppThemePalette(
    val id: AppThemeId,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val container: Color,
    val containerHigh: Color,
    val onContainerHigh: Color,
    val primary: Color,
    val primaryDark: Color,
    val secondary: Color,
    val tertiary: Color,
    val border: Color,
    val borderSubtle: Color,
    val dashedGuide: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val north: Color,
    val cardinal: Color,
    val centerHub: Color,
    val kaabaBadge: Color,
    val goldAccent: Color,
    val success: Color = Color(0xFF2E7D32),
    val warning: Color = Color(0xFFE65100),
    val error: Color = Color(0xFFB3261E)
)

// ==========================================
// 3 LIGHT THEMES
// ==========================================

// 1. Geometric Amethyst (Light)
val PaletteGeometricLight = AppThemePalette(
    id = AppThemeId.GEOMETRIC_LIGHT,
    isDark = false,
    background = Color(0xFFFCF8F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF3EDF7),
    container = Color(0xFFE8DEF8),
    containerHigh = Color(0xFFEADDFF),
    onContainerHigh = Color(0xFF21005D),
    primary = Color(0xFF6750A4),
    primaryDark = Color(0xFF4F378B),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
    border = Color(0xFFCAC4D0),
    borderSubtle = Color(0xFFE7E0EC),
    dashedGuide = Color(0xFF938F99),
    textPrimary = Color(0xFF1D1B20),
    textSecondary = Color(0xFF49454F),
    textMuted = Color(0xFF79747E),
    north = Color(0xFF6750A4),
    cardinal = Color(0xFF49454F),
    centerHub = Color(0xFF1D1B20),
    kaabaBadge = Color(0xFF6750A4),
    goldAccent = Color(0xFFD4AF37)
)

// 2. Emerald Oasis (Sacred Islamic Green Light)
val PaletteEmeraldLight = AppThemePalette(
    id = AppThemeId.EMERALD_LIGHT,
    isDark = false,
    background = Color(0xFFF4FBF6),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE3F3EA),
    container = Color(0xFFC6ECD6),
    containerHigh = Color(0xFFA9DFBD),
    onContainerHigh = Color(0xFF002114),
    primary = Color(0xFF006C4C),
    primaryDark = Color(0xFF005239),
    secondary = Color(0xFF4D6356),
    tertiary = Color(0xFF3E6374),
    border = Color(0xFFB8D4C5),
    borderSubtle = Color(0xFFD9EBE0),
    dashedGuide = Color(0xFF7FA894),
    textPrimary = Color(0xFF062117),
    textSecondary = Color(0xFF3E5348),
    textMuted = Color(0xFF70867A),
    north = Color(0xFF006C4C),
    cardinal = Color(0xFF3E5348),
    centerHub = Color(0xFF062117),
    kaabaBadge = Color(0xFF006C4C),
    goldAccent = Color(0xFFC59E30)
)

// 3. Desert Sandstone (Warm Terracotta & Makkah Stone Light)
val PaletteSandstoneLight = AppThemePalette(
    id = AppThemeId.SANDSTONE_LIGHT,
    isDark = false,
    background = Color(0xFFFFF8F3),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF8EDE2),
    container = Color(0xFFFFDCC2),
    containerHigh = Color(0xFFF6CBA5),
    onContainerHigh = Color(0xFF2F1500),
    primary = Color(0xFF8C5000),
    primaryDark = Color(0xFF6D3E00),
    secondary = Color(0xFF715A42),
    tertiary = Color(0xFF58633A),
    border = Color(0xFFDAC5B2),
    borderSubtle = Color(0xFFEFE0D3),
    dashedGuide = Color(0xFFA8927F),
    textPrimary = Color(0xFF2B1700),
    textSecondary = Color(0xFF594332),
    textMuted = Color(0xFF8B7462),
    north = Color(0xFF8C5000),
    cardinal = Color(0xFF594332),
    centerHub = Color(0xFF2B1700),
    kaabaBadge = Color(0xFF8C5000),
    goldAccent = Color(0xFFD49A00)
)

// ==========================================
// 3 DARK THEMES
// ==========================================

// 4. Midnight Kaaba (Deep Obsidian & Kiswah Gold Dark)
val PaletteMidnightKaaba = AppThemePalette(
    id = AppThemeId.MIDNIGHT_KAABA,
    isDark = true,
    background = Color(0xFF0D0E11),
    surface = Color(0xFF16181D),
    surfaceVariant = Color(0xFF20232A),
    container = Color(0xFF2C303B),
    containerHigh = Color(0xFF3A2E16),
    onContainerHigh = Color(0xFFFCE49E),
    primary = Color(0xFFE5C158),
    primaryDark = Color(0xFFC7A336),
    secondary = Color(0xFFC7C5D0),
    tertiary = Color(0xFFE5B8CC),
    border = Color(0xFF363B47),
    borderSubtle = Color(0xFF262932),
    dashedGuide = Color(0xFF565C6D),
    textPrimary = Color(0xFFF2F2F5),
    textSecondary = Color(0xFF9EA3B0),
    textMuted = Color(0xFF6C7282),
    north = Color(0xFFE5C158),
    cardinal = Color(0xFF9EA3B0),
    centerHub = Color(0xFF2C303B),
    kaabaBadge = Color(0xFFE5C158),
    goldAccent = Color(0xFFF3D87E)
)

// 5. Royal Celestial (Midnight Indigo & Cosmic Lilac Dark)
val PaletteRoyalCelestial = AppThemePalette(
    id = AppThemeId.ROYAL_CELESTIAL,
    isDark = true,
    background = Color(0xFF110E18),
    surface = Color(0xFF1A1624),
    surfaceVariant = Color(0xFF262035),
    container = Color(0xFF3B3252),
    containerHigh = Color(0xFF4B3E6B),
    onContainerHigh = Color(0xFFF1EAFD),
    primary = Color(0xFFD0BCFF),
    primaryDark = Color(0xFFB197E6),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    border = Color(0xFF42395A),
    borderSubtle = Color(0xFF2E273F),
    dashedGuide = Color(0xFF6E628D),
    textPrimary = Color(0xFFECE6F0),
    textSecondary = Color(0xFFB3ABCC),
    textMuted = Color(0xFF7B748C),
    north = Color(0xFFD0BCFF),
    cardinal = Color(0xFFA69EBA),
    centerHub = Color(0xFF2E273F),
    kaabaBadge = Color(0xFFD0BCFF),
    goldAccent = Color(0xFFE8C557)
)

// 6. Arabian Night (Malachite Night & High-Contrast Luminous Emerald Dark)
val PaletteArabianNight = AppThemePalette(
    id = AppThemeId.ARABIAN_NIGHT,
    isDark = true,
    background = Color(0xFF071810),
    surface = Color(0xFF0E281C),
    surfaceVariant = Color(0xFF163C2B),
    container = Color(0xFF1D523A),
    containerHigh = Color(0xFF236A4B),
    onContainerHigh = Color(0xFFE8FFF3),
    primary = Color(0xFF38EF9E),
    primaryDark = Color(0xFF12D478),
    secondary = Color(0xFFD8EFE3),
    tertiary = Color(0xFFB9E8FA),
    border = Color(0xFF3D7A5B),
    borderSubtle = Color(0xFF26543D),
    dashedGuide = Color(0xFF6BAF8C),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFC0E7D4),
    textMuted = Color(0xFF86B8A0),
    north = Color(0xFF38EF9E),
    cardinal = Color(0xFFD8EFE3),
    centerHub = Color(0xFF133624),
    kaabaBadge = Color(0xFF38EF9E),
    goldAccent = Color(0xFFFFD54F)
)

fun getThemePalette(id: AppThemeId): AppThemePalette = when (id) {
    AppThemeId.GEOMETRIC_LIGHT -> PaletteGeometricLight
    AppThemeId.EMERALD_LIGHT -> PaletteEmeraldLight
    AppThemeId.SANDSTONE_LIGHT -> PaletteSandstoneLight
    AppThemeId.MIDNIGHT_KAABA -> PaletteMidnightKaaba
    AppThemeId.ROYAL_CELESTIAL -> PaletteRoyalCelestial
    AppThemeId.ARABIAN_NIGHT -> PaletteArabianNight
}

val LocalAppTheme = staticCompositionLocalOf { PaletteGeometricLight }

// Backward compatibility legacy accessors
val GeoBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.background

val GeoSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.surface

val GeoSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.surfaceVariant

val GeoContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.container

val GeoContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.containerHigh

val GeoOnContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.onContainerHigh

val GeoPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.primary

val GeoPrimaryDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.primaryDark

val GeoSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.secondary

val GeoTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.tertiary

val GeoBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.border

val GeoBorderSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.borderSubtle

val GeoDashedGuide: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.dashedGuide

val GeoTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.textPrimary

val GeoTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.textSecondary

val GeoTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.textMuted

val GeoNorth: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.north

val GeoCardinal: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.cardinal

val GeoCenterHub: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.centerHub

val GeoKaabaBadge: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.kaabaBadge

val GeoGold: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.goldAccent

val GeoSuccess: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.success

val GeoWarning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.warning

val GeoError: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current.error
