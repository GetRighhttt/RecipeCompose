package com.example.recipe_app_compose.features.location.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreference
import com.example.recipe_app_compose.features.location.domain.preferences.LocationPreferenceStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val LOCATION_DATA_STORE_NAME = "location_preferences"
private val Context.locationDataStore by preferencesDataStore(
    name = LOCATION_DATA_STORE_NAME,
)

class DataStoreLocationPreferenceStore(context: Context) : LocationPreferenceStore {
    private val dataStore = context.applicationContext.locationDataStore

    override val preference: Flow<LocationPreference> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
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
