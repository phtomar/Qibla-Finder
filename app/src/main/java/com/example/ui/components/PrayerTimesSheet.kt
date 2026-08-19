package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.model.AppLanguage
import com.example.model.CalculationMethod
import com.example.model.LocationData
import com.example.model.PrayerSchedule
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoContainerHigh
import com.example.ui.theme.GeoOnContainerHigh
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesSheet(
    prayerSchedule: PrayerSchedule,
    location: LocationData,
    language: AppLanguage = AppLanguage.ENGLISH,
    currentMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    onSelectMethod: (CalculationMethod) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    var showMethodSelector by remember { mutableStateOf(false) }
    val isArabic = language == AppLanguage.ARABIC

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
                .testTag("prayer_times_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStr.prayerTimesTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = "${location.cityName} • ${AppStr.astronomicalSchedule}",
                        fontSize = 13.sp,
                        color = GeoTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppStr.close,
                        tint = GeoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Calculation Authority Card (with dropdown toggle)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(GeoSurfaceVariant)
                    .clickable { showMethodSelector = !showMethodSelector }
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = AppStr.calculationAuthority,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary
                            )
                            Text(
                                text = if (isArabic) currentMethod.displayNameAr else currentMethod.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoPrimary
                            )
                        }
                    }
                    Icon(
                        imageVector = if (showMethodSelector) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = GeoTextSecondary
                    )
                }
            }

            // Calculation Method Selection List
            if (showMethodSelector) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GeoContainer.copy(alpha = 0.4f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalculationMethod.entries.forEach { method ->
                        val isSelected = method == currentMethod
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GeoContainer else Color.Transparent)
                            .clickable {
                                onSelectMethod(method)
                                showMethodSelector = false
                            }
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
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Next Prayer Highlight Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GeoContainerHigh)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GeoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = AppStr.nextPrayer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
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
                            Text(
                                text = localizedNextPrayer,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnContainerHigh
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = AppStr.timeRemaining,
                            fontSize = 11.sp,
                            color = GeoOnContainerHigh.copy(alpha = 0.8f)
                        )
                        Text(
                            text = prayerSchedule.timeRemaining,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoOnContainerHigh
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prayer List
            PrayerRowItem(
                name = AppStr.fajr,
                subName = if (isArabic) "Fajr" else "الفجر",
                time = prayerSchedule.fajr,
                icon = Icons.Default.WbTwilight,
                isNext = prayerSchedule.nextPrayerName.equals("fajr", ignoreCase = true)
            )
            PrayerRowItem(
                name = AppStr.sunrise,
                subName = if (isArabic) "Sunrise" else "الشروق",
                time = prayerSchedule.sunrise,
                icon = Icons.Default.WbSunny,
                isNext = prayerSchedule.nextPrayerName.equals("sunrise", ignoreCase = true)
            )
            PrayerRowItem(
                name = AppStr.dhuhr,
                subName = if (isArabic) "Dhuhr" else "الظهر",
                time = prayerSchedule.dhuhr,
                icon = Icons.Default.Brightness7,
                isNext = prayerSchedule.nextPrayerName.equals("dhuhr", ignoreCase = true)
            )
            PrayerRowItem(
                name = AppStr.asr,
                subName = if (isArabic) "Asr" else "العصر",
                time = prayerSchedule.asr,
                icon = Icons.Default.Brightness6,
                isNext = prayerSchedule.nextPrayerName.equals("asr", ignoreCase = true)
            )
            PrayerRowItem(
                name = AppStr.maghrib,
                subName = if (isArabic) "Maghrib" else "المغرب",
                time = prayerSchedule.maghrib,
                icon = Icons.Default.Brightness5,
                isNext = prayerSchedule.nextPrayerName.equals("maghrib", ignoreCase = true)
            )
            PrayerRowItem(
                name = AppStr.isha,
                subName = if (isArabic) "Isha" else "العشاء",
                time = prayerSchedule.isha,
                icon = Icons.Default.NightsStay,
                isNext = prayerSchedule.nextPrayerName.equals("isha", ignoreCase = true)
            )
        }
    }
}

@Composable
private fun PrayerRowItem(
    name: String,
    subName: String,
    time: String,
    icon: ImageVector,
    isNext: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isNext) GeoContainer else GeoSurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isNext) GeoPrimary else GeoTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                    color = if (isNext) GeoPrimary else GeoTextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = subName,
                    fontSize = 13.sp,
                    color = GeoTextMuted
                )
                Text(
                    text = time,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNext) GeoPrimary else GeoTextPrimary
                )
            }
        }
    }
}
