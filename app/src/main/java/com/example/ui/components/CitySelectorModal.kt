package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PresetCity
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoBorderSubtle
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.util.QiblaMath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectorModal(
    currentCityName: String,
    isLoadingGps: Boolean,
    onUseGps: () -> Unit,
    onSelectCity: (PresetCity) -> Unit,
    onCustomCoordinates: (Double, Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var customLat by remember { mutableStateOf("") }
    var customLon by remember { mutableStateOf("") }
    var customLabel by remember { mutableStateOf("") }
    var customError by remember { mutableStateOf<String?>(null) }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            QiblaMath.PRESET_CITIES
        } else {
            QiblaMath.PRESET_CITIES.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.country.contains(searchQuery, ignoreCase = true)
            }
        }
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
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("city_selector_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStr.selectCityTitle,
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

            Spacer(modifier = Modifier.height(10.dp))

            // GPS Auto-detect button
            Button(
                onClick = {
                    onUseGps()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("gps_auto_detect_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (isLoadingGps) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Acquiring GPS...",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStr.useCurrentGps,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GeoSurfaceVariant,
                contentColor = GeoPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GeoPrimary
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = AppStr.selectCityTitle,
                            color = if (selectedTab == 0) GeoPrimary else GeoTextSecondary,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = AppStr.customCoordinates,
                            color = if (selectedTab == 1) GeoPrimary else GeoTextSecondary,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("city_search_input"),
                    placeholder = { Text(AppStr.searchCitiesPlaceholder, color = GeoTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GeoTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = GeoTextSecondary
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoBorderSubtle,
                        focusedContainerColor = GeoSurfaceVariant,
                        unfocusedContainerColor = GeoSurfaceVariant,
                        focusedTextColor = GeoTextPrimary,
                        unfocusedTextColor = GeoTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // List of preset cities
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredCities) { city ->
                        val isSelected = city.name.equals(currentCityName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) GeoContainer else GeoSurfaceVariant)
                                .clickable {
                                    onSelectCity(city)
                                    onDismiss()
                                }
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
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = if (isSelected) GeoPrimary else GeoTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = city.name,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GeoPrimary else GeoTextPrimary
                                        )
                                        Text(
                                            text = city.country,
                                            fontSize = 12.sp,
                                            color = GeoTextSecondary
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(GeoPrimary)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Custom Coordinates Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Enter coordinates in decimal degrees (e.g. 51.5074, -0.1278).",
                        fontSize = 13.sp,
                        color = GeoTextSecondary
                    )

                    OutlinedTextField(
                        value = customLabel,
                        onValueChange = { customLabel = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Location Name", color = GeoTextSecondary) },
                        placeholder = { Text("e.g. My Mosque / Home", color = GeoTextMuted) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            unfocusedBorderColor = GeoBorderSubtle,
                            focusedContainerColor = GeoSurfaceVariant,
                            unfocusedContainerColor = GeoSurfaceVariant,
                            focusedTextColor = GeoTextPrimary,
                            unfocusedTextColor = GeoTextPrimary
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customLat,
                            onValueChange = { customLat = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Latitude", color = GeoTextSecondary) },
                            placeholder = { Text("21.4225", color = GeoTextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorderSubtle,
                                focusedContainerColor = GeoSurfaceVariant,
                                unfocusedContainerColor = GeoSurfaceVariant,
                                focusedTextColor = GeoTextPrimary,
                                unfocusedTextColor = GeoTextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customLon,
                            onValueChange = { customLon = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Longitude", color = GeoTextSecondary) },
                            placeholder = { Text("39.8262", color = GeoTextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorderSubtle,
                                focusedContainerColor = GeoSurfaceVariant,
                                unfocusedContainerColor = GeoSurfaceVariant,
                                focusedTextColor = GeoTextPrimary,
                                unfocusedTextColor = GeoTextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    if (customError != null) {
                        Text(
                            text = customError ?: "",
                            color = GeoError,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val latVal = customLat.toDoubleOrNull()
                            val lonVal = customLon.toDoubleOrNull()
                            if (latVal == null || latVal < -90.0 || latVal > 90.0) {
                                customError = "Latitude must be between -90 and 90."
                                return@Button
                            }
                            if (lonVal == null || lonVal < -180.0 || lonVal > 180.0) {
                                customError = "Longitude must be between -180 and 180."
                                return@Button
                            }
                            customError = null
                            val name = if (customLabel.isBlank()) "Custom Location" else customLabel.trim()
                            onCustomCoordinates(latVal, lonVal, name)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLocation,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apply Coordinates",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
