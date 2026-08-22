package com.example.recipe_app_compose.features.categories.data.datasources.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Database(entities = [RandomMealEntity::class], version = 6)
@ConstructedBy(RandomMealDatabaseConstructor::class)
abstract class RandomMealDatabase : RoomDatabase() {
    abstract fun randomMealDao(): RandomMealDao
}

@Suppress("KotlinNoActualForExpect")
expect object RandomMealDatabaseConstructor : RoomDatabaseConstructor<RandomMealDatabase> {
    override fun initialize(): RandomMealDatabase
}

fun buildRandomMealDatabase(
    builder: RoomDatabase.Builder<RandomMealDatabase>,
): RandomMealDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.Default)
    .build()
