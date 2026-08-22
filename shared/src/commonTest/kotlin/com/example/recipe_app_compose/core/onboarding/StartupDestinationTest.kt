package com.example.recipe_app_compose.core.onboarding

import kotlin.test.Test
import kotlin.test.assertEquals

class StartupDestinationTest {
    @Test
    fun `incomplete onboarding opens onboarding`() {
        assertEquals(
            StartupDestination.Onboarding,
            resolveStartupDestination(
                completedOnboardingVersion = 0,
                currentOnboardingVersion = 1,
            ),
        )
    }

    @Test
    fun `completed onboarding opens main`() {
        assertEquals(
            StartupDestination.Main,
            resolveStartupDestination(
                completedOnboardingVersion = 1,
                currentOnboardingVersion = 1,
            ),
        )
    }

    @Test
    fun `new onboarding version requires onboarding again`() {
        assertEquals(
            StartupDestination.Onboarding,
            resolveStartupDestination(
                completedOnboardingVersion = 1,
                currentOnboardingVersion = 2,
            ),
        )
    }
}
