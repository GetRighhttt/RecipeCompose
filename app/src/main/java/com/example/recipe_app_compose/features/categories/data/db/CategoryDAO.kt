package com.example.recipe_app_compose.features.categories.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: RandomMeal)

    @Query("SELECT * FROM random_meal_table")
    fun getAllMeals(): Flow<List<RandomMeal>>

    @Query("DELETE FROM random_meal_table")
    fun deleteAll()

    @Delete
    suspend fun deleteMeal(meal: RandomMeal)
}
