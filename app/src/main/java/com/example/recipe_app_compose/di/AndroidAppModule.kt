package com.example.recipe_app_compose.di

import androidx.room.Room
import com.example.recipe_app_compose.core.navigation.RecipeNavigationSelection
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl.DatabaseRepositoryImpl
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.DatabaseViewModel
import com.example.recipe_app_compose.features.categories.presentation.viewmodel.RecipeViewModel
import com.example.recipe_app_compose.features.location.data.location.AndroidCurrentLocationProvider
import com.example.recipe_app_compose.features.location.data.preferences.DataStoreLocationPreferenceStore
import com.example.recipe_app_compose.features.location.data.repoimpl.YelpRepositoryImpl
import com.example.recipe_app_compose.features.location.domain.location.CurrentLocationProvider
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import com.example.recipe_app_compose.features.location.presentation.viewmodel.YelpViewModel
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
        Room.databaseBuilder(
            androidContext(),
            RandomMealDatabase::class.java,
            "randomMeal.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<RandomMealDatabase>().randomMealDao() }

    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }
    single<YelpRepository> { YelpRepositoryImpl() }
    single<CurrentLocationProvider> { AndroidCurrentLocationProvider(androidContext()) }
    single<LocationPreferenceStore> { DataStoreLocationPreferenceStore(androidContext()) }

    viewModel { RecipeViewModel(get()) }
    viewModel { DatabaseViewModel(get()) }
    viewModel { YelpViewModel(get(), get(), get()) }
}
