package com.example.recipe_app_compose.features.location.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.recipe_app_compose.core.components.BackTopAppBar
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpShop
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.action_directions
import com.example.recipe_app_compose.shared.generated.resources.business_location_unavailable
import com.example.recipe_app_compose.shared.generated.resources.open_driving_directions
import com.example.recipe_app_compose.shared.generated.resources.shop_location
import com.example.recipe_app_compose.shared.generated.resources.you_clicked_here
import com.example.recipe_app_compose.ui.theme.AppSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Shared marker selection and directions control. The interactive map below it
 * is an Android Google Maps or iOS MapKit implementation.
 */
data class MapDestination(
    val location: LocationData,
    val title: String,
    val subtitle: String,
)

fun YelpShop.toMapDestination(): MapDestination = MapDestination(
    location = LocationData(coordinates.latitude, coordinates.longitude),
    title = name,
    subtitle = listOf(location.address1, location.city, location.state)
        .filter(String::isNotBlank)
        .joinToString(),
)

@Composable
fun SharedLocationSelectionScreen(
    destination: MapDestination,
    onBack: () -> Unit,
    showTopAppBar: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val initialLocation = destination.location
    if (!initialLocation.isValid()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.business_location_unavailable))
        }
        return
    }

    val directionsLauncher = rememberDirectionsLauncher()
    var selectedLocation by remember(initialLocation) { mutableStateOf(initialLocation) }
    var isMapLoaded by remember(initialLocation) { mutableStateOf(false) }
    val markerMoved = selectedLocation != initialLocation
    val markerTitle = if (markerMoved) {
        stringResource(Res.string.you_clicked_here)
    } else {
        destination.title.ifBlank { stringResource(Res.string.shop_location) }
    }

    androidx.compose.foundation.layout.Column(modifier.fillMaxSize()) {
        if (showTopAppBar) {
            BackTopAppBar(
                title = stringResource(Res.string.shop_location),
                onBack = onBack,
            )
        }
        Box(Modifier.fillMaxSize()) {
            PlatformLocationMap(
                initialLocation = initialLocation,
                selectedLocation = selectedLocation,
                markerTitle = markerTitle,
                markerSubtitle = if (markerMoved) {
                    "${selectedLocation.latitude}, ${selectedLocation.longitude}"
                } else {
                    destination.subtitle
                },
                onLocationSelected = { selectedLocation = it },
                onMapLoaded = { isMapLoaded = true },
                modifier = Modifier.fillMaxSize(),
            )
            if (!isMapLoaded) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            ExtendedFloatingActionButton(
                onClick = { directionsLauncher.openDrivingDirections(selectedLocation) },
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.action_directions),
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(Res.string.open_driving_directions)) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(AppSpacing.Medium),
            )
        }
    }
}

private fun LocationData.isValid(): Boolean =
    latitude.isFinite() && latitude in -90.0..90.0 &&
        longitude.isFinite() && longitude in -180.0..180.0
