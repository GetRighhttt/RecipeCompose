package com.example.recipe_app_compose.domain.repository

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.domain.model.randommeal.RandomMealResponse
import com.example.recipe_app_compose.domain.model.categorymeal.CategoryMealResponse

interface RecipeRepository {
    suspend fun getCategories(): Resource<CategoryResponse>
    suspend fun getCategoriesMeal(): Resource<CategoryMealResponse>
    suspend fun getRandomMeal(): Resource<RandomMealResponse>
}