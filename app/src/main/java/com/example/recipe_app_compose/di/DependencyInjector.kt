package com.example.recipe_app_compose.di

import android.content.Context
import androidx.room.Room
import com.example.recipe_app_compose.features.categories.data.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.local.repoimpl.DatabaseRepoImpl
import com.example.recipe_app_compose.features.categories.data.remote.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepImpl

/*
Custom Dependency Injection Class created when the app is first created.
 */
object DependencyInjector {
    @Volatile
    private lateinit var database: RandomMealDatabase
    val databaseRepo by lazy { DatabaseRepoImpl(randomMealDAO = database.randomMealDao()) }
    val repository by lazy { RecipeRepositoryImpl() }
    val yelpRepo by lazy { YelpRepImpl() }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, RandomMealDatabase::class.java, "randomMeal.db")
            .fallbackToDestructiveMigration().build()
    }
}