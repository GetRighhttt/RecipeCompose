package com.example.recipe_app_compose.features.location.presentation.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
actual fun PlatformLocationMap(
    initialLocation: LocationData,
    selectedLocation: LocationData,
    markerTitle: String,
    markerSubtitle: String,
    onLocationSelected: (LocationData) -> Unit,
    onMapLoaded: () -> Unit,
    modifier: Modifier,
) {
    val initialLatLng = remember(initialLocation) {
        LatLng(initialLocation.latitude, initialLocation.longitude)
    }
    val selectedLatLng = remember(selectedLocation) {
        LatLng(selectedLocation.latitude, selectedLocation.longitude)
    }
    val markerState = rememberUpdatedMarkerState(position = selectedLatLng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng, DEFAULT_ZOOM)
    }
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false)
    }
    val properties = remember { MapProperties(mapType = MapType.NORMAL) }

    LaunchedEffect(markerState) {
        snapshotFlow { markerState.position to markerState.isDragging }
            .filter { (_, isDragging) -> isDragging }
            .map { (position, _) -> LocationData(position.latitude, position.longitude) }
            .distinctUntilChanged()
            .collect(onLocationSelected)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = { position ->
            onLocationSelected(LocationData(position.latitude, position.longitude))
        },
        onMapLoaded = onMapLoaded,
    ) {
        Marker(
            state = markerState,
            title = markerTitle,
            snippet = markerSubtitle,
            contentDescription = markerTitle,
            draggable = true,
        )
    }
}

@Composable
actual fun rememberDirectionsLauncher(): DirectionsLauncher {
    val context = LocalContext.current
    return remember(context) { AndroidDirectionsLauncher(context.applicationContext) }
}

private class AndroidDirectionsLauncher(
    private val context: Context,
) : DirectionsLauncher {
    override fun openDrivingDirections(destination: LocationData) {
        val directionsUri = "https://www.google.com/maps/dir/".toUri()
            .buildUpon()
            .appendQueryParameter("api", "1")
            .appendQueryParameter("destination", "${destination.latitude},${destination.longitude}")
            .appendQueryParameter("travelmode", "driving")
            .appendQueryParameter("dir_action", "navigate")
            .build()
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, directionsUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Unable to open Google Maps or a web browser.",
                Toast.LENGTH_LONG,
            ).show()
        } catch (_: SecurityException) {
            Toast.makeText(
                context,
                "Unable to open Google Maps or a web browser.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}

private const val DEFAULT_ZOOM = 12f
