package com.example.recipe_app_compose.features.categories.data.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.local.db.RandomMealDAO
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepoImpl(private val randomMealDAO: RandomMealDAO) : DatabaseRepository {
    override suspend fun executeInsertMeal(meal: RandomMeal) = randomMealDAO.insertMeal(meal = meal)
    override suspend fun executeDeleteMeal(meal: RandomMeal) = randomMealDAO.deleteMeal(meal = meal)
    override suspend fun executeGetAllMeals(): Flow<List<RandomMeal>> = randomMealDAO.getAllMeals()
    override fun executeDeleteAll() = randomMealDAO.deleteAll()
}