package com.example.recipe_app_compose.core.util.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class ReverseGeocoder(context: Context) {
    private val appContext = context.applicationContext

    suspend fun reverseGeocodeLocation(location: LocationData): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            reverseGeocodeAsync(location)
        } else {
            reverseGeocodeBlocking(location)
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun reverseGeocodeAsync(location: LocationData): String? =
        suspendCancellableCoroutine { continuation ->
            runCatching {
                Geocoder(appContext, Locale.getDefault()).getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                ) { addresses ->
                    if (continuation.isActive) {
                        continuation.resume(addresses.firstOrNull()?.getAddressLine(0))
                    }
                }
            }.onFailure {
                if (continuation.isActive) continuation.resume(null)
            }
        }

    @SuppressLint("Deprecated")
    @Suppress("DEPRECATION")
    private suspend fun reverseGeocodeBlocking(location: LocationData): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                Geocoder(appContext, Locale.getDefault())
                    .getFromLocation(location.latitude, location.longitude, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
            }.getOrNull()
        }
}
