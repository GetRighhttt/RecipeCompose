package com.example.recipe_app_compose

import android.content.Context
import androidx.room.Room
import com.example.recipe_app_compose.features.categories.data.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.repoimpl.DatabaseRepoImpl

object DependencyInjector {

    lateinit var database: RandomMealDatabase

    val databaseRepo by lazy {
        DatabaseRepoImpl(randomMealDAO = database.randomMealDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, RandomMealDatabase::class.java, "randomMeal.db").build()
    }
}