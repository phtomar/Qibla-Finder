package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.QiblaMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Qibla Finder", appName)
  }

  @Test
  fun `verify Qibla bearing calculations`() {
    // London: Lat 51.5074, Lon -0.1278 -> Qibla is approx 118.9 degrees (SE)
    val londonBearing = QiblaMath.calculateQiblaBearing(51.5074, -0.1278)
    assertTrue("London bearing should be approx 118-120 deg", abs(londonBearing - 119.0) < 2.0)

    // New York: Lat 40.7128, Lon -74.0060 -> Qibla is approx 58.5 degrees (ENE)
    val nyBearing = QiblaMath.calculateQiblaBearing(40.7128, -74.0060)
    assertTrue("NY bearing should be approx 58-59 deg", abs(nyBearing - 58.5) < 2.0)

    // Tokyo: Lat 35.6762, Lon 139.6503 -> Qibla is approx 293 degrees (WNW)
    val tokyoBearing = QiblaMath.calculateQiblaBearing(35.6762, 139.6503)
    assertTrue("Tokyo bearing should be approx 293 deg", abs(tokyoBearing - 293.0) < 2.0)
  }

  @Test
  fun `verify Distance calculations`() {
    // Distance from Kaaba to Kaaba should be ~0 km
    val meccaDist = QiblaMath.calculateDistanceKm(QiblaMath.KAABA_LATITUDE, QiblaMath.KAABA_LONGITUDE)
    assertTrue("Distance from Kaaba to itself should be 0", meccaDist < 1.0)

    // Distance from London to Mecca is approx 4790 km
    val londonDist = QiblaMath.calculateDistanceKm(51.5074, -0.1278)
    assertTrue("London to Mecca distance should be approx 4700-4900 km", londonDist in 4700.0..4900.0)
  }

  @Test
  fun `verify Prayer Times calculation`() {
    val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
      set(2026, java.util.Calendar.AUGUST, 19, 12, 0, 0)
    }
    val schedule = QiblaMath.calculatePrayerTimes(
      latitude = 51.5074,
      longitude = -0.1278,
      calendar = cal,
      timeZone = java.util.TimeZone.getTimeZone("UTC")
    )

    // For London in August (UTC):
    // Fajr ~03:30-04:30
    // Sunrise ~05:45
    // Dhuhr ~13:00 (solar noon around 13:00 UTC with Eq of Time)
    // Asr ~16:30-17:30
    // Maghrib ~20:15
    // Isha ~21:45
    val asrHour = schedule.asr.split(":")[0].toInt()
    assertTrue("Asr hour should be in the afternoon (15..18), was ${schedule.asr}", asrHour in 15..18)
    val dhuhrHour = schedule.dhuhr.split(":")[0].toInt()
    assertTrue("Dhuhr hour should be around noon (12..14), was ${schedule.dhuhr}", dhuhrHour in 12..14)
  }

  @Test
  fun `verify Egyptian General Authority of Survey in Cairo`() {
    // Cairo: Lat 30.0444, Lon 31.2357, Timezone UTC+3 (Egypt EEST)
    val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+3")).apply {
      set(2026, java.util.Calendar.AUGUST, 19, 12, 0, 0)
    }
    val schedule = QiblaMath.calculatePrayerTimes(
      latitude = 30.0444,
      longitude = 31.2357,
      calendar = cal,
      timeZone = java.util.TimeZone.getTimeZone("GMT+3"),
      method = com.example.model.CalculationMethod.EGYPTIAN
    )

    assertEquals("Egyptian General Authority of Survey", schedule.calculationMethodName)
    val fajrHour = schedule.fajr.split(":")[0].toInt()
    assertTrue("Fajr in Cairo summer should be ~4-5 AM, was ${schedule.fajr}", fajrHour in 4..5)
    val asrHour = schedule.asr.split(":")[0].toInt()
    assertTrue("Asr in Cairo should be ~16:00-17:00, was ${schedule.asr}", asrHour in 16..17)
  }

  @Test
  fun `verify exactly 3 light and 3 dark themes exist`() {
    val allThemes = com.example.model.AppThemeId.entries
    assertEquals(6, allThemes.size)

    val lightThemes = allThemes.filter { !it.isDark }
    val darkThemes = allThemes.filter { it.isDark }

    assertEquals(3, lightThemes.size)
    assertEquals(3, darkThemes.size)

    // Verify palettes for all themes load cleanly
    allThemes.forEach { themeId ->
      val palette = com.example.ui.theme.getThemePalette(themeId)
      assertEquals(themeId.isDark, palette.isDark)
    }
  }
}

