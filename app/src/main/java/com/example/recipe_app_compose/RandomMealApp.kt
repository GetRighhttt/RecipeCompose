package com.example.recipe_app_compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.recipe_app_compose.di.androidAppModule
import com.example.recipe_app_compose.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/** Starts the Android Koin graph before any Compose ViewModel is requested. */
class RandomMealApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@RandomMealApp)
            modules(androidAppModule)
        }
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(IMAGE_CROSSFADE_MILLIS)
        .build()

    private companion object {
        const val IMAGE_CROSSFADE_MILLIS = 200
    }
}
