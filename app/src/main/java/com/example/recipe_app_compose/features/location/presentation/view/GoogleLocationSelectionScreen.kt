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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.core.util.location.ReverseGeocoder
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@Composable
fun GoogleLocationSelectionScreen(
    location: LocationData,
) {
    if (!location.hasValidCoordinates()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.business_location_unavailable),
                modifier = Modifier.align(Alignment.Center),
            )
        }
        return
    }

    val context = LocalContext.current
    val reverseGeocoder = remember(context) { ReverseGeocoder(context) }
    val addressNotFound = stringResource(R.string.address_not_found)
    val businessLocation = remember(location.latitude, location.longitude) {
        LatLng(location.latitude, location.longitude)
    }
    var selectedLatitude by rememberSaveable(location.latitude, location.longitude) {
        mutableDoubleStateOf(location.latitude)
    }
    var selectedLongitude by rememberSaveable(location.latitude, location.longitude) {
        mutableDoubleStateOf(location.longitude)
    }
    val selectedLocation = remember(selectedLatitude, selectedLongitude) {
        LatLng(selectedLatitude, selectedLongitude)
    }
    val markerState = rememberUpdatedMarkerState(position = selectedLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(businessLocation, 12f)
    }
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            mapToolbarEnabled = false,
        )
    }
    val properties = remember { MapProperties(mapType = MapType.NORMAL) }

    var address by remember { mutableStateOf("") }
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(markerState) {
        snapshotFlow { markerState.position to markerState.isDragging }
            .filter { (_, isDragging) -> isDragging }
            .map { (position, _) -> position }
            .distinctUntilChanged()
            .collect { position ->
                selectedLatitude = position.latitude
                selectedLongitude = position.longitude
            }
    }

    LaunchedEffect(markerState, reverseGeocoder) {
        snapshotFlow {
            if (markerState.isDragging) {
                null
            } else {
                LatLng(selectedLatitude, selectedLongitude)
            }
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collectLatest { position ->
                address = reverseGeocoder.reverseGeocodeLocation(
                    LocationData(position.latitude, position.longitude)
                ) ?: addressNotFound
            }
    }

    val markerTitle = if (selectedLocation == businessLocation) {
        stringResource(R.string.shop_location)
    } else {
        stringResource(R.string.you_clicked_here)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapClick = { position ->
                selectedLatitude = position.latitude
                selectedLongitude = position.longitude
            },
            onMapLoaded = { isMapLoaded = true },
        ) {
            Marker(
                state = markerState,
                title = markerTitle,
                snippet = address,
                contentDescription = markerTitle,
                draggable = true,
            )
        }

        if (!isMapLoaded) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        DirectionsButton(
            onClick = { context.openDrivingDirections(selectedLocation) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        )
    }
}

@Composable
internal fun DirectionsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                imageVector = Icons.Filled.Directions,
                contentDescription = null,
            )
        },
        text = { Text(stringResource(R.string.open_driving_directions)) },
        modifier = modifier,
    )
}

private fun LocationData.hasValidCoordinates(): Boolean =
    latitude.isFinite() && latitude in -90.0..90.0 &&
        longitude.isFinite() && longitude in -180.0..180.0

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
