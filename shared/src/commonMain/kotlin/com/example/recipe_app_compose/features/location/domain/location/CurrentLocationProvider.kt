package com.example.recipe_app_compose.features.location.domain.location

import com.example.recipe_app_compose.features.location.domain.model.location.LocationData

/**
 * Platform adapter for a user-approved location lookup. Implementations own
 * permission checks and device APIs; shared state only receives a coordinate
 * or a null result.
 */
fun interface CurrentLocationProvider {
    suspend fun getCurrentLocation(): LocationData?
}
