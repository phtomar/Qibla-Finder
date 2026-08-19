package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticFeedbackHelper
import com.example.location.LocationHelper
import com.example.model.AppPreferences
import com.example.model.CalculationMethod
import com.example.model.CompassReading
import com.example.model.JuristicMethod
import com.example.model.LocationData
import com.example.model.PresetCity
import com.example.model.QiblaInfo
import com.example.model.QiblaUiState
import com.example.sensors.CompassSensorManager
import com.example.util.QiblaMath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = CompassSensorManager(application)
    private val locationHelper = LocationHelper(application)
    private val hapticHelper = HapticFeedbackHelper(application)

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var wasAlignedLastFrame = false
    private var lastAlignedTimeMs = 0L

    init {
        // Start listening to compass sensors
        observeSensors()

        // Set default location (Cairo or Mecca fallback or preset)
        val defaultCity = QiblaMath.PRESET_CITIES.firstOrNull { it.name == "Cairo" }
            ?: QiblaMath.PRESET_CITIES.firstOrNull { it.name == "Mecca" }
            ?: QiblaMath.PRESET_CITIES[0]
        selectCity(defaultCity)
    }

    private fun observeSensors() {
        viewModelScope.launch {
            sensorManager.getCompassOrientationFlow().collect { reading ->
                processSensorReading(reading)
            }
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
        refreshGpsLocation()
    }

    fun refreshGpsLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true) }
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                applyNewLocation(loc)
            } else {
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        statusMessage = "Could not fetch GPS. Using selected city."
                    )
                }
            }
        }
    }

    fun selectCity(city: PresetCity) {
        val declination = QiblaMath.getMagneticDeclination(city.latitude, city.longitude, 0.0)
        val loc = LocationData(
            latitude = city.latitude,
            longitude = city.longitude,
            altitude = 0.0,
            cityName = city.name,
            countryName = city.country,
            isGps = false,
            accuracyMeters = 0f,
            magneticDeclination = declination
        )
        applyNewLocation(loc)
    }

    fun setCustomCoordinates(lat: Double, lon: Double, name: String = "Custom Location") {
        val declination = QiblaMath.getMagneticDeclination(lat, lon, 0.0)
        val loc = LocationData(
            latitude = lat,
            longitude = lon,
            altitude = 0.0,
            cityName = name,
            countryName = "",
            isGps = false,
            accuracyMeters = 0f,
            magneticDeclination = declination
        )
        applyNewLocation(loc)
    }

    private fun applyNewLocation(location: LocationData) {
        val qiblaBearing = QiblaMath.calculateQiblaBearing(location.latitude, location.longitude)
        val distanceKm = QiblaMath.calculateDistanceKm(location.latitude, location.longitude)
        val distanceMiles = QiblaMath.kmToMiles(distanceKm)

        _uiState.update { state ->
            val updatedQibla = calculateQiblaState(
                currentHeading = state.compass.azimuth,
                qiblaBearing = qiblaBearing,
                distanceKm = distanceKm,
                distanceMiles = distanceMiles,
                useTrueNorth = state.preferences.useTrueNorth,
                declination = location.magneticDeclination
            )

            val updatedPreferences = if (state.preferences.autoDetectCalculationMethod) {
                val detected = CalculationMethod.detectBestMethod(location.countryName, location.cityName)
                state.preferences.copy(calculationMethod = detected)
            } else {
                state.preferences
            }

            state.copy(
                location = location,
                qibla = updatedQibla,
                preferences = updatedPreferences,
                isLocationLoading = false,
                statusMessage = if (location.isGps) "GPS Location: ${location.cityName}" else "Location: ${location.cityName}, ${location.countryName}"
            )
        }

        updatePrayerSchedule()
    }

    private fun processSensorReading(reading: CompassReading) {
        _uiState.update { state ->
            val updatedQibla = calculateQiblaState(
                currentHeading = reading.azimuth,
                qiblaBearing = state.qibla.qiblaBearing,
                distanceKm = state.qibla.distanceKm,
                distanceMiles = state.qibla.distanceMiles,
                useTrueNorth = state.preferences.useTrueNorth,
                declination = state.location.magneticDeclination
            )

            // Trigger feedback on alignment transition
            checkAlignmentFeedback(updatedQibla.isAligned, state.preferences)

            val statusMsg = when {
                !reading.isLevel -> "Hold phone flat on your palm for best precision"
                updatedQibla.isAligned -> "Aligned with Kaaba! (🕋 Qibla Found)"
                updatedQibla.relativeAngle > 3f -> "Turn ${abs(updatedQibla.relativeAngle).roundToInt()}° Right ➔"
                updatedQibla.relativeAngle < -3f -> "Turn ${abs(updatedQibla.relativeAngle).roundToInt()}° Left ⬅"
                else -> "Point phone towards the Kaaba"
            }

            state.copy(
                compass = reading,
                qibla = updatedQibla,
                statusMessage = statusMsg
            )
        }
    }

    private fun calculateQiblaState(
        currentHeading: Float,
        qiblaBearing: Double,
        distanceKm: Double,
        distanceMiles: Double,
        useTrueNorth: Boolean,
        declination: Float
    ): QiblaInfo {
        // Adjust heading if using True North
        val effectiveHeading = if (useTrueNorth) {
            QiblaMath.normalize360(currentHeading + declination)
        } else {
            currentHeading
        }

        // Relative angle between effective heading and true Qibla bearing
        val diff = QiblaMath.normalizeRelativeAngle((qiblaBearing.toFloat() - effectiveHeading))
        val isAligned = abs(diff) <= 3.0f

        // Alignment percentage (1.0 = exact 0 deg diff, 0.0 = 180 deg diff)
        val alignmentPercent = ((180f - abs(diff)) / 180f).coerceIn(0f, 1f)

        return QiblaInfo(
            qiblaBearing = qiblaBearing,
            distanceKm = distanceKm,
            distanceMiles = distanceMiles,
            relativeAngle = diff,
            isAligned = isAligned,
            alignmentPercent = alignmentPercent
        )
    }

    private fun checkAlignmentFeedback(isAligned: Boolean, preferences: AppPreferences) {
        val now = System.currentTimeMillis()
        if (isAligned && !wasAlignedLastFrame && (now - lastAlignedTimeMs > 1200)) {
            lastAlignedTimeMs = now
            if (preferences.hapticsEnabled) {
                hapticHelper.triggerAlignmentHaptic()
            }
            if (preferences.soundEnabled) {
                hapticHelper.triggerAlignmentTone()
            }
        }
        wasAlignedLastFrame = isAligned
    }

    private fun updatePrayerSchedule() {
        val state = _uiState.value
        val loc = state.location
        val schedule = QiblaMath.calculatePrayerTimes(
            latitude = loc.latitude,
            longitude = loc.longitude,
            calendar = Calendar.getInstance(),
            timeZone = TimeZone.getDefault(),
            method = state.preferences.calculationMethod,
            juristicMethod = state.preferences.juristicMethod
        )
        _uiState.update { it.copy(prayerSchedule = schedule) }
    }

    // UI Dialog & Preference actions
    fun toggleCalibrationDialog(show: Boolean) {
        _uiState.update { it.copy(showCalibrationDialog = show) }
    }

    fun toggleCitySelector(show: Boolean) {
        _uiState.update { it.copy(showCitySelector = show) }
    }

    fun togglePrayerTimes(show: Boolean) {
        _uiState.update { it.copy(showPrayerTimes = show) }
    }

    fun toggleSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _uiState.update {
            it.copy(
                preferences = it.preferences.copy(
                    calculationMethod = method,
                    autoDetectCalculationMethod = false
                )
            )
        }
        updatePrayerSchedule()
    }

    fun setAutoDetectCalculationMethod(autoDetect: Boolean) {
        _uiState.update { state ->
            val updatedMethod = if (autoDetect) {
                CalculationMethod.detectBestMethod(state.location.countryName, state.location.cityName)
            } else {
                state.preferences.calculationMethod
            }
            state.copy(
                preferences = state.preferences.copy(
                    autoDetectCalculationMethod = autoDetect,
                    calculationMethod = updatedMethod
                )
            )
        }
        updatePrayerSchedule()
    }

    fun setJuristicMethod(juristicMethod: JuristicMethod) {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(juristicMethod = juristicMethod))
        }
        updatePrayerSchedule()
    }

    fun setTrueNorth(enabled: Boolean) {
        _uiState.update { it.copy(preferences = it.preferences.copy(useTrueNorth = enabled)) }
        processSensorReading(_uiState.value.compass)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(preferences = it.preferences.copy(hapticsEnabled = enabled)) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(preferences = it.preferences.copy(soundEnabled = enabled)) }
    }

    fun setDialRotationMode(dialMode: Boolean) {
        _uiState.update { it.copy(preferences = it.preferences.copy(dialRotationMode = dialMode)) }
    }

    fun setUseKilometers(useKm: Boolean) {
        _uiState.update { it.copy(preferences = it.preferences.copy(useKilometers = useKm)) }
    }

    fun setTheme(themeId: com.example.model.AppThemeId) {
        _uiState.update { it.copy(preferences = it.preferences.copy(themeId = themeId)) }
    }

    fun setLanguage(language: com.example.model.AppLanguage) {
        _uiState.update { it.copy(preferences = it.preferences.copy(language = language)) }
    }

    fun toggleNearbyMosques(show: Boolean) {
        _uiState.update { it.copy(showNearbyMosques = show) }
    }

    override fun onCleared() {
        super.onCleared()
        hapticHelper.release()
    }
}
