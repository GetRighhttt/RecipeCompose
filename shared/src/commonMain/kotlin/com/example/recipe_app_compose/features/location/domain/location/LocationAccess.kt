package com.example.recipe_app_compose.features.location.domain.location

import androidx.compose.runtime.Composable
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlinx.coroutines.flow.StateFlow

/**
 * The system-level location capability exposed to shared Nearby UI.
 *
 * Permission prompting and device location APIs remain platform work, while the
 * shared screen can react consistently to the resulting authorization state.
 */
interface LocationAccess {
    val authorization: StateFlow<LocationAuthorization>

    /** Requests foreground access only after the user explicitly asks for it. */
    fun requestWhenInUseAccess()

    /** Obtains a single current coordinate after foreground access is granted. */
    suspend fun currentLocation(): LocationData?

    /** Opens the application's system settings when the platform supports it. */
    fun openAppSettings()
}

enum class LocationAuthorization {
    NotDetermined,
    Granted,
    Denied,
    Restricted,
    ServicesDisabled,
}

/**
 * Creates the platform bridge from shared Compose UI. Android registers an
 * activity-result launcher; iOS retains a Core Location manager and delegate.
 */
@Composable
expect fun rememberLocationAccess(): LocationAccess
