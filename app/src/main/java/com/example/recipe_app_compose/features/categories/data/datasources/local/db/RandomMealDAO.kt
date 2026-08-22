package com.example.recipe_app_compose.features.categories.data.datasources.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomMealDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: RandomMeal)

    @Query("SELECT EXISTS(SELECT 1 FROM random_meal_table WHERE meal_id = :mealId)")
    suspend fun containsMeal(mealId: String): Boolean

    @Transaction
    suspend fun insertMealIfAbsent(meal: RandomMeal) {
        val mealId = meal.idMeal
        if (mealId == null || !containsMeal(mealId)) {
            insertMeal(meal)
        }
    }

    @Query("SELECT * FROM random_meal_table")
    fun getAllMeals(): Flow<List<RandomMeal>>

    @Query("DELETE FROM random_meal_table")
    suspend fun deleteAllMeals()

    @Delete
    suspend fun deleteMeal(meal: RandomMeal)
}
