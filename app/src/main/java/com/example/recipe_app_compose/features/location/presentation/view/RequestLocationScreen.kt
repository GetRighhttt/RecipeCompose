package com.example.recipe_app_compose.features.location.presentation.view

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.recipe_app_compose.core.util.PermissionUtils
import com.example.recipe_app_compose.features.location.presentation.viewmodel.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RequestPermissions() {

    // context
    val context = LocalContext.current

    // viewmodel states
    val locationViewModel = LocationViewModel()
    val locationUtils = PermissionUtils(context)

    val scope = rememberCoroutineScope()

    // request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.requestLocationUpdates
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location Permission is required for this feature to work",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })

    SideEffect {
        if (locationUtils.hasLocationPermissions(context)) {
            locationUtils.requestLocationUpdates(locationViewModel)
        } else {
            scope.launch { delay(500L) }
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        if(!locationUtils.hasNetworkPermissions(context)) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE
                )
            )
        } else {
            Toast.makeText(context, "Network Permissions granted", Toast.LENGTH_SHORT).show()
        }
    }
}