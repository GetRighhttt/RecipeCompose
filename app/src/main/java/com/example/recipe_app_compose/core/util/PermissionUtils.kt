package com.example.recipe_app_compose.core.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.recipe_app_compose.domain.model.location.LocationData
import com.example.recipe_app_compose.presentation.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class PermissionUtils(private val context: Context) {

    /*
    Location Data
    */
    val hasLocationPermissions: (context: Context) -> Boolean = {
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

    val requestLocationUpdates: (LocationViewModel) -> Unit = { viewModel ->
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    val locationData = LocationData(location.latitude, location.longitude)
                    viewModel.updateLocation(locationData)
                }
            }
        }
    }

    /*
    NetWork Data
     */
    val hasNetworkPermissions: (context: Context) -> Boolean = {
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