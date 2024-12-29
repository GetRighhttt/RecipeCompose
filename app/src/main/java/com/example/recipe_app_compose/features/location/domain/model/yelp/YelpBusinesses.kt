package com.example.recipe_app_compose.features.location.domain.model.yelp

import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class YelpBusinesses(
    val roomID: UInt? = null,
    val rating: Double,
    val phone: String?,
    val id: String,
    val alias: String,
    @SerializedName("is_closed") val isClosed: Boolean,
    val categories: List<YelpCategories>,
    @SerializedName("review_count") val reviewCount: UInt,
    val name: String,
    val url: String,
    val coordinates: YelpCoordinates,
    @SerializedName("image_url") val imageUrl: String?,
    val location: YelpLocations,
    val distance: Double // meters
) : Parcelable {

//    fun displayDistance(): String {
//        val milesPerMeter = 0.000621371
//        val distanceInMiles = "%.2f".format(distance * milesPerMeter)
//        return "$distanceInMiles miles"

    @Composable
    fun displayRating(): String {
        return rating.toInt().toString()
    }

    @Composable
    fun displayPhoneNumber(): String {
        return if (phone?.isNotBlank() == true) {
            "(${phone.drop(2).dropLast(7)}) ${phone.drop(5).dropLast(4)} - ${phone.drop(8)}"
        } else {
            ""
        }
    }
}

