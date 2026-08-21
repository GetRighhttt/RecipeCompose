package com.example.recipe_app_compose.features.categories.domain.states

import com.example.recipe_app_compose.features.categories.domain.model.category.Category
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.Ingredient
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

data class UiState(
    val loading: Boolean = true,
    val list: List<Category>? = emptyList(),
    val error: String? = null
)

data class RandomMealUiState(
    val loading: Boolean = true,
    val item: List<RandomMeal>? = null,
    val error: String? = null
)

data class IngredientUiState(
    val loading: Boolean = true,
    val list: List<Ingredient>? = null,
    val error: String? = null
)
