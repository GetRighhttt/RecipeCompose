package com.example.recipe_app_compose.features.categories.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal

@Database(entities = [RandomMeal::class], version = 6, exportSchema = false)
abstract class RandomMealDatabase : RoomDatabase() {
    abstract fun randomMealDao(): RandomMealDAO
}