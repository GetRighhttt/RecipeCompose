package com.example.recipe_app_compose.features.categories.domain.repository

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse

interface RecipeRepository {
    suspend fun getCategories(): Resource<com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse>
    suspend fun getCategoriesMeal(): Resource<com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMealResponse>
    suspend fun getRandomMeal(): Resource<RandomMealResponse>
    suspend fun getIngredient(ingredient: String): Resource<com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse>
}