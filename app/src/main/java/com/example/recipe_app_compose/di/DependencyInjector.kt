package com.example.recipe_app_compose.di

import android.content.Context
import androidx.room.Room
import com.example.recipe_app_compose.features.categories.data.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.local.repoimpl.DatabaseRepoImpl
import com.example.recipe_app_compose.features.categories.data.remote.repoimpl.RecipeRepositoryImpl

/*
Custom Dependency Injection Class created when the app is first created.
 */
object DependencyInjector {
    private lateinit var database: RandomMealDatabase
    val databaseRepo by lazy {
        DatabaseRepoImpl(randomMealDAO = database.randomMealDao())
    }
    val repository by lazy {
        RecipeRepositoryImpl()
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, RandomMealDatabase::class.java, "randomMeal.db")
            .fallbackToDestructiveMigration().build()


    }
}