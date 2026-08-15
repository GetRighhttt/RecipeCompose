package com.example.recipe_app_compose.features.location.domain.location

import com.example.recipe_app_compose.features.location.domain.model.location.LocationData

fun interface CurrentLocationProvider {
    suspend fun getCurrentLocation(): LocationData?
}
