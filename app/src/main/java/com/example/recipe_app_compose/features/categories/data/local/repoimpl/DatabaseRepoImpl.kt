package com.example.recipe_app_compose.features.categories.data.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.local.db.RandomMealDAO
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepoImpl(private val randomMealDAO: RandomMealDAO) : DatabaseRepository {
    /*
    We are not passing in a resource state here because the methods are executed locally on the
    device, so there is no http response that we need to check for. We do update UI state in the
    view model however.
     */
    override suspend fun executeInsertMeal(meal: RandomMeal) = randomMealDAO.insertMeal(meal = meal)
    override suspend fun executeDeleteMeal(meal: RandomMeal) = randomMealDAO.deleteMeal(meal = meal)
    override suspend fun executeGetAllMeals(): Flow<List<RandomMeal>> = randomMealDAO.getAllMeals()
    override fun executeDeleteAll() = randomMealDAO.deleteAll()
}