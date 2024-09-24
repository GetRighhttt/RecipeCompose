package com.example.recipe_app_compose.features.location.domain.model.yelp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class YelpLocations(
    val city: String,
    val country: String,
    val address2: String,
    val address3: String,
    val state: String,
    val address1: String,
    @SerializedName("zip_code") val zipCode: String?
) : Serializable
