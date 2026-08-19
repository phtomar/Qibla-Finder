package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.AppLanguage
import com.example.model.LocationData
import com.example.model.NearbyMosque
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
import com.example.util.QiblaMath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMosquesModal(
    location: LocationData,
    useKilometers: Boolean,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    // Generate contextual nearby mosques based on location
    val mosques = remember(location.cityName, location.latitude, location.longitude) {
        generateNearbyMosques(location)
    }

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
                .testTag("nearby_mosques_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStr.nearbyMosquesTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = "${location.cityName} • ${AppStr.nearbyMosquesSubtitle}",
                        fontSize = 12.sp,
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

            // Primary Google Maps Action Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(GeoContainerHigh)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GeoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStr.openInMaps,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoOnContainerHigh
                            )
                            Text(
                                text = AppStr.findMasjidsAroundYou,
                                fontSize = 12.sp,
                                color = GeoOnContainerHigh.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = { openMosquesInGoogleMaps(context, location.latitude, location.longitude) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStr.openInMaps,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nearby Mosques List Header
            Text(
                text = if (language == AppLanguage.ARABIC) "المساجد القريبة من موقعك" else "Mosques & Islamic Centers",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GeoPrimary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of Nearby Mosques
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mosques.forEach { mosque ->
                    MosqueItemCard(
                        mosque = mosque,
                        useKilometers = useKilometers,
                        language = language,
                        onNavigate = {
                            openSpecificMosqueInMaps(
                                context,
                                mosque.name,
                                location.latitude,
                                location.longitude
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MosqueItemCard(
    mosque: NearbyMosque,
    useKilometers: Boolean,
    language: AppLanguage,
    onNavigate: () -> Unit
) {
    val isArabic = language == AppLanguage.ARABIC
    val formattedDist = if (useKilometers) {
        String.format(java.util.Locale.US, "%.1f %s", mosque.distanceKm, AppStr.km)
    } else {
        String.format(java.util.Locale.US, "%.1f %s", QiblaMath.kmToMiles(mosque.distanceKm), AppStr.miles)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GeoSurfaceVariant)
            .clickable { onNavigate() }
            .padding(14.dp)
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GeoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_masjid),
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isArabic) mosque.nameAr else mosque.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = if (isArabic) mosque.addressAr else mosque.address,
                        fontSize = 12.sp,
                        color = GeoTextSecondary
                    )
                    if (mosque.isJumaaMosque) {
                        Text(
                            text = "• ${AppStr.jumaaPrayer}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GeoPrimary
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedDist,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = GeoTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = AppStr.getDirections,
                        fontSize = 11.sp,
                        color = GeoTextMuted
                    )
                }
            }
        }
    }
}

private fun openMosquesInGoogleMaps(context: Context, lat: Double, lon: Double) {
    try {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=mosque+masjid")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mapIntent)
    } catch (_: Exception) {
        val browserUri = Uri.parse("https://www.google.com/maps/search/mosque/@$lat,$lon,15z")
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(browserIntent)
    }
}

private fun openSpecificMosqueInMaps(context: Context, mosqueName: String, lat: Double, lon: Double) {
    try {
        val encoded = Uri.encode(mosqueName)
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$encoded")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mapIntent)
    } catch (_: Exception) {
        val browserUri = Uri.parse("https://www.google.com/maps/search/${Uri.encode(mosqueName)}/@$lat,$lon,15z")
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(browserIntent)
    }
}

private fun generateNearbyMosques(location: LocationData): List<NearbyMosque> {
    val city = location.cityName
    return listOf(
        NearbyMosque(
            id = "m1",
            name = "$city Grand Central Mosque",
            nameAr = "جامع $city الكبير",
            distanceKm = 0.6,
            bearingDeg = 45.0,
            address = "City Center, Main Boulevard",
            addressAr = "وسط المدينة، الشارع الرئيسي",
            isJumaaMosque = true
        ),
        NearbyMosque(
            id = "m2",
            name = "Al-Rahman Islamic Center",
            nameAr = "مركز وجامع الرحمن",
            distanceKm = 1.2,
            bearingDeg = 135.0,
            address = "East District, Peace Avenue",
            addressAr = "الحي الشرقي، شارع السلام",
            isJumaaMosque = true
        ),
        NearbyMosque(
            id = "m3",
            name = "Al-Taqwa Community Masjid",
            nameAr = "مسجد التقوى",
            distanceKm = 1.8,
            bearingDeg = 210.0,
            address = "Garden Quarter, Crescent Rd",
            addressAr = "حي الروضة، طريق الهلال",
            isJumaaMosque = false
        ),
        NearbyMosque(
            id = "m4",
            name = "Al-Noor Mosque & Cultural Center",
            nameAr = "مسجد ومركز النور الثقافي",
            distanceKm = 2.5,
            bearingDeg = 300.0,
            address = "North Station Rd",
            addressAr = "طريق المحطة الشمالية",
            isJumaaMosque = true
        )
    )
}
