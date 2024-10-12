package com.example.recipe_app_compose

import android.app.Application
import com.example.recipe_app_compose.di.DependencyInjector

/*
Dependency Injection instantiated when class is first created.
 */
class RandomMealApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyInjector.provide(this)
    }
}