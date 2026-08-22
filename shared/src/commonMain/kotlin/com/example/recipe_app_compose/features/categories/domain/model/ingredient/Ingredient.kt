package com.example.recipe_app_compose.features.categories.domain.model.ingredient

import kotlinx.serialization.Serializable

/** Search result and full recipe data returned by MealDB. */
@Serializable
data class Ingredient(
    val idMeal: String? = null,
    val strMeal: String? = null,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
    val strYoutube: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strSource: String? = null,
) {
    fun doesMatchSearchQuery(query: String): Boolean =
        query.trim().let { normalizedQuery ->
            normalizedQuery.isNotEmpty() && strMeal?.contains(normalizedQuery, ignoreCase = true) == true
        }
}
