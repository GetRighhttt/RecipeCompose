package com.example.recipe_app_compose.features.categories.data.datasources.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Uses the existing Android database path so saved recipes survive migration. */
fun getRandomMealDatabaseBuilder(
    context: Context,
): RoomDatabase.Builder<RandomMealDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath("randomMeal.db")
    return Room.databaseBuilder(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}
