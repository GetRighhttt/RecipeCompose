package com.example.recipe_app_compose.core.onboarding

enum class StartupDestination {
    Onboarding,
    Login,
    Main,
}

fun resolveStartupDestination(
    completedOnboardingVersion: Int,
    currentOnboardingVersion: Int = CURRENT_ONBOARDING_VERSION,
    isSignedIn: Boolean,
): StartupDestination = when {
    completedOnboardingVersion < currentOnboardingVersion -> StartupDestination.Onboarding
    isSignedIn -> StartupDestination.Main
    else -> StartupDestination.Login
}
