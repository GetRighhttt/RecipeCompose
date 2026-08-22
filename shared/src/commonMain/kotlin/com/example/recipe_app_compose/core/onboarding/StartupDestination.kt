package com.example.recipe_app_compose.core.onboarding

/**
 * Platform-neutral startup decision. Android activities and the iOS host each
 * decide how to display the destination, while this common policy owns when
 * onboarding must be shown again after its version changes.
 */
enum class StartupDestination {
    Onboarding,
    Main,
}

fun resolveStartupDestination(
    completedOnboardingVersion: Int,
    currentOnboardingVersion: Int = CURRENT_ONBOARDING_VERSION,
): StartupDestination = when {
    completedOnboardingVersion < currentOnboardingVersion -> StartupDestination.Onboarding
    else -> StartupDestination.Main
}

const val CURRENT_ONBOARDING_VERSION = 1
