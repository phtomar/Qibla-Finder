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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CompassReading
import com.example.model.LocationData
import com.example.model.PrayerSchedule
import com.example.model.QiblaInfo
import com.example.model.SensorAccuracy
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBorderSubtle
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
fun QiblaBearingAndDistanceHeader(
    qibla: QiblaInfo,
    useKilometers: Boolean,
    modifier: Modifier = Modifier
) {
    val qiblaDirection = QiblaMath.getCardinalDirection(qibla.qiblaBearing)
    val distance = if (useKilometers) {
        String.format(Locale.getDefault(), "%,.0f km", qibla.distanceKm)
    } else {
        String.format(Locale.getDefault(), "%,.0f mi", qibla.distanceMiles)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Qibla Bearing Pill
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(GeoSurfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("under_dial_qibla_bearing")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = AppStr.qiblaBearing,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextSecondary
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f° %s", qibla.qiblaBearing, qiblaDirection),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }
            }
        }

        // Distance Pill with Custom Road to Kaaba Icon
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(GeoSurfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("under_dial_distance")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_road_to_kaaba),
                        contentDescription = AppStr.distance,
                        tint = GeoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = AppStr.distance,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextSecondary
                    )
                    Text(
                        text = distance,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                }
            }
        }
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
            .testTag("alignment_status_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isAligned) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isAligned) GeoPrimary else GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val arrowRotation = when {
                        qibla.relativeAngle > 0 -> 45f
                        qibla.relativeAngle < 0 -> -45f
                        else -> 0f
                    }
                    Icon(
                        imageVector = when {
                            isAligned -> Icons.Default.CheckCircle
                            !isLevel -> Icons.Default.Warning
                            else -> Icons.Default.Navigation
                        },
                        contentDescription = null,
                        tint = if (isAligned) Color.White else GeoPrimary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (!isAligned && isLevel) arrowRotation else 0f)
                    )
                }

                if (!isAligned && isLevel) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(GeoSurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${abs(qibla.relativeAngle).roundToInt()}°",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isAligned) AppStr.qiblaAligned else if (!isLevel) AppStr.deviceTilted else AppStr.turnPhone,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = if (isAligned) GeoOnContainerHigh else GeoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAligned) GeoOnContainerHigh else GeoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
            .clickable { onLocationClick() }
            .testTag("location_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GeoTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Precision Pill
                Box(
                    modifier = Modifier
                        .shadow(1.dp, CircleShape)
                        .clip(CircleShape)
                        .background(GeoSurface)
                        .clickable { onCalibrationClick() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp,
                        color = if (accuracy == SensorAccuracy.HIGH) GeoPrimary else GeoWarning
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = location.cityName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (location.countryName.isNotEmpty()) location.countryName else String.format(Locale.getDefault(), "%.2f° N", location.latitude),
                    fontSize = 11.sp,
                    color = GeoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
        shape = RoundedCornerShape(24.dp),
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
