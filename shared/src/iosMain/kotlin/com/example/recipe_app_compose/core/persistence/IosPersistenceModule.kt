package com.example.recipe_app_compose.core.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.recipe_app_compose.core.onboarding.DataStoreOnboardingCompletionStore
import com.example.recipe_app_compose.core.onboarding.OnboardingCompletionStore
import com.example.recipe_app_compose.features.location.data.preferences.DataStoreLocationPreferenceStore
import com.example.recipe_app_compose.features.location.data.remote.YelpApiConfiguration
import com.example.recipe_app_compose.features.location.data.remote.localIosYelpApiKey
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.buildRandomMealDatabase
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.getRandomMealDatabaseBuilder
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSBundle

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
    single {
        YelpApiConfiguration(
            apiKey = infoPlistValue("YELP_API_KEY").ifBlank { localIosYelpApiKey },
            baseUrl = infoPlistValue("YELP_BASE_URL")
                .ifBlank { "https://api.yelp.com/v3/" },
        )
    }
}

private fun createPreferenceDataStore(name: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            "${NSHomeDirectory()}/Documents/$name.preferences_pb".toPath()
        },
    )

private fun infoPlistValue(key: String): String =
    (NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String)
        ?.takeUnless { it.startsWith("$(") && it.endsWith(')') }
        .orEmpty()
