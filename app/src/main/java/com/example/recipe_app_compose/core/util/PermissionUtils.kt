package com.example.recipe_app_compose.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.recipe_app_compose.features.location.domain.model.LocationData
import com.example.recipe_app_compose.features.location.presentation.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

internal class PermissionUtils(private val context: Context) {

    /*
    Location Data

    1. Check if permissions exists
    2. Request permissions
    3. Get location updates
    4. Convert to lat long to address
    */
    internal val hasLocationPermissions: (context: Context) -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    internal val requestLocationUpdates: (LocationViewModel) -> Unit = { viewModel ->
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    val locationData =
                        LocationData(
                            location.latitude,
                            location.longitude
                        )
                    viewModel.updateLocation(locationData)
                }
            }
        }
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build()

        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("Deprecated")
    // geocoder to convert lat and long to address
    internal val reverseGeocodeLocation: (LocationData) -> String = { locationData ->
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(locationData.latitude, locationData.longitude)
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            addresses[0].getAddressLine(0)
        } else {
            "Address Not Found."
        }
    }

    /*
    NetWork Data
     */
    internal val hasNetworkPermissions: (context: Context) -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_WIFI_STATE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED

    }
}