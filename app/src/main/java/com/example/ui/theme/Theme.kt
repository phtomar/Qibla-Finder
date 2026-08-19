package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.model.AppLanguage
import com.example.model.AppThemeId
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.getAppStrings

@Composable
fun MyApplicationTheme(
    themeId: AppThemeId = AppThemeId.GEOMETRIC_LIGHT,
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit
) {
    val palette = getThemePalette(themeId)
    val appStrings = getAppStrings(language)
    val layoutDirection = if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

    val colorScheme = if (palette.isDark) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = if (themeId == AppThemeId.MIDNIGHT_KAABA) Color(0xFF1E1600) else Color.Black,
            primaryContainer = palette.containerHigh,
            onPrimaryContainer = palette.onContainerHigh,
            secondary = palette.secondary,
            onSecondary = Color.Black,
            secondaryContainer = palette.container,
            onSecondaryContainer = palette.textPrimary,
            tertiary = palette.tertiary,
            onTertiary = Color.Black,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceVariant,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            outlineVariant = palette.borderSubtle
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.containerHigh,
            onPrimaryContainer = palette.onContainerHigh,
            secondary = palette.secondary,
            onSecondary = Color.White,
            secondaryContainer = palette.container,
            onSecondaryContainer = palette.textPrimary,
            tertiary = palette.tertiary,
            onTertiary = Color.White,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceVariant,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
            outlineVariant = palette.borderSubtle
        )
    }

    CompositionLocalProvider(
        LocalAppTheme provides palette,
        LocalAppStrings provides appStrings,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
