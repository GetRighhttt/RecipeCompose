package com.example.recipe_app_compose.core.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.recipe_app_compose.core.onboarding.DataStoreOnboardingCompletionStore
import com.example.recipe_app_compose.core.onboarding.OnboardingCompletionStore
import com.example.recipe_app_compose.features.location.data.preferences.DataStoreLocationPreferenceStore
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.buildRandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.getRandomMealDatabaseBuilder
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

private val onboardingDataStore by lazy {
    createPreferenceDataStore("onboarding_preferences")
}
private val locationDataStore by lazy {
    createPreferenceDataStore("location_preferences")
}

/** iOS contributes file locations; common code owns preference keys and behavior. */
val iosPersistenceModule = module {
    single { buildRandomMealDatabase(getRandomMealDatabaseBuilder()) }
    single { get<RandomMealDatabase>().randomMealDao() }
    single<OnboardingCompletionStore> {
        DataStoreOnboardingCompletionStore(onboardingDataStore)
    }
    single<LocationPreferenceStore> {
        DataStoreLocationPreferenceStore(locationDataStore)
    }
}

private fun createPreferenceDataStore(name: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            "${NSHomeDirectory()}/Documents/$name.preferences_pb".toPath()
        },
    )
