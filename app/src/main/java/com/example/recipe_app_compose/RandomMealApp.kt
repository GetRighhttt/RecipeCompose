package com.example.recipe_app_compose

import android.app.Application
import com.example.recipe_app_compose.di.androidAppModule
import com.example.recipe_app_compose.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/** Starts the Android Koin graph before any Compose ViewModel is requested. */
class RandomMealApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@RandomMealApp)
            modules(androidAppModule)
        }
    }
}
