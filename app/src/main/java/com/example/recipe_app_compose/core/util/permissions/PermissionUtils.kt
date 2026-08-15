package com.example.recipe_app_compose.core.util.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class PermissionUtils(private val context: Context) {

    @SuppressLint("Deprecated")
    // geocoder to convert lat and long to address
    internal suspend fun reverseGeocodeLocation(locationData: LocationData): String =
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                locationData.latitude,
                locationData.longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Address Not Found."
            }
        }
}
