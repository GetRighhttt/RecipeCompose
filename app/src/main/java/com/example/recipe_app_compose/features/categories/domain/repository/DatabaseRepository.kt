package com.example.recipe_app_compose.features.categories.domain.repository

import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun executeInsertMeal(meal: RandomMeal)
    suspend fun executeGetAllMeals(): Flow<List<RandomMeal>>
    fun executeDeleteAll()
    suspend fun executeDeleteMeal(meal: RandomMeal)
}