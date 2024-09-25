package com.example.recipe_app_compose.features.location.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.core.util.PermissionUtils
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSelectionScreen(
    location: LocationData,
    onLocationSelected: (LocationData) -> Unit
) {

    var alertDialogState by remember { mutableStateOf(false) }
    val userLocation = remember {
        mutableStateOf(LatLng(location.latitude, location.longitude))
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 10f)
    }

    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.HYBRID))
    }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        alertDialogState = false
        GoogleMap(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapClick = { it ->
                userLocation.value = it
                val locationUtils = PermissionUtils(context)
                val address = it.let {
                    locationUtils.reverseGeocodeLocation(LocationData(it.latitude, it.longitude))
                }
                Toast.makeText(context, "Location: $address", Toast.LENGTH_SHORT).show()
            }
        ) {
            Marker(state = MarkerState(position = userLocation.value))
            alertDialogState = false
        }

    }
}
