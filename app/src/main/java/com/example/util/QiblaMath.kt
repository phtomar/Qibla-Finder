package com.example.util

import android.hardware.GeomagneticField
import com.example.model.PrayerSchedule
import com.example.model.PresetCity
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object QiblaMath {
    // Exact geographic coordinates of the Holy Kaaba in Mecca, Saudi Arabia
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0088
    private const val KM_TO_MILES = 0.621371

    /**
     * Calculates the forward azimuth (Qibla bearing) from the given location to the Kaaba.
     * Returns the bearing in degrees (0.0 to 360.0), where 0 is True North.
     */
    fun calculateQiblaBearing(userLat: Double, userLon: Double): Double {
        val lat1Rad = Math.toRadians(userLat)
        val lat2Rad = Math.toRadians(KAABA_LATITUDE)
        val deltaLonRad = Math.toRadians(KAABA_LONGITUDE - userLon)

        val y = sin(deltaLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)

        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)

        return (bearingDeg + 360.0) % 360.0
    }

    /**
     * Calculates the Great Circle distance from the user to the Kaaba in kilometers.
     */
    fun calculateDistanceKm(userLat: Double, userLon: Double): Double {
        val lat1Rad = Math.toRadians(userLat)
        val lat2Rad = Math.toRadians(KAABA_LATITUDE)
        val deltaLatRad = Math.toRadians(KAABA_LATITUDE - userLat)
        val deltaLonRad = Math.toRadians(KAABA_LONGITUDE - userLon)

        val a = sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2) * sin(deltaLonRad / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Converts kilometers to miles.
     */
    fun kmToMiles(km: Double): Double = km * KM_TO_MILES

    /**
     * Calculates the Magnetic Declination (the angle between Magnetic North and True North)
     * using the World Magnetic Model (WMM) built into Android.
     */
    fun getMagneticDeclination(lat: Double, lon: Double, altMeters: Double, timeMs: Long = System.currentTimeMillis()): Float {
        return try {
            val field = GeomagneticField(
                lat.toFloat(),
                lon.toFloat(),
                altMeters.toFloat(),
                timeMs
            )
            field.declination
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * Normalizes an angle into the range [-180, 180] degrees.
     */
    fun normalizeRelativeAngle(angle: Float): Float {
        var a = (angle + 180f) % 360f
        if (a < 0f) a += 360f
        return a - 180f
    }

    /**
     * Normalizes an angle into the standard range [0, 360) degrees.
     */
    fun normalize360(angle: Float): Float {
        var a = angle % 360f
        if (a < 0f) a += 360f
        return a
    }

    /**
     * Returns human-readable cardinal direction (e.g. N, NNE, NE, E, etc.).
     */
    fun getCardinalDirection(degrees: Double): String {
        val normalized = (degrees % 360 + 360) % 360
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW"
        )
        val index = ((normalized + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    /**
     * Returns Arabic cardinal label for the direction.
     */
    fun getArabicCardinal(cardinal: String): String {
        return when (cardinal.firstOrNull()) {
            'N' -> "الشمال"
            'S' -> "الجنوب"
            'E' -> "الشرق"
            'W' -> "الغرب"
            else -> ""
        }
    }

    /**
     * Calculates astronomical prayer times for the given coordinates and date using
     * official authorities (e.g. Egyptian General Authority of Survey as on Google Search in Egypt).
     */
    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance(),
        timeZone: TimeZone = TimeZone.getDefault(),
        method: com.example.model.CalculationMethod = com.example.model.CalculationMethod.EGYPTIAN,
        juristicMethod: com.example.model.JuristicMethod = com.example.model.JuristicMethod.STANDARD,
        is24Hour: Boolean = false,
        isArabic: Boolean = false
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val julianDate = getJulianDate(year, month, day)
        val d = julianDate - 2451545.0

        // Mean anomaly of the Sun
        val g = Math.toRadians((357.529 + 0.98560028 * d) % 360.0)
        // Mean longitude of the Sun
        val q = (280.459 + 0.98564736 * d) % 360.0
        // Geocentric apparent ecliptic longitude of the Sun
        val l = Math.toRadians((q + 1.915 * sin(g) + 0.020 * sin(2 * g)) % 360.0)

        // Mean obliquity of the ecliptic
        val e = Math.toRadians(23.439 - 0.00000036 * d)

        // Declination of the Sun
        val declination = asin(sin(e) * sin(l))

        // Equation of time (in hours)
        val ra = atan2(cos(e) * sin(l), cos(l)) / 15.0
        val eqTime = q / 15.0 - Math.toDegrees(ra)

        // Solar Noon in UTC hours
        val noonUtc = 12.0 - eqTime - (longitude / 15.0)

        // TimeZone offset in hours
        val tzOffsetHours = timeZone.getOffset(calendar.timeInMillis).toDouble() / (1000.0 * 60.0 * 60.0)
        val solarNoon = noonUtc + tzOffsetHours

        val latRad = Math.toRadians(latitude)

        // Calculate hours based on method parameters
        val fajrHour = computeSunHour(solarNoon, latRad, declination, method.fajrAngle, true)
        val sunriseHour = computeSunHour(solarNoon, latRad, declination, 0.833, true)
        val dhuhrHour = solarNoon + (method.dhuhrBufferMinutes / 60.0)
        val asrHour = computeAsrHour(solarNoon, latRad, declination, shadowFactor = juristicMethod.shadowFactor)
        val maghribHour = computeSunHour(solarNoon, latRad, declination, 0.833, false)
        val ishaHour = if (method.ishaIntervalMinutes != null) {
            maghribHour + (method.ishaIntervalMinutes / 60.0)
        } else {
            computeSunHour(solarNoon, latRad, declination, method.ishaAngle, false)
        }

        val fajrStr = formatTimeHour(fajrHour, is24Hour, isArabic)
        val sunriseStr = formatTimeHour(sunriseHour, is24Hour, isArabic)
        val dhuhrStr = formatTimeHour(dhuhrHour, is24Hour, isArabic)
        val asrStr = formatTimeHour(asrHour, is24Hour, isArabic)
        val maghribStr = formatTimeHour(maghribHour, is24Hour, isArabic)
        val ishaStr = formatTimeHour(ishaHour, is24Hour, isArabic)

        // Determine next prayer and time remaining
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0 + calendar.get(Calendar.SECOND) / 3600.0

        val (nextPrayer, diffHours) = when {
            currentHour < fajrHour -> "Fajr" to (fajrHour - currentHour)
            currentHour < sunriseHour -> "Sunrise" to (sunriseHour - currentHour)
            currentHour < dhuhrHour -> "Dhuhr" to (dhuhrHour - currentHour)
            currentHour < asrHour -> "Asr" to (asrHour - currentHour)
            currentHour < maghribHour -> "Maghrib" to (maghribHour - currentHour)
            currentHour < ishaHour -> "Isha" to (ishaHour - currentHour)
            else -> "Fajr" to (24.0 - currentHour + fajrHour)
        }

        val totalMinutes = (diffHours * 60).roundToInt().coerceAtLeast(0)
        val remHours = totalMinutes / 60
        val remMins = totalMinutes % 60
        val timeRemainingStr = if (remHours > 0) "${remHours}h ${remMins}m" else "${remMins}m"

        return PrayerSchedule(
            fajr = fajrStr,
            sunrise = sunriseStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            nextPrayerName = nextPrayer,
            timeRemaining = timeRemainingStr,
            calculationMethodName = method.displayName
        )
    }

    private fun getJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun computeSunHour(noon: Double, latRad: Double, declination: Double, angleDeg: Double, isMorning: Boolean): Double {
        val angleRad = Math.toRadians(angleDeg)
        val cosHourAngle = (-sin(angleRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        if (cosHourAngle > 1.0 || cosHourAngle < -1.0) {
            return if (isMorning) noon - 6.0 else noon + 6.0 // Approximate polar fallback
        }
        val hourAngle = Math.toDegrees(acos(cosHourAngle)) / 15.0
        return if (isMorning) noon - hourAngle else noon + hourAngle
    }

    private fun computeAsrHour(noon: Double, latRad: Double, declination: Double, shadowFactor: Double): Double {
        // Sun altitude angle above horizon in radians: arccot(shadowFactor + tan(|lat - dec|))
        val altitudeRad = atan(1.0 / (shadowFactor + tan(abs(latRad - declination))))
        val cosHourAngle = (sin(altitudeRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        if (cosHourAngle > 1.0 || cosHourAngle < -1.0) {
            return noon + 3.5
        }
        val hourAngle = Math.toDegrees(acos(cosHourAngle.coerceIn(-1.0, 1.0))) / 15.0
        return noon + hourAngle
    }

    fun formatTimeHour(time: Double, is24Hour: Boolean = false, isArabic: Boolean = false): String {
        var normalized = time % 24.0
        if (normalized < 0) normalized += 24.0
        val totalMinutes = (normalized * 60).roundToInt()
        val hours24 = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60

        return if (is24Hour) {
            String.format(Locale.US, "%02d:%02d", hours24, minutes)
        } else {
            val hours12 = when {
                hours24 == 0 -> 12
                hours24 > 12 -> hours24 - 12
                else -> hours24
            }
            val amPm = if (hours24 < 12) {
                if (isArabic) "ص" else "AM"
            } else {
                if (isArabic) "م" else "PM"
            }
            String.format(Locale.US, "%02d:%02d %s", hours12, minutes, amPm)
        }
    }

    /**
     * Curated list of major world cities with accurate coordinates.
     */
    val PRESET_CITIES = listOf(
        PresetCity("Mecca", "Saudi Arabia", 21.4225, 39.8262),
        PresetCity("Medina", "Saudi Arabia", 24.5247, 39.5692),
        PresetCity("Riyadh", "Saudi Arabia", 24.7136, 46.6753),
        PresetCity("Dubai", "United Arab Emirates", 25.2048, 55.2708),
        PresetCity("Abu Dhabi", "United Arab Emirates", 24.4539, 54.3773),
        PresetCity("Doha", "Qatar", 25.2854, 51.5310),
        PresetCity("Kuwait City", "Kuwait", 29.3759, 47.9774),
        PresetCity("Muscat", "Oman", 23.5880, 58.3829),
        PresetCity("Manama", "Bahrain", 26.2285, 50.5860),
        PresetCity("Amman", "Jordan", 31.9454, 35.9284),
        PresetCity("Jerusalem", "Palestine", 31.7683, 35.2137),
        PresetCity("Cairo", "Egypt", 30.0444, 31.2357),
        PresetCity("Alexandria", "Egypt", 31.2001, 29.9187),
        PresetCity("Istanbul", "Turkey", 41.0082, 28.9784),
        PresetCity("Ankara", "Turkey", 39.9334, 32.8597),
        PresetCity("Tehran", "Iran", 35.6892, 51.3890),
        PresetCity("Baghdad", "Iraq", 33.3152, 44.3661),
        PresetCity("Karachi", "Pakistan", 24.8607, 67.0011),
        PresetCity("Lahore", "Pakistan", 31.5204, 74.3587),
        PresetCity("Islamabad", "Pakistan", 33.6844, 73.0479),
        PresetCity("Dhaka", "Bangladesh", 23.8103, 90.4125),
        PresetCity("Mumbai", "India", 19.0760, 72.8777),
        PresetCity("New Delhi", "India", 28.6139, 77.2090),
        PresetCity("Hyderabad", "India", 17.3850, 78.4867),
        PresetCity("Jakarta", "Indonesia", -6.2088, 106.8456),
        PresetCity("Surabaya", "Indonesia", -7.2575, 112.7521),
        PresetCity("Bandung", "Indonesia", -6.9175, 107.6191),
        PresetCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869),
        PresetCity("Singapore", "Singapore", 1.3521, 103.8198),
        PresetCity("London", "United Kingdom", 51.5074, -0.1278),
        PresetCity("Paris", "France", 48.8566, 2.3522),
        PresetCity("Berlin", "Germany", 52.5200, 13.4050),
        PresetCity("Rome", "Italy", 41.9028, 12.4964),
        PresetCity("Madrid", "Spain", 40.4168, -3.7038),
        PresetCity("Moscow", "Russia", 55.7558, 37.6173),
        PresetCity("New York", "United States", 40.7128, -74.0060),
        PresetCity("Los Angeles", "United States", 34.0522, -118.2437),
        PresetCity("Chicago", "United States", 41.8781, -87.6298),
        PresetCity("Houston", "United States", 29.7604, -95.3698),
        PresetCity("Toronto", "Canada", 43.6532, -79.3832),
        PresetCity("Montreal", "Canada", 45.5017, -73.5673),
        PresetCity("Sydney", "Australia", -33.8688, 151.2093),
        PresetCity("Melbourne", "Australia", -37.8136, 144.9631),
        PresetCity("Tokyo", "Japan", 35.6762, 139.6503),
        PresetCity("Beijing", "China", 39.9042, 116.4074),
        PresetCity("Casablanca", "Morocco", 33.5731, -7.5898),
        PresetCity("Algiers", "Algeria", 36.7538, 3.0588),
        PresetCity("Tunis", "Tunisia", 36.8065, 10.1815),
        PresetCity("Johannesburg", "South Africa", -26.2041, 28.0473),
        PresetCity("Cape Town", "South Africa", -33.9249, 18.4241)
    )
}
