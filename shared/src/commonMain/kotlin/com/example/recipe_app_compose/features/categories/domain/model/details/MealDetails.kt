package com.example.recipe_app_compose.features.categories.domain.model.details

import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

/** Render-ready meal content shared by the featured, search, and saved flows. */
data class MealDetails(
    val id: String,
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
    id = idMeal.orEmpty().trim(),
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
    id = idMeal.orEmpty().trim(),
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

fun MealDetails.toRandomMeal() = RandomMeal(
    idMeal = id.takeIf(String::isNotBlank),
    strMeal = name,
    strCategory = category,
    strArea = cuisine,
    strInstructions = instructions,
    strMealThumb = imageUrl,
    strYoutube = youtubeUrl,
    strIngredient1 = ingredients.getOrNull(0),
    strIngredient2 = ingredients.getOrNull(1),
    strIngredient3 = ingredients.getOrNull(2),
    strIngredient4 = ingredients.getOrNull(3),
    strIngredient5 = ingredients.getOrNull(4),
    strIngredient6 = ingredients.getOrNull(5),
    strIngredient7 = ingredients.getOrNull(6),
    strIngredient8 = ingredients.getOrNull(7),
    strIngredient9 = ingredients.getOrNull(8),
    strSource = sourceUrl,
)

fun List<RandomMeal>.containsSavedMeal(mealId: String?): Boolean {
    val normalizedMealId = mealId?.trim().takeUnless { it.isNullOrEmpty() } ?: return false
    return any { savedMeal -> savedMeal.idMeal?.trim() == normalizedMealId }
}

private fun mealIngredients(vararg values: String?): List<String> = values
    .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
    .distinct()
