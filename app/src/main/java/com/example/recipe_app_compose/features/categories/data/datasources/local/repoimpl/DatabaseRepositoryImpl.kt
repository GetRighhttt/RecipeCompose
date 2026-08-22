package com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDAO
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepositoryImpl(
    private val randomMealDao: RandomMealDAO,
) : DatabaseRepository {
    override suspend fun saveMeal(meal: RandomMeal) {
        randomMealDao.insertMealIfAbsent(meal)
    }

    override suspend fun deleteMeal(meal: RandomMeal) {
        randomMealDao.deleteMeal(meal)
    }

    override fun getMeals(): Flow<List<RandomMeal>> = randomMealDao.getAllMeals()

    override suspend fun deleteAllMeals() {
        randomMealDao.deleteAllMeals()
    }
}
