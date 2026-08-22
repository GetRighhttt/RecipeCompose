package com.example.recipe_app_compose.di

import com.example.recipe_app_compose.BuildConfig
import com.example.recipe_app_compose.core.navigation.RecipeNavigationSelection
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.buildRandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.getRandomMealDatabaseBuilder
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.data.preferences.DataStoreLocationPreferenceStore
import com.example.recipe_app_compose.features.location.data.preferences.locationPreferenceDataStore
import com.example.recipe_app_compose.features.location.data.remote.YelpApiConfiguration
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Android-only construction details. The interfaces consumed by ViewModels are
 * already shared, so their iOS implementations can replace only these bindings
 * rather than reproducing ViewModel wiring.
 */
val androidAppModule = module {
    single { RecipeNavigationSelection() }
    single {
        buildRandomMealDatabase(getRandomMealDatabaseBuilder(androidContext()))
    }
    single { get<RandomMealDatabase>().randomMealDao() }

    single {
        YelpApiConfiguration(
            apiKey = BuildConfig.YELP_API_KEY,
            baseUrl = BuildConfig.YELP_BASE_URL,
        )
    }
    single<LocationPreferenceStore> {
        DataStoreLocationPreferenceStore(locationPreferenceDataStore(androidContext()))
    }

    viewModel { RecipeViewModel(get()) }
    viewModel { DatabaseViewModel(get()) }
}
