package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.components.AlignmentStatusBanner
import com.example.ui.components.CalibrationModal
import com.example.ui.components.CitySelectorModal
import com.example.ui.components.CompassMetricsRow
import com.example.ui.components.GeometricDegreeHeading
import com.example.ui.components.GeometricLocationCard
import com.example.ui.components.GeometricNextPrayerCard
import com.example.ui.components.NearbyMosquesModal
import com.example.ui.components.PrayerTimesSheet
import com.example.ui.components.QiblaCompassDial
import com.example.ui.components.SettingsModal
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.viewmodel.QiblaViewModel

@Composable
fun QiblaFinderScreen(
    viewModel: QiblaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Permission launcher for location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.onLocationPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GeoBackground,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            GeometricBottomNavBar(
                onQiblaClick = { /* Already on Qibla */ },
                onPrayersClick = { viewModel.togglePrayerTimes(true) },
                onNearbyClick = { viewModel.toggleNearbyMosques(true) },
                onCalibrationClick = { viewModel.toggleCalibrationDialog(true) },
                onSettingsClick = { viewModel.toggleSettings(true) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar (64px height)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GeoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = AppStr.appTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleNearbyMosques(true) },
                        modifier = Modifier.testTag("header_nearby_btn")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_masjid),
                            contentDescription = AppStr.navNearby,
                            tint = GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshGpsLocation() },
                        modifier = Modifier.testTag("header_gps_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = AppStr.useCurrentGps,
                            tint = GeoTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleSettings(true) },
                        modifier = Modifier.testTag("header_more_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = AppStr.settingsTitle,
                            tint = GeoTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Central Geometric Compass Dial (Option 1: Target on Ring with Independent Guidance Pointer)
            QiblaCompassDial(
                compass = uiState.compass,
                qibla = uiState.qibla,
                dialRotationMode = uiState.preferences.dialRotationMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Geometric Degree Heading (e.g. 292° NORTHWEST • MAKKAH)
            GeometricDegreeHeading(
                qibla = uiState.qibla,
                compass = uiState.compass
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Alignment Status Banner
            AlignmentStatusBanner(
                qibla = uiState.qibla,
                compass = uiState.compass,
                statusMessage = uiState.statusMessage
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Location Card (South Kensington, London, UK • High Precision)
            GeometricLocationCard(
                location = uiState.location,
                accuracy = uiState.compass.accuracy,
                onLocationClick = { viewModel.toggleCitySelector(true) },
                onCalibrationClick = { viewModel.toggleCalibrationDialog(true) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Next Prayer Card (Next Prayer: Asr at 15:42 • In 1h 24m)
            GeometricNextPrayerCard(
                prayerSchedule = uiState.prayerSchedule,
                onPrayerClick = { viewModel.togglePrayerTimes(true) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Compass Metrics Row (Bearing, Heading, Distance)
            CompassMetricsRow(
                qibla = uiState.qibla,
                compass = uiState.compass,
                useKilometers = uiState.preferences.useKilometers
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Sheets and Dialogs
    if (uiState.showPrayerTimes) {
        PrayerTimesSheet(
            prayerSchedule = uiState.prayerSchedule,
            location = uiState.location,
            language = uiState.preferences.language,
            currentMethod = uiState.preferences.calculationMethod,
            onSelectMethod = { viewModel.setCalculationMethod(it) },
            onDismiss = { viewModel.togglePrayerTimes(false) }
        )
    }

    if (uiState.showNearbyMosques) {
        NearbyMosquesModal(
            location = uiState.location,
            useKilometers = uiState.preferences.useKilometers,
            language = uiState.preferences.language,
            onDismiss = { viewModel.toggleNearbyMosques(false) }
        )
    }

    if (uiState.showCitySelector) {
        CitySelectorModal(
            currentCityName = uiState.location.cityName,
            isLoadingGps = uiState.isLocationLoading,
            onUseGps = { viewModel.refreshGpsLocation() },
            onSelectCity = { viewModel.selectCity(it) },
            onCustomCoordinates = { lat, lon, name -> viewModel.setCustomCoordinates(lat, lon, name) },
            onDismiss = { viewModel.toggleCitySelector(false) }
        )
    }

    if (uiState.showCalibrationDialog) {
        CalibrationModal(
            accuracy = uiState.compass.accuracy,
            onDismiss = { viewModel.toggleCalibrationDialog(false) }
        )
    }

    if (uiState.showSettings) {
        SettingsModal(
            preferences = uiState.preferences,
            onToggleTrueNorth = { viewModel.setTrueNorth(it) },
            onToggleHaptics = { viewModel.setHapticsEnabled(it) },
            onToggleSound = { viewModel.setSoundEnabled(it) },
            onToggleDialMode = { viewModel.setDialRotationMode(it) },
            onToggleUnits = { viewModel.setUseKilometers(it) },
            onToggleAutoDetectMethod = { viewModel.setAutoDetectCalculationMethod(it) },
            onSelectCalculationMethod = { viewModel.setCalculationMethod(it) },
            onSelectTheme = { viewModel.setTheme(it) },
            onSelectLanguage = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.toggleSettings(false) }
        )
    }
}

@Composable
private fun GeometricBottomNavBar(
    onQiblaClick: () -> Unit,
    onPrayersClick: () -> Unit,
    onNearbyClick: () -> Unit,
    onCalibrationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GeoSurfaceVariant)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                painter = rememberVectorPainter(Icons.Default.Explore),
                label = AppStr.navQibla,
                isSelected = true,
                onClick = onQiblaClick
            )
            NavItem(
                painter = rememberVectorPainter(Icons.Outlined.Schedule),
                label = AppStr.navPrayers,
                isSelected = false,
                onClick = onPrayersClick
            )
            NavItem(
                painter = painterResource(id = R.drawable.ic_masjid),
                label = AppStr.navNearby,
                isSelected = false,
                onClick = onNearbyClick
            )
            NavItem(
                painter = rememberVectorPainter(Icons.Outlined.CompassCalibration),
                label = AppStr.navCalibrate,
                isSelected = false,
                onClick = onCalibrationClick
            )
            NavItem(
                painter = rememberVectorPainter(Icons.Outlined.Settings),
                label = AppStr.navSettings,
                isSelected = false,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun NavItem(
    painter: Painter,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) GeoContainer else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = label,
                tint = if (isSelected) GeoTextPrimary else GeoTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) GeoTextPrimary else GeoTextSecondary,
            maxLines = 1
        )
    }
}
