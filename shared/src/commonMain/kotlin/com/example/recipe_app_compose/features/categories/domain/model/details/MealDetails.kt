package com.example.recipe_app_compose.features.categories.domain.model.details

import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

/** Render-ready meal content shared by the featured, search, and saved flows. */
data class MealDetails(
    val name: String,
    val imageUrl: String,
    val category: String,
    val cuisine: String,
    val sourceUrl: String,
    val youtubeUrl: String,
    val instructions: String,
    val ingredients: List<String>,
)

fun RandomMeal.toMealDetails() = MealDetails(
    name = strMeal.orEmpty().trim(),
    imageUrl = strMealThumb.orEmpty().trim(),
    category = strCategory.orEmpty().trim(),
    cuisine = strArea.orEmpty().trim(),
    sourceUrl = strSource.orEmpty().trim(),
    youtubeUrl = strYoutube.orEmpty().trim(),
    instructions = strInstructions.orEmpty().trim(),
    ingredients = mealIngredients(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9,
    ),
)

fun Ingredient.toMealDetails() = MealDetails(
    name = strMeal.orEmpty().trim(),
    imageUrl = strMealThumb.orEmpty().trim(),
    category = strCategory.orEmpty().trim(),
    cuisine = strArea.orEmpty().trim(),
    sourceUrl = strSource.orEmpty().trim(),
    youtubeUrl = strYoutube.orEmpty().trim(),
    instructions = strInstructions.orEmpty().trim(),
    ingredients = mealIngredients(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9,
    ),
)

fun List<RandomMeal>.containsSavedMeal(mealId: String?): Boolean {
    val normalizedMealId = mealId?.trim().takeUnless { it.isNullOrEmpty() } ?: return false
    return any { savedMeal -> savedMeal.idMeal?.trim() == normalizedMealId }
}

private fun mealIngredients(vararg values: String?): List<String> = values
    .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
    .distinct()
