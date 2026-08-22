package com.example.recipe_app_compose.core.onboarding

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val ONBOARDING_DATA_STORE_NAME = "onboarding_preferences"
private val Context.onboardingDataStore by preferencesDataStore(
    name = ONBOARDING_DATA_STORE_NAME,
)

class OnboardingPreferences(context: Context) : OnboardingCompletionStore {
    private val dataStore = context.applicationContext.onboardingDataStore

    override suspend fun completedVersion(): Int = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
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
