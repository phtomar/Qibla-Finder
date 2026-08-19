package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CompassReading
import com.example.model.LocationData
import com.example.model.PrayerSchedule
import com.example.model.QiblaInfo
import com.example.model.SensorAccuracy
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoContainerHigh
import com.example.ui.theme.GeoOnContainerHigh
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.GeoWarning
import com.example.util.QiblaMath
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GeometricDegreeHeading(
    qibla: QiblaInfo,
    compass: CompassReading,
    modifier: Modifier = Modifier
) {
    val qiblaDirection = QiblaMath.getCardinalDirection(qibla.qiblaBearing)
    val angleDisplay = qibla.qiblaBearing.roundToInt()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Light Typography (e.g. 292°)
        Text(
            text = buildAnnotatedString {
                append("$angleDisplay")
                withStyle(style = SpanStyle(color = GeoPrimary, fontWeight = FontWeight.Normal)) {
                    append("°")
                }
            },
            fontSize = 46.sp,
            fontWeight = FontWeight.Light,
            color = GeoTextPrimary,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Subtitle: e.g. NORTHWEST • MAKKAH
        Text(
            text = "${qiblaDirection.uppercase()} • ${AppStr.makkah}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = GeoTextSecondary,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun AlignmentStatusBanner(
    qibla: QiblaInfo,
    compass: CompassReading,
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val isAligned = qibla.isAligned
    val isLevel = compass.isLevel

    val cardBg by animateColorAsState(
        targetValue = when {
            isAligned -> GeoContainerHigh
            !isLevel -> GeoSurfaceVariant
            else -> GeoSurfaceVariant
        },
        label = "geo_banner_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alignment_status_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isAligned) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isAligned) GeoPrimary else GeoContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isAligned -> Icons.Default.CheckCircle
                        !isLevel -> Icons.Default.Warning
                        qibla.relativeAngle > 0 -> Icons.Default.Navigation
                        else -> Icons.Outlined.NearMe
                    },
                    contentDescription = null,
                    tint = if (isAligned) Color.White else GeoPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAligned) AppStr.qiblaAligned else if (!isLevel) AppStr.deviceTilted else AppStr.turnPhone,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = if (isAligned) GeoOnContainerHigh else GeoTextSecondary
                )
                Text(
                    text = statusMessage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAligned) GeoOnContainerHigh else GeoTextPrimary
                )
            }

            if (!isAligned && isLevel) {
                Box(
                    modifier = Modifier
                        .shadow(1.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(GeoSurface)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${abs(qibla.relativeAngle).roundToInt()}°",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun GeometricLocationCard(
    location: LocationData,
    accuracy: SensorAccuracy,
    onLocationClick: () -> Unit,
    onCalibrationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLocationClick() }
            .testTag("location_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GeoTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = location.cityName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${location.countryName} • ${String.format(Locale.getDefault(), "%.2f° N", location.latitude)}",
                        fontSize = 12.sp,
                        color = GeoTextSecondary,
                        maxLines = 1
                    )
                }
            }

            // High Precision Pill
            Box(
                modifier = Modifier
                    .shadow(1.dp, CircleShape)
                    .clip(CircleShape)
                    .background(GeoSurface)
                    .clickable { onCalibrationClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("precision_pill")
            ) {
                val accLabel = when (accuracy) {
                    SensorAccuracy.HIGH -> AppStr.accuracyHigh
                    SensorAccuracy.MEDIUM -> AppStr.accuracyMedium
                    SensorAccuracy.LOW -> AppStr.accuracyLow
                    SensorAccuracy.UNRELIABLE -> AppStr.accuracyUnreliable
                }
                Text(
                    text = accLabel.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = if (accuracy == SensorAccuracy.HIGH) GeoPrimary else GeoWarning
                )
            }
        }
    }
}

@Composable
fun GeometricNextPrayerCard(
    prayerSchedule: PrayerSchedule,
    onPrayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPrayerClick() }
            .testTag("next_prayer_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GeoContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = AppStr.nextPrayer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = GeoOnContainerHigh
                )
                val localizedNextPrayer = when (prayerSchedule.nextPrayerName.lowercase()) {
                    "fajr" -> AppStr.fajr
                    "sunrise" -> AppStr.sunrise
                    "dhuhr" -> AppStr.dhuhr
                    "asr" -> AppStr.asr
                    "maghrib" -> AppStr.maghrib
                    "isha" -> AppStr.isha
                    else -> prayerSchedule.nextPrayerName
                }
                val prayerTime = when(prayerSchedule.nextPrayerName.lowercase()) {
                    "fajr" -> prayerSchedule.fajr
                    "sunrise" -> prayerSchedule.sunrise
                    "dhuhr" -> prayerSchedule.dhuhr
                    "asr" -> prayerSchedule.asr
                    "maghrib" -> prayerSchedule.maghrib
                    else -> prayerSchedule.isha
                }
                Text(
                    text = "$localizedNextPrayer $prayerTime",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoOnContainerHigh
                )
            }

            Text(
                text = "${AppStr.inTime} ${prayerSchedule.timeRemaining}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GeoOnContainerHigh
            )
        }
    }
}

@Composable
fun CompassMetricsRow(
    qibla: QiblaInfo,
    compass: CompassReading,
    useKilometers: Boolean,
    modifier: Modifier = Modifier
) {
    val distance = if (useKilometers) {
        String.format(Locale.getDefault(), "%,.0f km", qibla.distanceKm)
    } else {
        String.format(Locale.getDefault(), "%,.0f mi", qibla.distanceMiles)
    }

    val qiblaDirection = QiblaMath.getCardinalDirection(qibla.qiblaBearing)
    val currentHeadingDirection = QiblaMath.getCardinalDirection(compass.azimuth.toDouble())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GeometricMetricPill(
            title = AppStr.qiblaBearing,
            value = String.format(Locale.getDefault(), "%.1f°", qibla.qiblaBearing),
            subtext = qiblaDirection,
            icon = Icons.Default.Explore,
            highlight = true,
            modifier = Modifier.weight(1f)
        )
        GeometricMetricPill(
            title = AppStr.heading,
            value = String.format(Locale.getDefault(), "%.0f°", compass.azimuth),
            subtext = currentHeadingDirection,
            icon = Icons.Default.Navigation,
            highlight = false,
            modifier = Modifier.weight(1f)
        )
        GeometricMetricPill(
            title = AppStr.distance,
            value = distance,
            subtext = AppStr.toMakkah,
            icon = Icons.Default.Place,
            highlight = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GeometricMetricPill(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) GeoContainer else GeoSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlight) GeoPrimary else GeoTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (highlight) GeoPrimary else GeoTextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = GeoTextSecondary,
                maxLines = 1
            )
        }
    }
}
