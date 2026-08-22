package com.example.recipe_app_compose.features.location.domain.preferences

import kotlinx.coroutines.flow.Flow

interface LocationPreferenceStore {
    val preference: Flow<LocationPreference>

    suspend fun setPreference(preference: LocationPreference)
}
