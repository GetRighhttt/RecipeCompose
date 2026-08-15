@file:OptIn(InternalCoroutinesApi::class)

package com.example.recipe_app_compose.di

import android.content.Context
import androidx.room.Room
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl.DatabaseRepoImpl
import com.example.recipe_app_compose.features.categories.data.datasources.remote.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.features.location.data.location.AndroidCurrentLocationProvider
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepImpl
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

/*
Custom Dependency Injection Class created when the app is first created.
 */
object DependencyInjector {
    @Volatile
    private lateinit var database: RandomMealDatabase
    private lateinit var locationProvider: CurrentLocationProvider
    val databaseRepo by lazy { DatabaseRepoImpl(randomMealDAO = database.randomMealDao()) }
    val repository by lazy { RecipeRepositoryImpl() }
    val yelpRepo by lazy { YelpRepImpl() }
    val currentLocationProvider: CurrentLocationProvider
        get() = locationProvider

    @OptIn(InternalCoroutinesApi::class)
    fun provide(context: Context) {
        synchronized(this) {
            locationProvider = AndroidCurrentLocationProvider(context)
            database =
                Room.databaseBuilder(context, RandomMealDatabase::class.java, "randomMeal.db")
                    .fallbackToDestructiveMigration().build()
        }
    }
}
