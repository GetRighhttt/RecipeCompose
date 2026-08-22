package com.example.recipe_app_compose.core.onboarding

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val ONBOARDING_DATA_STORE_NAME = "onboarding_preferences"
private val Context.onboardingDataStore by preferencesDataStore(
    name = ONBOARDING_DATA_STORE_NAME,
)

class OnboardingPreferences(context: Context) : OnboardingCompletionStore by
    DataStoreOnboardingCompletionStore(context.applicationContext.onboardingDataStore)
