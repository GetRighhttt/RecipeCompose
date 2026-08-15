package com.example.recipe_app_compose.features.location.domain.model.yelp

import android.os.Parcelable
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

    fun displayRating(): String = rating.toString().removeSuffix(".0")

    fun displayPhoneNumber(): String {
        val originalPhone = phone.orEmpty()
        val digits = originalPhone.filter(Char::isDigit)
        val nationalNumber = when {
            digits.length == 11 && digits.startsWith('1') -> digits.drop(1)
            digits.length == 10 -> digits
            else -> return originalPhone
        }

        return "(${nationalNumber.take(3)}) " +
            "${nationalNumber.substring(3, 6)}-${nationalNumber.takeLast(4)}"
    }
}
