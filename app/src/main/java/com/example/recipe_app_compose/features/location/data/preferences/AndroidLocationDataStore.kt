package com.example.recipe_app_compose.features.location.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val LOCATION_DATA_STORE_NAME = "location_preferences"
private val Context.locationDataStore by preferencesDataStore(
    name = LOCATION_DATA_STORE_NAME,
)

/** Keeps the existing Android DataStore file while shared code owns its schema and behavior. */
fun locationPreferenceDataStore(context: Context): DataStore<Preferences> =
    context.applicationContext.locationDataStore
