package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.AppPreferences
import com.example.model.AppThemeId
import com.example.model.CalculationMethod
import com.example.model.JuristicMethod
import com.example.model.LocationData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TasbihSavedState(
    val currentCount: Int,
    val rounds: Int,
    val totalCount: Int,
    val lastDate: String
)

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadPreferences(): AppPreferences {
        val langCode = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
        val language = if (langCode == AppLanguage.ARABIC.code) AppLanguage.ARABIC else AppLanguage.ENGLISH

        val themeName = prefs.getString(KEY_THEME_ID, AppThemeId.SANDSTONE_LIGHT.name)
        val themeId = try {
            AppThemeId.valueOf(themeName ?: AppThemeId.SANDSTONE_LIGHT.name)
        } catch (e: Exception) {
            AppThemeId.SANDSTONE_LIGHT
        }

        val calcMethodName = prefs.getString(KEY_CALC_METHOD, CalculationMethod.EGYPTIAN.name)
        val calcMethod = try {
            CalculationMethod.valueOf(calcMethodName ?: CalculationMethod.EGYPTIAN.name)
        } catch (e: Exception) {
            CalculationMethod.EGYPTIAN
        }

        val juristicName = prefs.getString(KEY_JURISTIC_METHOD, JuristicMethod.STANDARD.name)
        val juristicMethod = try {
            JuristicMethod.valueOf(juristicName ?: JuristicMethod.STANDARD.name)
        } catch (e: Exception) {
            JuristicMethod.STANDARD
        }

        val hasChosenLanguage = prefs.getBoolean(KEY_HAS_CHOSEN_LANGUAGE, false)

        return AppPreferences(
            useTrueNorth = prefs.getBoolean(KEY_USE_TRUE_NORTH, true),
            hapticsEnabled = prefs.getBoolean(KEY_HAPTICS_ENABLED, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            dialRotationMode = prefs.getBoolean(KEY_DIAL_ROTATION_MODE, true),
            useKilometers = prefs.getBoolean(KEY_USE_KILOMETERS, true),
            use24HourFormat = prefs.getBoolean(KEY_USE_24_HOUR_FORMAT, false),
            calculationMethod = calcMethod,
            autoDetectCalculationMethod = prefs.getBoolean(KEY_AUTO_DETECT_CALC, true),
            juristicMethod = juristicMethod,
            themeId = themeId,
            language = language,
            hasChosenLanguage = hasChosenLanguage
        )
    }

    fun hasChosenLanguage(): Boolean = prefs.getBoolean(KEY_HAS_CHOSEN_LANGUAGE, false)

    fun setChosenLanguage(chosen: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_CHOSEN_LANGUAGE, chosen).apply()
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun saveTheme(themeId: AppThemeId) {
        prefs.edit().putString(KEY_THEME_ID, themeId.name).apply()
    }

    fun saveTrueNorth(useTrueNorth: Boolean) {
        prefs.edit().putBoolean(KEY_USE_TRUE_NORTH, useTrueNorth).apply()
    }

    fun saveHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
    }

    fun saveSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun saveDialRotationMode(dialRotationMode: Boolean) {
        prefs.edit().putBoolean(KEY_DIAL_ROTATION_MODE, dialRotationMode).apply()
    }

    fun saveUseKilometers(useKilometers: Boolean) {
        prefs.edit().putBoolean(KEY_USE_KILOMETERS, useKilometers).apply()
    }

    fun saveUse24HourFormat(use24Hour: Boolean) {
        prefs.edit().putBoolean(KEY_USE_24_HOUR_FORMAT, use24Hour).apply()
    }

    fun saveCalculationMethod(method: CalculationMethod, autoDetect: Boolean) {
        prefs.edit()
            .putString(KEY_CALC_METHOD, method.name)
            .putBoolean(KEY_AUTO_DETECT_CALC, autoDetect)
            .apply()
    }

    fun saveJuristicMethod(method: JuristicMethod) {
        prefs.edit().putString(KEY_JURISTIC_METHOD, method.name).apply()
    }

    fun saveLastLocation(location: LocationData) {
        prefs.edit()
            .putFloat(KEY_LOC_LAT, location.latitude.toFloat())
            .putFloat(KEY_LOC_LON, location.longitude.toFloat())
            .putString(KEY_LOC_CITY, location.cityName)
            .putString(KEY_LOC_COUNTRY, location.countryName)
            .putBoolean(KEY_LOC_IS_GPS, location.isGps)
            .putFloat(KEY_LOC_DECLINATION, location.magneticDeclination)
            .apply()
    }

    fun loadLastLocation(): LocationData? {
        if (!prefs.contains(KEY_LOC_LAT) || !prefs.contains(KEY_LOC_LON)) {
            return null
        }
        val lat = prefs.getFloat(KEY_LOC_LAT, 21.4225f).toDouble()
        val lon = prefs.getFloat(KEY_LOC_LON, 39.8262f).toDouble()
        val city = prefs.getString(KEY_LOC_CITY, "Cairo") ?: "Cairo"
        val country = prefs.getString(KEY_LOC_COUNTRY, "Egypt") ?: "Egypt"
        val isGps = prefs.getBoolean(KEY_LOC_IS_GPS, false)
        val declination = prefs.getFloat(KEY_LOC_DECLINATION, 0f)

        return LocationData(
            latitude = lat,
            longitude = lon,
            cityName = city,
            countryName = country,
            isGps = isGps,
            magneticDeclination = declination
        )
    }

    // ==========================================
    // TASBIH PERSISTENCE & DAILY AUTO-RESET
    // ==========================================

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun loadTasbihState(): TasbihSavedState {
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_TASBIH_DATE, "") ?: ""
        val total = prefs.getInt(KEY_TASBIH_TOTAL, 0)

        // If day changed, daily count & rounds auto-reset to 0 while preserving total count
        return if (lastDate.isNotEmpty() && lastDate != today) {
            saveTasbihState(0, 0, total, today)
            TasbihSavedState(
                currentCount = 0,
                rounds = 0,
                totalCount = total,
                lastDate = today
            )
        } else {
            TasbihSavedState(
                currentCount = prefs.getInt(KEY_TASBIH_COUNT, 0),
                rounds = prefs.getInt(KEY_TASBIH_ROUNDS, 0),
                totalCount = total,
                lastDate = if (lastDate.isEmpty()) today else lastDate
            )
        }
    }

    fun saveTasbihState(count: Int, rounds: Int, total: Int, date: String = getTodayDateString()) {
        prefs.edit()
            .putInt(KEY_TASBIH_COUNT, count)
            .putInt(KEY_TASBIH_ROUNDS, rounds)
            .putInt(KEY_TASBIH_TOTAL, total)
            .putString(KEY_TASBIH_DATE, date)
            .apply()
    }

    fun resetTasbihAll() {
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TASBIH_COUNT, 0)
            .putInt(KEY_TASBIH_ROUNDS, 0)
            .putInt(KEY_TASBIH_TOTAL, 0)
            .putString(KEY_TASBIH_DATE, today)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "qibla_compass_preferences"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_THEME_ID = "key_theme_id"
        private const val KEY_USE_TRUE_NORTH = "key_use_true_north"
        private const val KEY_HAPTICS_ENABLED = "key_haptics_enabled"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_DIAL_ROTATION_MODE = "key_dial_rotation_mode"
        private const val KEY_USE_KILOMETERS = "key_use_kilometers"
        private const val KEY_USE_24_HOUR_FORMAT = "key_use_24_hour_format"
        private const val KEY_CALC_METHOD = "key_calc_method"
        private const val KEY_AUTO_DETECT_CALC = "key_auto_detect_calc"
        private const val KEY_JURISTIC_METHOD = "key_juristic_method"
        private const val KEY_HAS_CHOSEN_LANGUAGE = "key_has_chosen_language"

        private const val KEY_LOC_LAT = "key_loc_lat"
        private const val KEY_LOC_LON = "key_loc_lon"
        private const val KEY_LOC_CITY = "key_loc_city"
        private const val KEY_LOC_COUNTRY = "key_loc_country"
        private const val KEY_LOC_IS_GPS = "key_loc_is_gps"
        private const val KEY_LOC_DECLINATION = "key_loc_declination"

        private const val KEY_TASBIH_COUNT = "key_tasbih_count"
        private const val KEY_TASBIH_ROUNDS = "key_tasbih_rounds"
        private const val KEY_TASBIH_TOTAL = "key_tasbih_total"
        private const val KEY_TASBIH_DATE = "key_tasbih_date"
    }
}
