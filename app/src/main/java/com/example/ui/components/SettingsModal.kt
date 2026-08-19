package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.AppPreferences
import com.example.model.AppThemeId
import com.example.model.CalculationMethod
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
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
    onToggleClockFormat: (Boolean) -> Unit = {},
    onToggleAutoDetectMethod: (Boolean) -> Unit = {},
    onSelectCalculationMethod: (CalculationMethod) -> Unit = {},
    onSelectTheme: (AppThemeId) -> Unit = {},
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onCalibrateClick: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isArabic = preferences.language == AppLanguage.ARABIC

    var showMethodPicker by remember { mutableStateOf(false) }

    // All categories collapsed as default per user requirement
    var isLanguageExpanded by remember { mutableStateOf(false) }
    var isThemesExpanded by remember { mutableStateOf(false) }
    var isPreferencesExpanded by remember { mutableStateOf(false) }
    var isCalculationExpanded by remember { mutableStateOf(false) }
    var isSensorsExpanded by remember { mutableStateOf(false) }
    var isFeedbackExpanded by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings_modal"),
            color = GeoBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStr.settingsTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStr.close,
                            tint = GeoTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 1: LANGUAGE (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionLanguage,
                    icon = Icons.Default.Language,
                    isExpanded = isLanguageExpanded,
                    onToggleExpand = { isLanguageExpanded = !isLanguageExpanded }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GeoSurface)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = preferences.language == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) GeoContainer else Color.Transparent)
                                    .clickable { onSelectLanguage(lang) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "العربية" else "English",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GeoPrimary else GeoTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 2: VISUAL THEMES (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionThemes,
                    icon = Icons.Default.Palette,
                    isExpanded = isThemesExpanded,
                    onToggleExpand = { isThemesExpanded = !isThemesExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Light Themes Row
                        Text(
                            text = AppStr.lightThemes,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppThemeId.entries.filter { !it.isDark }.forEach { themeId ->
                                CompactThemeSwatch(
                                    themeId = themeId,
                                    isArabic = isArabic,
                                    isSelected = preferences.themeId == themeId,
                                    onSelect = { onSelectTheme(themeId) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Dark Themes Row
                        Text(
                            text = AppStr.darkThemes,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppThemeId.entries.filter { it.isDark }.forEach { themeId ->
                                CompactThemeSwatch(
                                    themeId = themeId,
                                    isArabic = isArabic,
                                    isSelected = preferences.themeId == themeId,
                                    onSelect = { onSelectTheme(themeId) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 3: APP PREFERENCES (2x3 Grid of 3 settings items: Dial Mode, Distance Unit, Clock Format)
                CollapsibleSettingsCard(
                    title = AppStr.sectionAppPreferences,
                    icon = Icons.Default.Tune,
                    isExpanded = isPreferencesExpanded,
                    onToggleExpand = { isPreferencesExpanded = !isPreferencesExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Item 1: Dial Mode (Rotating Dial / Fixed Dial)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = AppStr.rotatingDialTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoTextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoSurface)
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SegmentedOption(
                                    label = AppStr.dialRotating,
                                    isSelected = preferences.dialRotationMode,
                                    onClick = { onToggleDialMode(true) },
                                    modifier = Modifier.weight(1f)
                                )
                                SegmentedOption(
                                    label = AppStr.dialFixed,
                                    isSelected = !preferences.dialRotationMode,
                                    onClick = { onToggleDialMode(false) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Item 2: Distance Unit (Kilometers / Miles)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = AppStr.useKmTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoTextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoSurface)
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SegmentedOption(
                                    label = AppStr.unitKm,
                                    isSelected = preferences.useKilometers,
                                    onClick = { onToggleUnits(true) },
                                    modifier = Modifier.weight(1f)
                                )
                                SegmentedOption(
                                    label = AppStr.unitMiles,
                                    isSelected = !preferences.useKilometers,
                                    onClick = { onToggleUnits(false) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Item 3: Clock Format (12-Hour / 24-Hour)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = AppStr.clockFormatTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoTextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoSurface)
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SegmentedOption(
                                    label = AppStr.clock12Hour,
                                    isSelected = !preferences.use24HourFormat,
                                    onClick = { onToggleClockFormat(false) },
                                    modifier = Modifier.weight(1f)
                                )
                                SegmentedOption(
                                    label = AppStr.clock24Hour,
                                    isSelected = preferences.use24HourFormat,
                                    onClick = { onToggleClockFormat(true) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 4: PRAYER CALCULATION AUTHORITY (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionCalculationMethod,
                    icon = Icons.Default.AutoAwesome,
                    isExpanded = isCalculationExpanded,
                    onToggleExpand = { isCalculationExpanded = !isCalculationExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Auto-Detect Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.autoDetectAuthority,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = AppStr.autoDetectSubtitle,
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }

                            Switch(
                                checked = preferences.autoDetectCalculationMethod,
                                onCheckedChange = onToggleAutoDetectMethod,
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

                        HorizontalDivider(color = GeoBorderSubtle, thickness = 0.8.dp)

                        // Current Authority Selector Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GeoSurface)
                                .clickable { showMethodPicker = true }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.officialAuthority,
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                                Text(
                                    text = if (isArabic) preferences.calculationMethod.displayNameAr else preferences.calculationMethod.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 5: SENSORS & CALIBRATION (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionSensorsUnits,
                    icon = Icons.Default.Explore,
                    isExpanded = isSensorsExpanded,
                    onToggleExpand = { isSensorsExpanded = !isSensorsExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // True North vs Magnetic North
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.trueNorthTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = AppStr.trueNorthSubtitle,
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }

                            Switch(
                                checked = preferences.useTrueNorth,
                                onCheckedChange = onToggleTrueNorth,
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

                        HorizontalDivider(color = GeoBorderSubtle, thickness = 0.8.dp)

                        // Direct Compass Calibration Trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GeoSurface)
                                .clickable { onCalibrateClick() }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = AppStr.calibrationTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = AppStr.calibrationSubtitle,
                                        fontSize = 11.sp,
                                        color = GeoTextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 6: FEEDBACK & ALERTS (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionFeedback,
                    icon = Icons.Default.VolumeUp,
                    isExpanded = isFeedbackExpanded,
                    onToggleExpand = { isFeedbackExpanded = !isFeedbackExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Audio Chime
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.soundTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = AppStr.soundSubtitle,
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }

                            Switch(
                                checked = preferences.soundEnabled,
                                onCheckedChange = onToggleSound,
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

                        HorizontalDivider(color = GeoBorderSubtle, thickness = 0.8.dp)

                        // Haptic Vibration Pulse
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.hapticTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = AppStr.hapticSubtitle,
                                    fontSize = 11.sp,
                                    color = GeoTextSecondary
                                )
                            }

                            Switch(
                                checked = preferences.hapticsEnabled,
                                onCheckedChange = onToggleHaptics,
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

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 7: ABOUT APP (Collapsible - Collapsed by default)
                CollapsibleSettingsCard(
                    title = AppStr.sectionAboutApp,
                    icon = Icons.Default.Info,
                    isExpanded = isAboutExpanded,
                    onToggleExpand = { isAboutExpanded = !isAboutExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // App Name & Version Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStr.aboutAppName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = AppStr.aboutAppVersion,
                                    fontSize = 11.sp,
                                    color = GeoPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GeoContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "v2.4.0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary
                                )
                            }
                        }

                        // App Description
                        Text(
                            text = AppStr.aboutAppDescription,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = GeoTextSecondary
                        )

                        HorizontalDivider(color = GeoBorderSubtle, thickness = 0.8.dp)

                        // Holy Kaaba Coordinates Reference
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppStr.kaabaCoordinatesTitle,
                                fontSize = 12.sp,
                                color = GeoTextSecondary
                            )
                            Text(
                                text = "${QiblaMath.KAABA_LATITUDE}° N, ${QiblaMath.KAABA_LONGITUDE}° E",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimary
                            )
                        }

                        HorizontalDivider(color = GeoBorderSubtle, thickness = 0.8.dp)

                        // Privacy Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GeoSurface)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = AppStr.aboutAppPrivacy,
                                fontSize = 11.sp,
                                color = GeoTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Calculation Method Picker Dialog
    if (showMethodPicker) {
        BasicAlertDialog(
            onDismissRequest = { showMethodPicker = false }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = GeoSurface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = AppStr.officialAuthority,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )

                    CalculationMethod.entries.forEach { method ->
                        val isSelected = preferences.calculationMethod == method
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) GeoContainer else GeoSurfaceVariant)
                                .clickable {
                                    onSelectCalculationMethod(method)
                                    showMethodPicker = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                    text = "Fajr: ${method.fajrAngle}° | Isha: ${if (method.ishaIntervalMinutes != null) "${method.ishaIntervalMinutes}m" else "${method.ishaAngle}°"}",
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
            }
        }
    }
}

@Composable
private fun CollapsibleSettingsCard(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GeoSurfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(if (isExpanded) RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp) else RoundedCornerShape(18.dp))
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = GeoPrimary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GeoTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun CompactThemeSwatch(
    themeId: AppThemeId,
    isArabic: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = getThemePalette(themeId)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) GeoContainer else GeoSurface)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) GeoPrimary else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 3-dot color swatch preview
            Row(
                horizontalArrangement = Arrangement.spacedBy((-3).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(palette.background)
                        .border(1.dp, palette.border, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(palette.primary)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(palette.containerHigh)
                        .border(1.dp, palette.borderSubtle, CircleShape)
                )
            }

            Text(
                text = if (isArabic) themeId.displayNameAr else themeId.displayName,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) GeoPrimary else GeoTextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SegmentedOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GeoContainer else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GeoPrimary else GeoTextSecondary,
            maxLines = 1
        )
    }
}
