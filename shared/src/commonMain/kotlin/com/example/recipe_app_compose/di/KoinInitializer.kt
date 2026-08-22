package com.example.recipe_app_compose.di

import com.example.recipe_app_compose.features.categories.data.remote.RecipeRepositoryImpl
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository
import com.example.recipe_app_compose.features.categories.presentation.RecipeStore
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val sharedAppModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl() }
    factory { RecipeStore(get()) }
}

/**
 * Starts the shared dependency container. Each host contributes only its own
 * platform bindings through [appDeclaration]; shared bindings are added here as
 * they move from the Android application module.
 */
fun initKoin(
    appDeclaration: KoinApplication.() -> Unit = {},
): KoinApplication = startKoin {
    modules(sharedAppModule)
    appDeclaration()
}
