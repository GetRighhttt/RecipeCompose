package com.example.recipe_app_compose.features.location.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    val latitude: Double,
    val longitude: Double
) : Parcelable
