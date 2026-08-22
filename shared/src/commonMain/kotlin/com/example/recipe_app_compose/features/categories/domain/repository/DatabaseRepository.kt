package com.example.recipe_app_compose.features.categories.domain.repository

import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun saveMeal(meal: RandomMeal)
    fun getMeals(): Flow<List<RandomMeal>>
    suspend fun deleteAllMeals()
    suspend fun deleteMeal(meal: RandomMeal)
}
