package com.example.recipe_app_compose.core.navigation

import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

/**
 * In-memory handoff for Android's legacy navigation graph.
 *
 * The old graph stored Parcelable domain models in a SavedStateHandle. Shared
 * models intentionally are not Android Parcelables, so this bridge preserves
 * the existing routes until the Compose Multiplatform navigation graph moves.
 */
class RecipeNavigationSelection {
    var category: Category? = null
    var ingredient: Ingredient? = null
    var savedMeal: RandomMeal? = null
}
