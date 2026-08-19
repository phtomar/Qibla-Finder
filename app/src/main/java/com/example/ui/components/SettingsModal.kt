package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppPreferences
import com.example.model.AppThemeId
import com.example.model.CalculationMethod
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoBorderSubtle
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.getThemePalette
import com.example.util.QiblaMath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    preferences: AppPreferences,
    onToggleTrueNorth: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleDialMode: (Boolean) -> Unit,
    onToggleUnits: (Boolean) -> Unit,
    onToggleAutoDetectMethod: (Boolean) -> Unit = {},
    onSelectCalculationMethod: (CalculationMethod) -> Unit = {},
    onSelectTheme: (AppThemeId) -> Unit = {},
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val isArabic = preferences.language == AppLanguage.ARABIC

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GeoBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(GeoBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(scrollState)
                .testTag("settings_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStr.settingsTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = GeoTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppStr.close,
                        tint = GeoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 0: LANGUAGE SELECTION
            SectionHeader(title = AppStr.sectionLanguage)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GeoSurfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppLanguage.entries.forEach { lang ->
                    val isSelected = preferences.language == lang
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GeoContainer else Color.Transparent)
                            .clickable { onSelectLanguage(lang) }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (isSelected) GeoPrimary else GeoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "العربية (Arabic)" else "English (الإنجليزية)",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GeoPrimary else GeoTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: VISUAL THEMES
            SectionHeader(title = AppStr.sectionThemes)

            Text(
                text = AppStr.lightThemes,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GeoTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            // Light Themes
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppThemeId.entries.filter { !it.isDark }.forEach { themeId ->
                    ThemeSelectionCard(
                        themeId = themeId,
                        isArabic = isArabic,
                        isSelected = preferences.themeId == themeId,
                        onSelect = { onSelectTheme(themeId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = AppStr.darkThemes,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GeoTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            // Dark Themes
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppThemeId.entries.filter { it.isDark }.forEach { themeId ->
                    ThemeSelectionCard(
                        themeId = themeId,
                        isArabic = isArabic,
                        isSelected = preferences.themeId == themeId,
                        onSelect = { onSelectTheme(themeId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: PRAYER CALCULATION AUTHORITY
            SectionHeader(title = AppStr.sectionCalculationMethod)

            SettingToggleItem(
                title = AppStr.autoDetectAuthority,
                subtitle = AppStr.autoDetectSubtitle,
                icon = Icons.Default.AutoAwesome,
                checked = preferences.autoDetectCalculationMethod,
                onCheckedChange = onToggleAutoDetectMethod
            )

            // Calculation Method Picker
            Text(
                text = AppStr.officialAuthority,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = GeoTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GeoSurfaceVariant)
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalculationMethod.entries.forEach { method ->
                    val isSelected = method == preferences.calculationMethod
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GeoContainer else Color.Transparent)
                            .clickable { onSelectCalculationMethod(method) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) method.displayNameAr else method.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GeoPrimary else GeoTextPrimary
                            )
                            Text(
                                text = method.authorityName,
                                fontSize = 11.sp,
                                color = GeoTextSecondary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 3: COMPASS & SENSORS
            SectionHeader(title = AppStr.sectionCompass)

            // Setting 1: True North vs Magnetic North
            SettingToggleItem(
                title = AppStr.trueNorthTitle,
                subtitle = AppStr.trueNorthSubtitle,
                icon = Icons.Default.North,
                checked = preferences.useTrueNorth,
                onCheckedChange = onToggleTrueNorth
            )

            // Setting 2: Haptic Vibration on Alignment
            SettingToggleItem(
                title = AppStr.hapticTitle,
                subtitle = AppStr.hapticSubtitle,
                icon = Icons.Default.Vibration,
                checked = preferences.hapticsEnabled,
                onCheckedChange = onToggleHaptics
            )

            // Setting 3: Alignment Sound Chime
            SettingToggleItem(
                title = AppStr.soundTitle,
                subtitle = AppStr.soundSubtitle,
                icon = Icons.Default.VolumeUp,
                checked = preferences.soundEnabled,
                onCheckedChange = onToggleSound
            )

            // Setting 4: Compass Dial Mode
            SettingToggleItem(
                title = AppStr.rotatingDialTitle,
                subtitle = if (preferences.dialRotationMode) AppStr.rotatingDialSubtitleOn else AppStr.rotatingDialSubtitleOff,
                icon = Icons.Default.CompassCalibration,
                checked = preferences.dialRotationMode,
                onCheckedChange = onToggleDialMode
            )

            // Setting 5: Distance Units
            SettingToggleItem(
                title = AppStr.useKmTitle,
                subtitle = if (preferences.useKilometers) AppStr.useKmSubtitleOn else AppStr.useKmSubtitleOff,
                icon = Icons.Default.Straighten,
                checked = preferences.useKilometers,
                onCheckedChange = onToggleUnits
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kaaba Coordinates & Geodesy Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GeoSurfaceVariant)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = AppStr.kaabaCoordinatesTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                    }
                    Text(
                        text = if (isArabic)
                            "• دائرة العرض: ${QiblaMath.KAABA_LATITUDE}° شمالاً (21° 25′ 21″ N)\n• خط الطول: ${QiblaMath.KAABA_LONGITUDE}° شرقاً (39° 49′ 34″ E)\n• حساب زاوية القبلة الكروية العظمى بدقة جيوديسية رياضية متناهية."
                        else
                            "• Latitude: ${QiblaMath.KAABA_LATITUDE}° N (21° 25′ 21″ N)\n• Longitude: ${QiblaMath.KAABA_LONGITUDE}° E (39° 49′ 34″ E)\n• Great Circle forward azimuth calculation with spherical geodesy trigonometry.",
                        fontSize = 12.sp,
                        color = GeoTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectionCard(
    themeId: AppThemeId,
    isArabic: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val palette = getThemePalette(themeId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) GeoContainer else GeoSurfaceVariant)
            .clickable { onSelect() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Color preview dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(palette.background)
                            .border(1.dp, palette.border, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(palette.primary)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(palette.containerHigh)
                            .border(1.dp, palette.borderSubtle, CircleShape)
                    )
                }

                Column {
                    Text(
                        text = if (isArabic) themeId.displayNameAr else themeId.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) GeoPrimary else GeoTextPrimary
                    )
                    Text(
                        text = if (isArabic) themeId.subtitleAr else themeId.subtitle,
                        fontSize = 11.sp,
                        color = GeoTextSecondary
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(GeoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = GeoPrimary,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GeoSurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = GeoTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GeoPrimary,
                    uncheckedThumbColor = GeoTextSecondary,
                    uncheckedTrackColor = GeoSurface,
                    checkedBorderColor = GeoPrimary,
                    uncheckedBorderColor = GeoBorderSubtle
                )
            )
        }
    }
}
