package com.example.recipe_app_compose.features.location.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData

/** Native map canvas; shared Compose owns the surrounding state and controls. */
@Composable
expect fun PlatformLocationMap(
    initialLocation: LocationData,
    selectedLocation: LocationData,
    markerTitle: String,
    markerSubtitle: String,
    onLocationSelected: (LocationData) -> Unit,
    onMapLoaded: () -> Unit,
    modifier: Modifier = Modifier,
)

interface DirectionsLauncher {
    fun openDrivingDirections(destination: LocationData)
}

@Composable
expect fun rememberDirectionsLauncher(): DirectionsLauncher
