package com.example.recipe_app_compose.features.location.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.core.util.PermissionUtils
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.presentation.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleLocationSelectionScreen(
    location: LocationData
) {
    // context
    val context = LocalContext.current

    // viewmodel states
    val locationViewModel = LocationViewModel()
    val isLoading = locationViewModel.isLoading.collectAsState()

    // location states
    val businessLocation = remember { mutableStateOf(LatLng(location.latitude, location.longitude)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(businessLocation.value, 10f)
    }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.HYBRID)) }
    val locationUtils = PermissionUtils(context)

    // draggable state markers
    var markerStateValue by remember { mutableStateOf(false) }
    val newMarkerState = rememberMarkerState()

    // use geocode method to get addresses
    val businessAddress = locationUtils.reverseGeocodeLocation(
        LocationData(
            businessLocation.value.latitude,
            businessLocation.value.longitude
        )
    )

    val newAddress = locationUtils.reverseGeocodeLocation(
        LocationData(
            newMarkerState.position.latitude,
            newMarkerState.position.longitude
        )
    )

    // UI
    Column(modifier = Modifier.fillMaxSize()) {

        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            GoogleMap(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings,
                onMapClick = { location ->
                    markerStateValue = true
                    newMarkerState.position = location
                }
            ) {
                if (markerStateValue) {
                    Marker(
                        state = newMarkerState,
                        title = "You Clicked Here:",
                        draggable = true,
                        snippet = newAddress
                    )
                } else {
                    Marker(
                        state = MarkerState(position = businessLocation.value),
                        title = "Business Location",
                        snippet = businessAddress
                    )
                }
            }
        }
    }
}