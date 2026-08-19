package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.example.model.LocationData
import com.example.util.QiblaMath
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers.IO) {
        try {
            val location: Location? = suspendCancellableCoroutine { continuation ->
                val cts = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cts.token
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        continuation.resume(loc)
                    } else {
                        // Fallback to last known location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            continuation.resume(lastLoc ?: getFallbackSystemLocation())
                        }.addOnFailureListener {
                            continuation.resume(getFallbackSystemLocation())
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(getFallbackSystemLocation())
                }

                continuation.invokeOnCancellation {
                    cts.cancel()
                }
            }

            if (location != null) {
                val (city, country) = getAddressFromCoordinates(location.latitude, location.longitude)
                val declination = QiblaMath.getMagneticDeclination(
                    location.latitude,
                    location.longitude,
                    location.altitude
                )
                LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    cityName = city,
                    countryName = country,
                    isGps = true,
                    accuracyMeters = location.accuracy,
                    magneticDeclination = declination
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFallbackSystemLocation(): Location? {
        return try {
            val providers = locationManager?.getProviders(true) ?: return null
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            bestLocation
        } catch (e: Exception) {
            null
        }
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Current Location"
                val country = addr.countryName ?: ""
                city to country
            } else {
                "Location (${String.format(Locale.US, "%.2f", latitude)}, ${String.format(Locale.US, "%.2f", longitude)})" to ""
            }
        } catch (e: Exception) {
            "Location (${String.format(Locale.US, "%.2f", latitude)}, ${String.format(Locale.US, "%.2f", longitude)})" to ""
        }
    }
}
