package com.example.recipe_app_compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.recipe_app_compose.di.DependencyInjector

/*
Dependency Injection instantiated when class is first created.
 */
class RandomMealApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        DependencyInjector.provide(this)
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(IMAGE_CROSSFADE_MILLIS)
        .build()

    private companion object {
        const val IMAGE_CROSSFADE_MILLIS = 200
    }
}
