package com.example.recipe_app_compose.features.categories.domain.model.ingredient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val idMeal: String?,
    val strMeal: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strYoutube: String?,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strSource: String?
) : Parcelable {
    fun doesMatchSearchQuery(query: String): Boolean {
        val normalizedQuery = query.trim()
        return normalizedQuery.isNotEmpty() &&
            strMeal?.contains(normalizedQuery, ignoreCase = true) == true
    }
}
