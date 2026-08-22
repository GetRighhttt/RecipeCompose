package com.example.recipe_app_compose.core.onboarding

/**
 * Persists onboarding completion without exposing an Android Context or a
 * platform-specific file location to shared startup logic.
 */
interface OnboardingCompletionStore {
    suspend fun completedVersion(): Int

    suspend fun markCompleted(version: Int = CURRENT_ONBOARDING_VERSION)
}
