package com.example.recipe_app_compose.features.categories.domain.model.ingredient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String
) : Parcelable {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            strMeal,
            strMeal.substring(0..3),
            strMeal.substring(4..10),
            strMeal.lowercase(),
            strMeal.uppercase()
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

/*
"meals": [
    {
      "strMeal": "Brown Stew Chicken",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/sypxpx1515365095.jpg",
      "idMeal": "52940"
    },
 */