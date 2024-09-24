package com.example.recipe_app_compose.features.location.domain.model.yelp

import java.io.Serializable

data class YelpCoordinates(
    val latitude: Double,
    val longitude: Double
) : Serializable
