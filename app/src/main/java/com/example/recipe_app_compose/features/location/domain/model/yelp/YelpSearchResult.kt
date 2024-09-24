package com.example.recipe_app_compose.features.location.domain.model.yelp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/*
Returns Yelp response
 */
@Parcelize
data class YelpSearchResult(
    @SerializedName("total") val total: UInt,
    @SerializedName("businesses") val restaurants: List<YelpBusinesses>
) : Parcelable
