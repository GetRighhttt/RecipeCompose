package com.example.recipe_app_compose.features.categories.domain.repository

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse

/** Platform-neutral gateway for MealDB recipe data. */
interface RecipeRepository {
    suspend fun getCategories(): Resource<CategoryResponse>
    suspend fun getRandomMeal(): Resource<RandomMealResponse>
    suspend fun getIngredient(ingredient: String): Resource<IngredientResponse>
}
