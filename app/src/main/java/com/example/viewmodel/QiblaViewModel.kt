package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticFeedbackHelper
import com.example.data.PreferencesManager
import com.example.location.LocationHelper
import com.example.model.AppLanguage
import com.example.model.AppPreferences
import com.example.model.AppThemeId
import com.example.model.CalculationMethod
import com.example.model.CompassReading
import com.example.model.JuristicMethod
import com.example.model.LocationData
import com.example.model.PresetCity
import com.example.model.QiblaInfo
import com.example.model.QiblaUiState
import com.example.sensors.CompassSensorManager
import com.example.util.QiblaMath
import kotlinx.coroutines.Job
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

    private val preferencesManager = PreferencesManager(application)
    private val sensorManager = CompassSensorManager(application)
    private val locationHelper = LocationHelper(application)
    private val hapticHelper = HapticFeedbackHelper(application)

    private val initialPreferences = preferencesManager.loadPreferences()
    private val _uiState = MutableStateFlow(QiblaUiState(preferences = initialPreferences))
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var sensorJob: Job? = null
    private var wasAlignedLastFrame = false
    private var lastAlignedTimeMs = 0L
    private var hasAutoPromptedCalibration = false

    init {
        // Start listening to compass sensors initially
        startSensors()

        // Check if user has selected language on first install
        if (!preferencesManager.hasChosenLanguage()) {
            _uiState.update { it.copy(showLanguagePrompt = true) }
        }

        // Load saved location or fallback to Cairo / Mecca
        val savedLocation = preferencesManager.loadLastLocation()
        if (savedLocation != null) {
            applyNewLocation(savedLocation)
        } else {
            val defaultCity = QiblaMath.PRESET_CITIES.firstOrNull { it.name == "Cairo" }
                ?: QiblaMath.PRESET_CITIES.firstOrNull { it.name == "Mecca" }
                ?: QiblaMath.PRESET_CITIES[0]
            selectCity(defaultCity)
        }
    }

    /**
     * Resumes compass sensor sampling when the app is active in foreground.
     */
    fun startSensors() {
        if (sensorJob?.isActive == true) return
        sensorJob = viewModelScope.launch {
            sensorManager.getCompassOrientationFlow().collect { reading ->
                processSensorReading(reading)
            }
        }
    }

    /**
     * Pauses sensor sampling and detaches hardware listeners when app is in background,
     * completely eliminating background beeps and saving battery.
     */
    fun stopSensors() {
        sensorJob?.cancel()
        sensorJob = null
        wasAlignedLastFrame = false
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
        preferencesManager.saveLastLocation(location)

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

            // Auto-prompt calibration popup if sensor precision is low/unreliable
            val shouldAutoPromptCalibration = !hasAutoPromptedCalibration &&
                (reading.accuracy == com.example.model.SensorAccuracy.UNRELIABLE || reading.accuracy == com.example.model.SensorAccuracy.LOW)

            if (shouldAutoPromptCalibration) {
                hasAutoPromptedCalibration = true
            }

            val statusMsg = when {
                !reading.isLevel -> "Hold phone flat"
                updatedQibla.isAligned -> "Aligned with Kaaba! 🕋"
                updatedQibla.relativeAngle > 3f -> "Turn Right ➔"
                updatedQibla.relativeAngle < -3f -> "Turn Left ⬅"
                else -> "Point to Kaaba"
            }

            state.copy(
                compass = reading,
                qibla = updatedQibla,
                showCalibrationDialog = if (shouldAutoPromptCalibration) true else state.showCalibrationDialog,
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
        val isArabic = state.preferences.language == AppLanguage.ARABIC
        val schedule = QiblaMath.calculatePrayerTimes(
            latitude = loc.latitude,
            longitude = loc.longitude,
            calendar = Calendar.getInstance(),
            timeZone = TimeZone.getDefault(),
            method = state.preferences.calculationMethod,
            juristicMethod = state.preferences.juristicMethod,
            is24Hour = state.preferences.use24HourFormat,
            isArabic = isArabic
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

    fun toggleTasbih(show: Boolean) {
        _uiState.update { it.copy(showTasbih = show) }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        preferencesManager.saveCalculationMethod(method, autoDetect = false)
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
        val currentLoc = _uiState.value.location
        val updatedMethod = if (autoDetect) {
            CalculationMethod.detectBestMethod(currentLoc.countryName, currentLoc.cityName)
        } else {
            _uiState.value.preferences.calculationMethod
        }
        preferencesManager.saveCalculationMethod(updatedMethod, autoDetect = autoDetect)
        _uiState.update { state ->
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
        preferencesManager.saveJuristicMethod(juristicMethod)
        _uiState.update {
            it.copy(preferences = it.preferences.copy(juristicMethod = juristicMethod))
        }
        updatePrayerSchedule()
    }

    fun setTrueNorth(enabled: Boolean) {
        preferencesManager.saveTrueNorth(enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(useTrueNorth = enabled)) }
        processSensorReading(_uiState.value.compass)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        preferencesManager.saveHapticsEnabled(enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(hapticsEnabled = enabled)) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferencesManager.saveSoundEnabled(enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(soundEnabled = enabled)) }
    }

    fun setDialRotationMode(dialMode: Boolean) {
        preferencesManager.saveDialRotationMode(dialMode)
        _uiState.update { it.copy(preferences = it.preferences.copy(dialRotationMode = dialMode)) }
    }

    fun setUseKilometers(useKm: Boolean) {
        preferencesManager.saveUseKilometers(useKm)
        _uiState.update { it.copy(preferences = it.preferences.copy(useKilometers = useKm)) }
    }

    fun setUse24HourFormat(use24Hour: Boolean) {
        preferencesManager.saveUse24HourFormat(use24Hour)
        _uiState.update { it.copy(preferences = it.preferences.copy(use24HourFormat = use24Hour)) }
        updatePrayerSchedule()
    }

    fun setTheme(themeId: AppThemeId) {
        preferencesManager.saveTheme(themeId)
        _uiState.update { it.copy(preferences = it.preferences.copy(themeId = themeId)) }
    }

    fun setLanguage(language: AppLanguage) {
        preferencesManager.saveLanguage(language)
        _uiState.update { it.copy(preferences = it.preferences.copy(language = language)) }
        updatePrayerSchedule()
    }

    fun selectInitialLanguage(language: AppLanguage) {
        preferencesManager.saveLanguage(language)
        preferencesManager.setChosenLanguage(true)
        _uiState.update {
            it.copy(
                preferences = it.preferences.copy(language = language, hasChosenLanguage = true),
                showLanguagePrompt = false
            )
        }
        updatePrayerSchedule()
    }

    fun closeLanguagePrompt() {
        preferencesManager.setChosenLanguage(true)
        _uiState.update { it.copy(showLanguagePrompt = false) }
    }

    fun toggleNearbyMosques(show: Boolean) {
        _uiState.update { it.copy(showNearbyMosques = show) }
    }

    override fun onCleared() {
        super.onCleared()
        stopSensors()
        hapticHelper.release()
    }
}
