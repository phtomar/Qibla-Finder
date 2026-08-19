package com.example.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.model.AppLanguage

data class AppStrings(
    val language: AppLanguage,
    val appTitle: String,
    val appSubtitle: String,
    val navQibla: String,
    val navPrayers: String,
    val navNearby: String,
    val navTasbih: String,
    val navSettings: String,

    // Compass & Status
    val statusAligned: String,
    val statusTurnRight: String,
    val statusTurnLeft: String,
    val statusHoldFlat: String,
    val statusPointPhone: String,
    val holyKaabaMakkah: String,
    val makkah: String,
    val toMakkah: String,
    val qiblaDirection: String,
    val qiblaBearing: String,
    val bearing: String,
    val heading: String,
    val distance: String,
    val km: String,
    val miles: String,
    val qiblaAligned: String,
    val deviceTilted: String,
    val turnPhone: String,
    val inTime: String,

    // Accuracy
    val accuracyHigh: String,
    val accuracyMedium: String,
    val accuracyLow: String,
    val accuracyUnreliable: String,

    // Prayers
    val prayerTimesTitle: String,
    val astronomicalSchedule: String,
    val nextPrayer: String,
    val timeRemaining: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val calculationAuthority: String,

    // Settings
    val settingsTitle: String,
    val sectionLanguage: String,
    val sectionThemes: String,
    val sectionAppPreferences: String,
    val sectionCalculationMethod: String,
    val sectionSensorsUnits: String,
    val sectionFeedback: String,
    val sectionCompass: String,
    val sectionAboutApp: String,
    val lightThemes: String,
    val darkThemes: String,
    val autoDetectAuthority: String,
    val autoDetectSubtitle: String,
    val officialAuthority: String,
    val selectCalculationMethod: String,
    val trueNorthTitle: String,
    val trueNorthSubtitle: String,
    val calibrateSensorsAction: String,
    val calibrateSensorsSubtitle: String,
    val hapticTitle: String,
    val hapticSubtitle: String,
    val soundTitle: String,
    val soundSubtitle: String,
    val rotatingDialTitle: String,
    val rotatingDialSubtitleOn: String,
    val rotatingDialSubtitleOff: String,
    val dialRotating: String,
    val dialFixed: String,
    val useKmTitle: String,
    val useKmSubtitleOn: String,
    val useKmSubtitleOff: String,
    val unitKm: String,
    val unitMiles: String,
    val clockFormatTitle: String,
    val clock12Hour: String,
    val clock24Hour: String,
    val kaabaCoordinatesTitle: String,
    val geodesyDetailsTitle: String,
    val aboutAppName: String,
    val aboutAppVersion: String,
    val aboutAppDescription: String,
    val aboutAppPrivacy: String,
    val aboutAppKaabaDatum: String,
    val close: String,

    // Nearby Mosques
    val nearbyMosquesTitle: String,
    val nearbyMosquesSubtitle: String,
    val openInMaps: String,
    val findMasjidsAroundYou: String,
    val estimatedDistance: String,
    val jumaaPrayer: String,
    val getDirections: String,

    // Calibration
    val calibrationTitle: String,
    val calibrationSubtitle: String,
    val calibrationInstruction: String,
    val sensorStatus: String,
    val done: String,

    // Digital Tasbih
    val tasbihTitle: String,
    val tasbihSubtitle: String,
    val tasbihReset: String,
    val tasbihTarget: String,
    val tasbihRound: String,
    val tasbihTotal: String,
    val tasbihTapInstruction: String,
    val tasbihTargetReached: String,

    // City Selector
    val selectCityTitle: String,
    val useCurrentGps: String,
    val searchCitiesPlaceholder: String,
    val customCoordinates: String
)

val EnglishStrings = AppStrings(
    language = AppLanguage.ENGLISH,
    appTitle = "Qibla Finder",
    appSubtitle = "High Precision Makkah Azimuth",
    navQibla = "Qibla",
    navPrayers = "Prayers",
    navNearby = "Nearby",
    navTasbih = "Tasbih",
    navSettings = "Settings",

    statusAligned = "Aligned with Kaaba! (🕋 Qibla Found)",
    statusTurnRight = "Turn Right ➔",
    statusTurnLeft = "Turn Left ⬅",
    statusHoldFlat = "Hold phone flat on your palm for best precision",
    statusPointPhone = "Point phone towards the Kaaba",
    holyKaabaMakkah = "MAKKAH AL-MUKARRAMAH",
    makkah = "MAKKAH",
    toMakkah = "To Makkah",
    qiblaDirection = "Qibla Direction",
    qiblaBearing = "Qibla Bearing",
    bearing = "Bearing",
    heading = "Heading",
    distance = "Distance",
    km = "km",
    miles = "mi",
    qiblaAligned = "QIBLA ALIGNED",
    deviceTilted = "DEVICE TILTED",
    turnPhone = "TARGET GUIDANCE",
    inTime = "In",

    accuracyHigh = "High Precision",
    accuracyMedium = "Medium Precision",
    accuracyLow = "Low Precision",
    accuracyUnreliable = "Calibration Needed",

    prayerTimesTitle = "Prayer Times",
    astronomicalSchedule = "Astronomical Calculation",
    nextPrayer = "Next Prayer",
    timeRemaining = "Remaining",
    fajr = "Fajr",
    sunrise = "Sunrise",
    dhuhr = "Dhuhr",
    asr = "Asr",
    maghrib = "Maghrib",
    isha = "Isha",
    calculationAuthority = "Calculation Authority",

    settingsTitle = "Settings & Preferences",
    sectionLanguage = "LANGUAGE / اللغة",
    sectionThemes = "VISUAL THEMES",
    sectionAppPreferences = "APP PREFERENCES",
    sectionCalculationMethod = "PRAYER CALCULATION METHOD",
    sectionSensorsUnits = "SENSORS & CALIBRATION",
    sectionFeedback = "FEEDBACK & ALERTS",
    sectionCompass = "COMPASS & HARDWARE",
    sectionAboutApp = "ABOUT APP",
    lightThemes = "Light Themes",
    darkThemes = "Dark Themes",
    autoDetectAuthority = "Auto-Detect Authority",
    autoDetectSubtitle = "Automatically select official authority based on location",
    officialAuthority = "Official Authority",
    selectCalculationMethod = "Select Calculation Method",
    trueNorthTitle = "True North (Geodetic)",
    trueNorthSubtitle = "Applies geomagnetic declination model for true geographic Kaaba azimuth",
    calibrateSensorsAction = "Calibrate Compass Sensor",
    calibrateSensorsSubtitle = "Perform figure-8 motion for maximum azimuth accuracy",
    hapticTitle = "Haptic Vibration Pulse",
    hapticSubtitle = "Tactile feedback when pointing within ±3° of the Kaaba",
    soundTitle = "Alignment Audio Chime",
    soundSubtitle = "Harmonic tone indicator when Qibla direction is locked",
    rotatingDialTitle = "Dial Mode",
    rotatingDialSubtitleOn = "Dial rotates with heading (Kaaba at top)",
    rotatingDialSubtitleOff = "Fixed dial with rotating needle",
    dialRotating = "Rotating Dial",
    dialFixed = "Fixed Dial",
    useKmTitle = "Distance Unit",
    useKmSubtitleOn = "Distance in Kilometers",
    useKmSubtitleOff = "Distance in Miles",
    unitKm = "Kilometers (km)",
    unitMiles = "Miles (mi)",
    clockFormatTitle = "Prayer Time Clock",
    clock12Hour = "12-Hour (AM/PM)",
    clock24Hour = "24-Hour",
    kaabaCoordinatesTitle = "Holy Kaaba Coordinates",
    geodesyDetailsTitle = "Geodesy & Kaaba Coordinates",
    aboutAppName = "Qibla Compass & Prayer Times",
    aboutAppVersion = "Version 2.4.0 (Astronomical Build)",
    aboutAppDescription = "High-precision Qibla azimuth calculation with geodetic magnetic declination correction, official prayer calculation authorities, digital tasbih with authentic Azhkar, and offline astronomical engine.",
    aboutAppPrivacy = "100% On-Device & Privacy-Focused. No data collected or shared.",
    aboutAppKaabaDatum = "Holy Kaaba Datum (WGS 84)",
    close = "Close",

    nearbyMosquesTitle = "Nearby Mosques",
    nearbyMosquesSubtitle = "Find Masjids & Prayer Spaces Around You",
    openInMaps = "Open in Google Maps",
    findMasjidsAroundYou = "Search for all mosques and Islamic centers near your current coordinates.",
    estimatedDistance = "Approx. Distance",
    jumaaPrayer = "Juma'a Mosque",
    getDirections = "Get Directions",

    calibrationTitle = "Compass Calibration",
    calibrationSubtitle = "Calibrate magnetic sensor for precise Kaaba alignment",
    calibrationInstruction = "Wave phone in a smooth figure-8 motion until high accuracy is achieved.",
    sensorStatus = "Sensor Precision",
    done = "Done",

    tasbihTitle = "Digital Tasbih",
    tasbihSubtitle = "Daily Dhikr & Remembrance Counter",
    tasbihReset = "Reset",
    tasbihTarget = "Target",
    tasbihRound = "Round",
    tasbihTotal = "Total",
    tasbihTapInstruction = "Tap anywhere to count",
    tasbihTargetReached = "Target completed! 🎉",

    selectCityTitle = "Select City",
    useCurrentGps = "Use Live GPS Location",
    searchCitiesPlaceholder = "Search city or country...",
    customCoordinates = "Custom Coordinates"
)

val ArabicStrings = AppStrings(
    language = AppLanguage.ARABIC,
    appTitle = "بوصلة القبلة",
    appSubtitle = "اتجاه الكعبة المشرفة بدقة هندسية عالية",
    navQibla = "القبلة",
    navPrayers = "الصلاة",
    navNearby = "المساجد",
    navTasbih = "التسبيح",
    navSettings = "الإعدادات",

    statusAligned = "باتجاه الكعبة المشرفة تماماً! (🕋 تم تحديد القبلة)",
    statusTurnRight = "استدر يميناً ➔",
    statusTurnLeft = "استدر يساراً ⬅",
    statusHoldFlat = "ضع الهاتف بشكل مستوٍ على راحة يدك لدقة أعلى",
    statusPointPhone = "وجّه الهاتف نحو الكعبة المشرفة",
    holyKaabaMakkah = "مكة المكرمة",
    makkah = "مكة المكرمة",
    toMakkah = "إلى مكة",
    qiblaDirection = "اتجاه القبلة",
    qiblaBearing = "زاوية القبلة",
    bearing = "زاوية القبلة",
    heading = "الاتجاه الحالي",
    distance = "المسافة إلى مكة",
    km = "كم",
    miles = "ميل",
    qiblaAligned = "تمت محاذاة القبلة",
    deviceTilted = "الهاتف مائل",
    turnPhone = "توجيه الهاتف",
    inTime = "خلال",

    accuracyHigh = "دقة ممتازة",
    accuracyMedium = "دقة متوسطة",
    accuracyLow = "دقة منخفضة",
    accuracyUnreliable = "يحتاج إلى معايرة",

    prayerTimesTitle = "مواقيت الصلاة",
    astronomicalSchedule = "الحساب الفلكي الدقيق",
    nextPrayer = "الصلاة القادمة",
    timeRemaining = "المتبقي",
    fajr = "الفجر",
    sunrise = "الشروق",
    dhuhr = "الظهر",
    asr = "العصر",
    maghrib = "المغرب",
    isha = "العشاء",
    calculationAuthority = "طريقة الحساب المعتمدة",

    settingsTitle = "الإعدادات والمظهر",
    sectionLanguage = "LANGUAGE / اللغة",
    sectionThemes = "المظهر والألوان",
    sectionAppPreferences = "تفضيلات التطبيق",
    sectionCalculationMethod = "طريقة حساب مواقيت الصلاة",
    sectionSensorsUnits = "الحساسات والمعايرة",
    sectionFeedback = "التنبيهات والاستجابة",
    sectionCompass = "البوصلة وأجهزة الاستشعار",
    sectionAboutApp = "حول التطبيق",
    lightThemes = "السمات الفاتحة",
    darkThemes = "السمات الداكنة",
    autoDetectAuthority = "التعرف التلقائي على الهيئة",
    autoDetectSubtitle = "تحديد طريقة الحساب الرسمية تلقائياً حسب موقعك",
    officialAuthority = "الهيئة الرسمية المعتمدة",
    selectCalculationMethod = "اختر هيئة الحساب الرسمية",
    trueNorthTitle = "الشمال الحقيقي (الجيوديسي)",
    trueNorthSubtitle = "تطبيق نموذج الانحراف المغناطيسي للوصول لزاوية الكعبة الحقيقية",
    calibrateSensorsAction = "معايرة حساس البوصلة",
    calibrateSensorsSubtitle = "تحريك الهاتف بحركة رقم 8 للوصول لأعلى دقة للقبلة",
    hapticTitle = "الاهتزاز عند المحاذاة",
    hapticSubtitle = "نبض لمسي عند التوجه نحو القبلة في نطاق ±3 درجات",
    soundTitle = "التنبيه الصوتي عند المحاذاة",
    soundSubtitle = "نغمة هادئة تؤكد ضبط اتجاه القبلة بنجاح",
    rotatingDialTitle = "نمط قرص البوصلة",
    rotatingDialSubtitleOn = "يدور القرص مع حركتك (الكعبة في الأعلى)",
    rotatingDialSubtitleOff = "قرص ثابت مع إبرة متحركة",
    dialRotating = "قرص متحرك",
    dialFixed = "قرص ثابت",
    useKmTitle = "وحدة قياس المسافة",
    useKmSubtitleOn = "عرض المسافات بالكيلومتر",
    useKmSubtitleOff = "عرض المسافات بالميل",
    unitKm = "كيلومتر (كم)",
    unitMiles = "ميل (mi)",
    clockFormatTitle = "تنسيق وقت الصلاة",
    clock12Hour = "١٢ ساعة (ص/م)",
    clock24Hour = "٢٤ ساعة",
    kaabaCoordinatesTitle = "إحداثيات الكعبة المشرفة",
    geodesyDetailsTitle = "الجيوديسيا وإحداثيات الكعبة",
    aboutAppName = "بوصلة القبلة ومواقيت الصلاة",
    aboutAppVersion = "الإصدار 2.4.0 (دقة فلكية)",
    aboutAppDescription = "تطبيق متطور لحساب اتجاه القبلة بدقة جيوديسية فائقة مع تصحيح الانحراف المغناطيسي، ومواقيت الصلاة الرسمية، وسبحة إلكترونية مع أذكار مأثورة، يعمل بالكامل بدون إنترنت.",
    aboutAppPrivacy = "خصوصية كاملة ١٠٠٪ داخل جهازك، لا يتم جمع أو مشاركة أي بيانات.",
    aboutAppKaabaDatum = "مرجع الكعبة المشرفة (WGS 84)",
    close = "إغلاق",

    nearbyMosquesTitle = "المساجد القريبة",
    nearbyMosquesSubtitle = "البحث عن المساجد والمصليات القريبة منك",
    openInMaps = "فتح في خرائط Google",
    findMasjidsAroundYou = "البحث عن جميع المساجد والمراكز الإسلامية المحيطة بموقعك الحالي.",
    estimatedDistance = "المسافة التقريبية",
    jumaaPrayer = "مسجد جمعة",
    getDirections = "الاتجاهات",

    calibrationTitle = "معايرة البوصلة",
    calibrationSubtitle = "معايرة الحساس المغناطيسي لأعلى دقة في تحديد القبلة",
    calibrationInstruction = "حرّك الهاتف بحركة دائرية على شكل رقم 8 حتى ترتفع دقة الحساس.",
    sensorStatus = "حالة دقة الحساس",
    done = "تم",

    tasbihTitle = "السبحة الإلكترونية",
    tasbihSubtitle = "عداد الأذكار والتسبيح اليومي",
    tasbihReset = "إعادة ضبط",
    tasbihTarget = "الهدف",
    tasbihRound = "الدورة",
    tasbihTotal = "الإجمالي",
    tasbihTapInstruction = "المس أي مكان للتسبيح",
    tasbihTargetReached = "اكتمل عدد التسبيح المحدد! 🎉",

    selectCityTitle = "اختيار المدينة",
    useCurrentGps = "استخدام الموقع الفعلي (GPS)",
    searchCitiesPlaceholder = "ابحث عن مدينة أو دولة...",
    customCoordinates = "إحداثيات مخصصة"
)

fun getAppStrings(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.ARABIC -> ArabicStrings
    AppLanguage.ENGLISH -> EnglishStrings
}

val LocalAppStrings = staticCompositionLocalOf { EnglishStrings }

object AppStr {
    val current: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current

    val language: AppLanguage @Composable @ReadOnlyComposable get() = current.language
    val appTitle: String @Composable @ReadOnlyComposable get() = current.appTitle
    val appSubtitle: String @Composable @ReadOnlyComposable get() = current.appSubtitle
    val navQibla: String @Composable @ReadOnlyComposable get() = current.navQibla
    val navPrayers: String @Composable @ReadOnlyComposable get() = current.navPrayers
    val navNearby: String @Composable @ReadOnlyComposable get() = current.navNearby
    val navTasbih: String @Composable @ReadOnlyComposable get() = current.navTasbih
    val navSettings: String @Composable @ReadOnlyComposable get() = current.navSettings
    val statusAligned: String @Composable @ReadOnlyComposable get() = current.statusAligned
    val statusTurnRight: String @Composable @ReadOnlyComposable get() = current.statusTurnRight
    val statusTurnLeft: String @Composable @ReadOnlyComposable get() = current.statusTurnLeft
    val statusHoldFlat: String @Composable @ReadOnlyComposable get() = current.statusHoldFlat
    val statusPointPhone: String @Composable @ReadOnlyComposable get() = current.statusPointPhone
    val holyKaabaMakkah: String @Composable @ReadOnlyComposable get() = current.holyKaabaMakkah
    val makkah: String @Composable @ReadOnlyComposable get() = current.makkah
    val toMakkah: String @Composable @ReadOnlyComposable get() = current.toMakkah
    val qiblaDirection: String @Composable @ReadOnlyComposable get() = current.qiblaDirection
    val qiblaBearing: String @Composable @ReadOnlyComposable get() = current.qiblaBearing
    val bearing: String @Composable @ReadOnlyComposable get() = current.bearing
    val heading: String @Composable @ReadOnlyComposable get() = current.heading
    val distance: String @Composable @ReadOnlyComposable get() = current.distance
    val km: String @Composable @ReadOnlyComposable get() = current.km
    val miles: String @Composable @ReadOnlyComposable get() = current.miles
    val qiblaAligned: String @Composable @ReadOnlyComposable get() = current.qiblaAligned
    val deviceTilted: String @Composable @ReadOnlyComposable get() = current.deviceTilted
    val turnPhone: String @Composable @ReadOnlyComposable get() = current.turnPhone
    val inTime: String @Composable @ReadOnlyComposable get() = current.inTime
    val accuracyHigh: String @Composable @ReadOnlyComposable get() = current.accuracyHigh
    val accuracyMedium: String @Composable @ReadOnlyComposable get() = current.accuracyMedium
    val accuracyLow: String @Composable @ReadOnlyComposable get() = current.accuracyLow
    val accuracyUnreliable: String @Composable @ReadOnlyComposable get() = current.accuracyUnreliable
    val prayerTimesTitle: String @Composable @ReadOnlyComposable get() = current.prayerTimesTitle
    val astronomicalSchedule: String @Composable @ReadOnlyComposable get() = current.astronomicalSchedule
    val nextPrayer: String @Composable @ReadOnlyComposable get() = current.nextPrayer
    val timeRemaining: String @Composable @ReadOnlyComposable get() = current.timeRemaining
    val fajr: String @Composable @ReadOnlyComposable get() = current.fajr
    val sunrise: String @Composable @ReadOnlyComposable get() = current.sunrise
    val dhuhr: String @Composable @ReadOnlyComposable get() = current.dhuhr
    val asr: String @Composable @ReadOnlyComposable get() = current.asr
    val maghrib: String @Composable @ReadOnlyComposable get() = current.maghrib
    val isha: String @Composable @ReadOnlyComposable get() = current.isha
    val calculationAuthority: String @Composable @ReadOnlyComposable get() = current.calculationAuthority
    val settingsTitle: String @Composable @ReadOnlyComposable get() = current.settingsTitle
    val sectionLanguage: String @Composable @ReadOnlyComposable get() = current.sectionLanguage
    val sectionThemes: String @Composable @ReadOnlyComposable get() = current.sectionThemes
    val sectionAppPreferences: String @Composable @ReadOnlyComposable get() = current.sectionAppPreferences
    val sectionCalculationMethod: String @Composable @ReadOnlyComposable get() = current.sectionCalculationMethod
    val sectionSensorsUnits: String @Composable @ReadOnlyComposable get() = current.sectionSensorsUnits
    val sectionFeedback: String @Composable @ReadOnlyComposable get() = current.sectionFeedback
    val sectionCompass: String @Composable @ReadOnlyComposable get() = current.sectionCompass
    val sectionAboutApp: String @Composable @ReadOnlyComposable get() = current.sectionAboutApp
    val lightThemes: String @Composable @ReadOnlyComposable get() = current.lightThemes
    val darkThemes: String @Composable @ReadOnlyComposable get() = current.darkThemes
    val autoDetectAuthority: String @Composable @ReadOnlyComposable get() = current.autoDetectAuthority
    val autoDetectSubtitle: String @Composable @ReadOnlyComposable get() = current.autoDetectSubtitle
    val officialAuthority: String @Composable @ReadOnlyComposable get() = current.officialAuthority
    val selectCalculationMethod: String @Composable @ReadOnlyComposable get() = current.selectCalculationMethod
    val trueNorthTitle: String @Composable @ReadOnlyComposable get() = current.trueNorthTitle
    val trueNorthSubtitle: String @Composable @ReadOnlyComposable get() = current.trueNorthSubtitle
    val calibrateSensorsAction: String @Composable @ReadOnlyComposable get() = current.calibrateSensorsAction
    val calibrateSensorsSubtitle: String @Composable @ReadOnlyComposable get() = current.calibrateSensorsSubtitle
    val hapticTitle: String @Composable @ReadOnlyComposable get() = current.hapticTitle
    val hapticSubtitle: String @Composable @ReadOnlyComposable get() = current.hapticSubtitle
    val soundTitle: String @Composable @ReadOnlyComposable get() = current.soundTitle
    val soundSubtitle: String @Composable @ReadOnlyComposable get() = current.soundSubtitle
    val rotatingDialTitle: String @Composable @ReadOnlyComposable get() = current.rotatingDialTitle
    val rotatingDialSubtitleOn: String @Composable @ReadOnlyComposable get() = current.rotatingDialSubtitleOn
    val rotatingDialSubtitleOff: String @Composable @ReadOnlyComposable get() = current.rotatingDialSubtitleOff
    val dialRotating: String @Composable @ReadOnlyComposable get() = current.dialRotating
    val dialFixed: String @Composable @ReadOnlyComposable get() = current.dialFixed
    val useKmTitle: String @Composable @ReadOnlyComposable get() = current.useKmTitle
    val useKmSubtitleOn: String @Composable @ReadOnlyComposable get() = current.useKmSubtitleOn
    val useKmSubtitleOff: String @Composable @ReadOnlyComposable get() = current.useKmSubtitleOff
    val unitKm: String @Composable @ReadOnlyComposable get() = current.unitKm
    val unitMiles: String @Composable @ReadOnlyComposable get() = current.unitMiles
    val clockFormatTitle: String @Composable @ReadOnlyComposable get() = current.clockFormatTitle
    val clock12Hour: String @Composable @ReadOnlyComposable get() = current.clock12Hour
    val clock24Hour: String @Composable @ReadOnlyComposable get() = current.clock24Hour
    val kaabaCoordinatesTitle: String @Composable @ReadOnlyComposable get() = current.kaabaCoordinatesTitle
    val geodesyDetailsTitle: String @Composable @ReadOnlyComposable get() = current.geodesyDetailsTitle
    val aboutAppName: String @Composable @ReadOnlyComposable get() = current.aboutAppName
    val aboutAppVersion: String @Composable @ReadOnlyComposable get() = current.aboutAppVersion
    val aboutAppDescription: String @Composable @ReadOnlyComposable get() = current.aboutAppDescription
    val aboutAppPrivacy: String @Composable @ReadOnlyComposable get() = current.aboutAppPrivacy
    val aboutAppKaabaDatum: String @Composable @ReadOnlyComposable get() = current.aboutAppKaabaDatum
    val close: String @Composable @ReadOnlyComposable get() = current.close
    val nearbyMosquesTitle: String @Composable @ReadOnlyComposable get() = current.nearbyMosquesTitle
    val nearbyMosquesSubtitle: String @Composable @ReadOnlyComposable get() = current.nearbyMosquesSubtitle
    val openInMaps: String @Composable @ReadOnlyComposable get() = current.openInMaps
    val findMasjidsAroundYou: String @Composable @ReadOnlyComposable get() = current.findMasjidsAroundYou
    val estimatedDistance: String @Composable @ReadOnlyComposable get() = current.estimatedDistance
    val jumaaPrayer: String @Composable @ReadOnlyComposable get() = current.jumaaPrayer
    val getDirections: String @Composable @ReadOnlyComposable get() = current.getDirections
    val calibrationTitle: String @Composable @ReadOnlyComposable get() = current.calibrationTitle
    val calibrationSubtitle: String @Composable @ReadOnlyComposable get() = current.calibrationSubtitle
    val calibrationInstruction: String @Composable @ReadOnlyComposable get() = current.calibrationInstruction
    val sensorStatus: String @Composable @ReadOnlyComposable get() = current.sensorStatus
    val done: String @Composable @ReadOnlyComposable get() = current.done
    val tasbihTitle: String @Composable @ReadOnlyComposable get() = current.tasbihTitle
    val tasbihSubtitle: String @Composable @ReadOnlyComposable get() = current.tasbihSubtitle
    val tasbihReset: String @Composable @ReadOnlyComposable get() = current.tasbihReset
    val tasbihTarget: String @Composable @ReadOnlyComposable get() = current.tasbihTarget
    val tasbihRound: String @Composable @ReadOnlyComposable get() = current.tasbihRound
    val tasbihTotal: String @Composable @ReadOnlyComposable get() = current.tasbihTotal
    val tasbihTapInstruction: String @Composable @ReadOnlyComposable get() = current.tasbihTapInstruction
    val tasbihTargetReached: String @Composable @ReadOnlyComposable get() = current.tasbihTargetReached
    val selectCityTitle: String @Composable @ReadOnlyComposable get() = current.selectCityTitle
    val useCurrentGps: String @Composable @ReadOnlyComposable get() = current.useCurrentGps
    val searchCitiesPlaceholder: String @Composable @ReadOnlyComposable get() = current.searchCitiesPlaceholder
    val customCoordinates: String @Composable @ReadOnlyComposable get() = current.customCoordinates
}
