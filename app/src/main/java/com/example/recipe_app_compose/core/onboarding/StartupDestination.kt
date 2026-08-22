package com.example.recipe_app_compose.core.onboarding

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
