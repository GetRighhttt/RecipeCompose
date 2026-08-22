package com.example.recipe_app_compose.features.location.data.location

import android.content.Context
import android.location.Location
import com.example.recipe_app_compose.core.util.permissions.hasForegroundLocationPermission
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidCurrentLocationProvider(context: Context) : CurrentLocationProvider {
    private val applicationContext = context.applicationContext
    private val locationClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)

    override suspend fun getCurrentLocation(): LocationData? {
        if (!applicationContext.hasForegroundLocationPermission()) return null

        val location = requestRecentLocation() ?: requestCurrentLocation()
        return location?.let { LocationData(it.latitude, it.longitude) }
    }

    private suspend fun requestCurrentLocation(): Location? {
        val request = CurrentLocationRequest.Builder()
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(MAX_LOCATION_AGE_MILLIS)
            .setDurationMillis(LOCATION_TIMEOUT_MILLIS)
            .build()

        return try {
            suspendCancellableCoroutine { continuation ->
                val cancellationTokenSource = CancellationTokenSource()
                continuation.invokeOnCancellation { cancellationTokenSource.cancel() }

                locationClient
                    .getCurrentLocation(request, cancellationTokenSource.token)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) {
                            continuation.resumeWithException(error)
                        }
                    }
                    .addOnCanceledListener { continuation.cancel() }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun requestRecentLocation(): Location? {
        val request = LastLocationRequest.Builder()
            .setMaxUpdateAgeMillis(MAX_LAST_LOCATION_AGE_MILLIS)
            .build()

        return try {
            suspendCancellableCoroutine { continuation ->
                locationClient
                    .getLastLocation(request)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) {
                            continuation.resumeWithException(error)
                        }
                    }
                    .addOnCanceledListener { continuation.cancel() }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val MAX_LOCATION_AGE_MILLIS = 5 * 60 * 1000L
        const val MAX_LAST_LOCATION_AGE_MILLIS = 5 * 60 * 1000L
        const val LOCATION_TIMEOUT_MILLIS = 10_000L
    }
}
