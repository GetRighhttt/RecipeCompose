package com.example.recipe_app_compose.core.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Shared DataStore implementation; each platform supplies only the file location. */
class DataStoreOnboardingCompletionStore(
    private val dataStore: DataStore<Preferences>,
) : OnboardingCompletionStore {
    override suspend fun completedVersion(): Int = dataStore.data
        .map { preferences -> preferences[COMPLETED_VERSION_KEY] ?: 0 }
        .first()

    override suspend fun markCompleted(version: Int) {
        dataStore.edit { preferences ->
            preferences[COMPLETED_VERSION_KEY] = version
        }
    }

    private companion object {
        val COMPLETED_VERSION_KEY = intPreferencesKey("completed_onboarding_version")
    }
}
