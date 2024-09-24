package com.example.recipe_app_compose.features.location.domain.model.yelp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YelpCategories(
    val alias: String,
    val title: String
) : Parcelable