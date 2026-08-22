package com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDao
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.toDomain
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.toEntity
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseRepositoryImpl(
    private val randomMealDao: RandomMealDao,
) : DatabaseRepository {
    override suspend fun saveMeal(meal: RandomMeal) {
        randomMealDao.insertMealIfAbsent(meal.toEntity())
    }

    override fun getMeals(): Flow<List<RandomMeal>> = randomMealDao.getAllMeals().map { meals ->
        meals.map { it.toDomain() }
    }

    override suspend fun deleteAllMeals() {
        randomMealDao.deleteAllMeals()
    }

    override suspend fun deleteMeal(meal: RandomMeal) {
        meal.idMeal?.let { randomMealDao.deleteMealById(it) }
    }
}
