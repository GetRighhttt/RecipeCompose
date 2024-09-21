package com.example.recipe_app_compose.domain.repository

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.domain.model.CategoryResponse
import com.example.recipe_app_compose.domain.model.RandomMealResponse
import com.example.recipe_app_compose.domain.model.SeafoodCategoryResponse

interface RecipeRepository {
    suspend fun getCategories(): Resource<CategoryResponse>
    suspend fun getSeafoodCategories(): Resource<SeafoodCategoryResponse>
    suspend fun getRandomMeal(): Resource<RandomMealResponse>
}