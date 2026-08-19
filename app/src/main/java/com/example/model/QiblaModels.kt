package com.example.model

/**
 * Supported App Languages
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    ARABIC("ar", "Arabic", "العربية")
}

/**
 * Accuracy level of geomagnetic/rotation sensors.
 */
enum class SensorAccuracy(val level: Int, val label: String) {
    UNRELIABLE(0, "Unreliable"),
    LOW(1, "Low Accuracy"),
    MEDIUM(2, "Medium Accuracy"),
    HIGH(3, "High Accuracy");

    companion object {
        fun fromSensorAccuracy(accuracy: Int): SensorAccuracy = when (accuracy) {
            3 -> HIGH
            2 -> MEDIUM
            1 -> LOW
            else -> UNRELIABLE
        }
    }
}

/**
 * Real-time orientation angles from the device motion sensors.
 */
data class CompassReading(
    val azimuth: Float = 0f,       // 0 to 360 degrees from North
    val pitch: Float = 0f,         // Tilt front-back in degrees (-90 to 90)
    val roll: Float = 0f,          // Tilt left-right in degrees (-180 to 180)
    val accuracy: SensorAccuracy = SensorAccuracy.HIGH,
    val isLevel: Boolean = true    // True if phone is sufficiently flat (< 15 deg tilt)
)

/**
 * Geographic location information of the user.
 */
data class LocationData(
    val latitude: Double = 21.4225, // Default near Mecca if not located
    val longitude: Double = 39.8262,
    val altitude: Double = 0.0,
    val cityName: String = "Mecca",
    val countryName: String = "Saudi Arabia",
    val isGps: Boolean = false,
    val accuracyMeters: Float = 0f,
    val magneticDeclination: Float = 0f // Offset between True North and Magnetic North
)

/**
 * Calculated Qibla direction and distance.
 */
data class QiblaInfo(
    val qiblaBearing: Double = 0.0,      // True angle from North to Kaaba (0-360)
    val distanceKm: Double = 0.0,        // Distance to Kaaba in kilometers
    val distanceMiles: Double = 0.0,     // Distance to Kaaba in miles
    val relativeAngle: Float = 0f,       // Difference: (QiblaBearing - Heading) normalized to [-180, 180]
    val isAligned: Boolean = false,      // True if within ±3 degrees of Kaaba
    val alignmentPercent: Float = 0f     // 0f (180 deg away) to 1f (exact alignment)
)

/**
 * Official Calculation Authorities.
 */
enum class CalculationMethod(
    val displayName: String,
    val displayNameAr: String,
    val authorityName: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMinutes: Int? = null,
    val dhuhrBufferMinutes: Int = 1
) {
    EGYPTIAN(
        displayName = "Egyptian General Authority of Survey",
        displayNameAr = "الهيئة المصرية العامة للمساحة",
        authorityName = "الهيئة المصرية العامة للمساحة",
        fajrAngle = 19.5,
        ishaAngle = 17.5
    ),
    UMM_AL_QURA(
        displayName = "Umm al-Qura University, Makkah",
        displayNameAr = "جامعة أم القرى (مكة المكرمة)",
        authorityName = "جامعة أم القرى (السعودية والخليج)",
        fajrAngle = 18.5,
        ishaAngle = 0.0,
        ishaIntervalMinutes = 90
    ),
    MUSLIM_WORLD_LEAGUE(
        displayName = "Muslim World League (MWL)",
        displayNameAr = "رابطة العالم الإسلامي",
        authorityName = "رابطة العالم الإسلامي",
        fajrAngle = 18.0,
        ishaAngle = 17.0
    ),
    ISNA(
        displayName = "Islamic Society of North America (ISNA)",
        displayNameAr = "الجمعية الإسلامية لأمريكا الشمالية (ISNA)",
        authorityName = "ISNA (أمريكا الشمالية)",
        fajrAngle = 15.0,
        ishaAngle = 15.0
    ),
    KARACHI(
        displayName = "Univ. of Islamic Sciences, Karachi",
        displayNameAr = "جامعة العلوم الإسلامية بكراتشي",
        authorityName = "جامعة العلوم الإسلامية بكراتشي (باكستان، الهند، بنغلاديش)",
        fajrAngle = 18.0,
        ishaAngle = 18.0
    ),
    DUBAI(
        displayName = "Dubai Islamic Affairs (IACAD)",
        displayNameAr = "دائرة الشؤون الإسلامية بدبي (IACAD)",
        authorityName = "دائرة الشؤون الإسلامية والعمل الخيري بدبي (الإمارات)",
        fajrAngle = 18.2,
        ishaAngle = 0.0,
        ishaIntervalMinutes = 90
    ),
    DIYANET(
        displayName = "Diyanet (Turkey)",
        displayNameAr = "رئاسة الشؤون الدينية التركية (ديانت)",
        authorityName = "Diyanet İşleri Başkanlığı (تركيا)",
        fajrAngle = 18.0,
        ishaAngle = 17.0
    );

    companion object {
        fun detectBestMethod(country: String, cityName: String): CalculationMethod {
            val lowerCountry = country.lowercase()
            val lowerCity = cityName.lowercase()
            return when {
                lowerCountry.contains("egypt") || lowerCity.contains("cairo") || lowerCity.contains("alexandria") -> EGYPTIAN
                lowerCountry.contains("saudi") || lowerCity.contains("mecca") || lowerCity.contains("medina") || lowerCity.contains("riyadh") -> UMM_AL_QURA
                lowerCountry.contains("emirates") || lowerCity.contains("dubai") || lowerCity.contains("abu dhabi") -> DUBAI
                lowerCountry.contains("pakistan") || lowerCountry.contains("india") || lowerCountry.contains("bangladesh") || lowerCity.contains("karachi") -> KARACHI
                lowerCountry.contains("united states") || lowerCountry.contains("usa") || lowerCountry.contains("canada") -> ISNA
                lowerCountry.contains("turkey") || lowerCountry.contains("türkiye") || lowerCity.contains("istanbul") -> DIYANET
                else -> MUSLIM_WORLD_LEAGUE
            }
        }
    }
}

/**
 * Asr juristic calculation school.
 */
enum class JuristicMethod(val displayName: String, val shadowFactor: Double) {
    STANDARD("Standard (Shafi'i, Maliki, Hanbali)", 1.0),
    HANAFI("Hanafi (Shadow factor 2x)", 2.0)
}

/**
 * Available Visual Themes (3 Light + 3 Dark).
 */
enum class AppThemeId(
    val displayName: String,
    val displayNameAr: String,
    val subtitle: String,
    val subtitleAr: String,
    val isDark: Boolean
) {
    // 3 Light Themes
    GEOMETRIC_LIGHT(
        displayName = "Geometric Amethyst",
        displayNameAr = "الأرجواني الهندسي",
        subtitle = "Royal purple with serene alabaster canvas",
        subtitleAr = "بنفسجي ملكي مع خلفية مرمرية ناعمة",
        isDark = false
    ),
    EMERALD_LIGHT(
        displayName = "Emerald Oasis",
        displayNameAr = "واحة الزمرد",
        subtitle = "Sacred Islamic green with mint accents",
        subtitleAr = "أخضر إسلامي مقدس مع لمسات نعناعية",
        isDark = false
    ),
    SANDSTONE_LIGHT(
        displayName = "Desert Sandstone",
        displayNameAr = "حجر الصحراء الرملي",
        subtitle = "Warm terracotta & Makkah limestone",
        subtitleAr = "طين دافئ وحجر مكة المكرمة العريق",
        isDark = false
    ),

    // 3 Dark Themes
    MIDNIGHT_KAABA(
        displayName = "Midnight Kaaba",
        displayNameAr = "سواد الكعبة المشرفة",
        subtitle = "Deep obsidian with Kiswah gold accents",
        subtitleAr = "سواد ناصع مع ذهب كسوة الكعبة المشرفة",
        isDark = true
    ),
    ROYAL_CELESTIAL(
        displayName = "Royal Celestial",
        displayNameAr = "السماء الملكية",
        subtitle = "Deep cosmic indigo with luminous lilac",
        subtitleAr = "نيلي كوني داكن مع إضاءة ليلكية ساحرة",
        isDark = true
    ),
    ARABIAN_NIGHT(
        displayName = "Arabian Night",
        displayNameAr = "ليالي عربية",
        subtitle = "Malachite night with luminous mint jade",
        subtitleAr = "ليل ملاكيت عميق مع يشم نعناعي مضيء",
        isDark = true
    )
}

/**
 * Prayer times calculated based on location and date.
 */
data class PrayerSchedule(
    val fajr: String = "--:--",
    val sunrise: String = "--:--",
    val dhuhr: String = "--:--",
    val asr: String = "--:--",
    val maghrib: String = "--:--",
    val isha: String = "--:--",
    val nextPrayerName: String = "Fajr",
    val timeRemaining: String = "--:--",
    val calculationMethodName: String = CalculationMethod.EGYPTIAN.displayName
)

/**
 * Preset major cities for offline or manual selection.
 */
data class PresetCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Nearby Mosque representation
 */
data class NearbyMosque(
    val id: String,
    val name: String,
    val nameAr: String,
    val distanceKm: Double,
    val bearingDeg: Double,
    val address: String,
    val addressAr: String,
    val isJumaaMosque: Boolean = true
)

/**
 * User customization preferences.
 */
data class AppPreferences(
    val useTrueNorth: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val dialRotationMode: Boolean = true,
    val useKilometers: Boolean = true,
    val calculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    val autoDetectCalculationMethod: Boolean = true,
    val juristicMethod: JuristicMethod = JuristicMethod.STANDARD,
    val themeId: AppThemeId = AppThemeId.GEOMETRIC_LIGHT,
    val language: AppLanguage = AppLanguage.ENGLISH
)

/**
 * Combined UI State for the Qibla Finder screen.
 */
data class QiblaUiState(
    val location: LocationData = LocationData(),
    val compass: CompassReading = CompassReading(),
    val qibla: QiblaInfo = QiblaInfo(),
    val prayerSchedule: PrayerSchedule = PrayerSchedule(),
    val preferences: AppPreferences = AppPreferences(),
    val isLocationLoading: Boolean = false,
    val showCalibrationDialog: Boolean = false,
    val showCitySelector: Boolean = false,
    val showPrayerTimes: Boolean = false,
    val showSettings: Boolean = false,
    val showNearbyMosques: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val statusMessage: String = "Point your phone towards the Kaaba"
)
