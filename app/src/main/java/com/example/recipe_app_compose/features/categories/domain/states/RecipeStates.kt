package com.example.recipe_app_compose.features.categories.domain.states

import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient

data class RecipeState(
    val loading: Boolean = true,
    val list: List<com.example.recipe_app_compose.features.categories.domain.model.category.Category>? = emptyList(),
    val error: String? = null
)

data class CategoryMealState(
    val loading: Boolean = true,
    val list: List<com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMeal>? = emptyList(),
    val error: String? = null
)

data class RandomMealState(
    val loading: Boolean = true,
    val item: List<com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal>? = null,
    val error: String? = null
)

data class IngredientMealState(
    val loading: Boolean = true,
    val list: List<com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient>? = null,
    val error: String? = null
)