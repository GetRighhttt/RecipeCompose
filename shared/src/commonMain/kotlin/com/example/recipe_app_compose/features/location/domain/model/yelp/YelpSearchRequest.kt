package com.example.recipe_app_compose.features.location.domain.model.yelp

import com.example.recipe_app_compose.features.location.domain.model.location.LocationData

/** Input to a restaurant search, independent of the HTTP client used to execute it. */
data class YelpSearchRequest(
    val term: String,
    val origin: YelpSearchOrigin,
    val radiusMeters: Int? = null,
    val limit: UInt = 50U,
    val offset: UInt = 0U,
)

sealed interface YelpSearchOrigin {
    data class Coordinates(val location: LocationData) : YelpSearchOrigin

    data class NamedLocation(val value: String) : YelpSearchOrigin
}
