package com.example.recipe_app_compose.features.location.presentation.view

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.recipe_app_compose.core.util.PermissionUtils
import com.example.recipe_app_compose.features.categories.presentation.view.MainActivity
import com.example.recipe_app_compose.features.location.presentation.viewmodel.LocationViewModel

@Composable
fun AddressScreen(modifier: Modifier) {
    val locationViewModel = LocationViewModel()
    MyApp(viewModel = locationViewModel)
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = PermissionUtils(context)
    LocationDisplay(locationUtils = locationUtils, viewModel, context = context)
}


@Composable
fun LocationDisplay(
    locationUtils: PermissionUtils,
    viewModel: LocationViewModel,
    context: Context
) {
    val location = viewModel.location.collectAsState().value

    val address = location?.let{
        locationUtils.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult = { permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // I HAVE ACCESS to location

                locationUtils.requestLocationUpdates
            }else{
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                        .show()
                }else{
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        if(location != null){
            Text("Address: ${location.latitude} ${location.longitude} \n $address")
        }else{
            Text(text = "Location not available")
        }


        Button(onClick = {
            if(locationUtils.hasLocationPermissions(context)){
                // Permission already granted update the location
                locationUtils.requestLocationUpdates(viewModel)
            }else{
                // Request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}