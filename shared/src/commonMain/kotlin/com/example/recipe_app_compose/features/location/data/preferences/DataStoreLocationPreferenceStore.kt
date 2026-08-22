package com.example.recipe_app_compose.features.location.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Shared DataStore implementation; each platform supplies only the file location. */
class DataStoreLocationPreferenceStore(
    private val dataStore: DataStore<Preferences>,
) : LocationPreferenceStore {
    override val preference: Flow<LocationPreference> = dataStore.data.map { preferences ->
        preferences[LOCATION_PREFERENCE_KEY]
            ?.let { storedValue ->
                LocationPreference.entries.find { it.name == storedValue }
            }
            ?: LocationPreference.AskEveryTime
    }

    override suspend fun setPreference(preference: LocationPreference) {
        dataStore.edit { preferences ->
            preferences[LOCATION_PREFERENCE_KEY] = preference.name
        }
    }

    private companion object {
        val LOCATION_PREFERENCE_KEY = stringPreferencesKey("location_preference")
    }
}
