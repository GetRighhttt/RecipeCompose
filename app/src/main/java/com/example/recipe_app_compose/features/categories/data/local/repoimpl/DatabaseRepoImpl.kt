package com.example.recipe_app_compose.features.categories.data.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.local.db.RandomMealDAO
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DatabaseRepoImpl(private val randomMealDAO: RandomMealDAO) : DatabaseRepository {
    override suspend fun executeInsertMeal(meal: RandomMeal) = withContext(Dispatchers.IO) {
        randomMealDAO.insertMeal(meal = meal)
    }

    override suspend fun executeDeleteMeal(meal: RandomMeal) = withContext(Dispatchers.IO) {
        randomMealDAO.deleteMeal(meal = meal)
    }

    override suspend fun executeGetAllMeals(): Flow<List<RandomMeal>> =
        withContext(Dispatchers.IO) {
            randomMealDAO.getAllMeals()
        }

    override fun executeDeleteAll() = randomMealDAO.deleteAll()
}