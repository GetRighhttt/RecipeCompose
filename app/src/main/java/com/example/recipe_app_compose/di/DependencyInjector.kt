package com.example.recipe_app_compose.di

import android.content.Context
import androidx.room.Room
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl.DatabaseRepositoryImpl
import com.example.recipe_app_compose.features.categories.data.datasources.remote.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.features.location.data.location.AndroidCurrentLocationProvider
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepositoryImpl
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider

object DependencyInjector {
    @Volatile
    private lateinit var database: RandomMealDatabase

    @Volatile
    private lateinit var locationProvider: CurrentLocationProvider

    @Volatile
    private var isInitialized = false

    val databaseRepository by lazy {
        DatabaseRepositoryImpl(randomMealDao = database.randomMealDao())
    }
    val recipeRepository by lazy { RecipeRepositoryImpl() }
    val yelpRepository by lazy { YelpRepositoryImpl() }
    val currentLocationProvider: CurrentLocationProvider
        get() = locationProvider

    fun provide(context: Context) {
        if (isInitialized) return

        synchronized(this) {
            if (isInitialized) return

            val applicationContext = context.applicationContext
            locationProvider = AndroidCurrentLocationProvider(applicationContext)
            database = Room.databaseBuilder(
                applicationContext,
                RandomMealDatabase::class.java,
                "randomMeal.db",
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            isInitialized = true
        }
    }
}
