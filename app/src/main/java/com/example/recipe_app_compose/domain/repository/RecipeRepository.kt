package com.example.recipe_app_compose.domain.repository

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.domain.model.categorymeal.CategoryMealResponse
import com.example.recipe_app_compose.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.domain.model.randommeal.RandomMealResponse

interface RecipeRepository {
    suspend fun getCategories(): Resource<CategoryResponse>
    suspend fun getCategoriesMeal(): Resource<CategoryMealResponse>
    suspend fun getRandomMeal(): Resource<RandomMealResponse>
    suspend fun getIngredient(ingredient: String): Resource<IngredientResponse>
}