package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Service to retrieve dynamic real-time GPS / Network coordinates using FusedLocationProviderClient.
 * Gracefully defaults to Giza, Egypt if location is disabled or unavailable.
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        const val DEFAULT_LOCATION_QUERY = "Giza"
        const val DEFAULT_LAT = 30.0131
        const val DEFAULT_LON = 31.2089
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationCoordinates(): Pair<Double, Double>? {
        return try {
            suspendCancellableCoroutine { continuation ->
                val cts = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cts.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(Pair(location.latitude, location.longitude))
                    } else {
                        // Fallback to last known location if available
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                            if (lastLoc != null) {
                                continuation.resume(Pair(lastLoc.latitude, lastLoc.longitude))
                            } else {
                                continuation.resume(null)
                            }
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cts.cancel()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFormattedLocationQuery(): String {
        val coords = getCurrentLocationCoordinates()
        return if (coords != null) {
            "${coords.first},${coords.second}"
        } else {
            DEFAULT_LOCATION_QUERY
        }
    }
}
