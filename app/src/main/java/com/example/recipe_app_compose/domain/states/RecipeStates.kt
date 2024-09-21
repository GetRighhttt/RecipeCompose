package com.example.recipe_app_compose.domain.states

import com.example.recipe_app_compose.domain.model.Category
import com.example.recipe_app_compose.domain.model.RandomMeal
import com.example.recipe_app_compose.domain.model.CategoryMeal

data class RecipeState(
    val loading: Boolean = true,
    val list: List<Category>? = emptyList(),
    val error: String? = null
)

data class CategoryMealState(
    val loading: Boolean = true,
    val list: List<CategoryMeal>? = emptyList(),
    val error: String? = null
)

data class RandomMealState(
    val loading: Boolean = true,
    val item: List<RandomMeal>? = null,
    val error: String? = null
)