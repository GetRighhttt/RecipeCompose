package com.example.recipe_app_compose.features.categories.data.datasources.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: RandomMealEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM random_meal_table WHERE meal_id = :mealId)")
    suspend fun containsMeal(mealId: String): Boolean

    @Transaction
    suspend fun insertMealIfAbsent(meal: RandomMealEntity) {
        val mealId = meal.idMeal
        if (mealId == null || !containsMeal(mealId)) {
            insertMeal(meal)
        }
    }

    @Query("SELECT * FROM random_meal_table")
    fun getAllMeals(): Flow<List<RandomMealEntity>>

    @Query("DELETE FROM random_meal_table")
    suspend fun deleteAllMeals()

    @Query("DELETE FROM random_meal_table WHERE meal_id = :mealId")
    suspend fun deleteMealById(mealId: String)
}
