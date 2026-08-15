package com.example.recipe_app_compose.features.location.presentation.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.util.permissions.PermissionUtils
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
fun GoogleLocationSelectionScreen(
    location: LocationData
) {
    val context = LocalContext.current
    val locationUtils = remember(context) { PermissionUtils(context) }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        )
    }
    val properties = remember { MapProperties(mapType = MapType.HYBRID) }
    var markerStateValue by remember { mutableStateOf(false) }
    val newMarkerState = remember { MarkerState() }

    val businessLocation = LatLng(location.latitude, location.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(businessLocation, 12f)
    }
    val businessMarkerState = remember {
        MarkerState(position = businessLocation)
    }

    var businessAddress by remember { mutableStateOf("") }
    LaunchedEffect(businessMarkerState.position) {
        businessAddress = locationUtils.reverseGeocodeLocation(
            LocationData(
                businessMarkerState.position.latitude,
                businessMarkerState.position.longitude
            )
        )
    }

    var newAddress by remember { mutableStateOf("") }
    LaunchedEffect(markerStateValue, newMarkerState.position) {
        if (markerStateValue) {
            newAddress = locationUtils.reverseGeocodeLocation(
                LocationData(
                    newMarkerState.position.latitude,
                    newMarkerState.position.longitude
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
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
                    title = stringResource(R.string.you_clicked_here),
                    draggable = true,
                    snippet = newAddress
                )
            } else {
                Marker(
                    state = businessMarkerState,
                    title = stringResource(R.string.business_location),
                    snippet = businessAddress
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = {
                val destination = if (markerStateValue) {
                    newMarkerState.position
                } else {
                    businessMarkerState.position
                }
                context.openDrivingDirections(destination)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(R.string.open_driving_directions)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
        )
    }
}

private fun Context.openDrivingDirections(destination: LatLng) {
    val directionsUri = "https://www.google.com/maps/dir/".toUri()
        .buildUpon()
        .appendQueryParameter("api", "1")
        .appendQueryParameter("destination", "${destination.latitude},${destination.longitude}")
        .appendQueryParameter("travelmode", "driving")
        .appendQueryParameter("dir_action", "navigate")
        .build()

    try {
        startActivity(Intent(Intent.ACTION_VIEW, directionsUri))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.unable_to_open_google_maps, Toast.LENGTH_LONG).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, R.string.unable_to_open_google_maps, Toast.LENGTH_LONG).show()
    }
}
