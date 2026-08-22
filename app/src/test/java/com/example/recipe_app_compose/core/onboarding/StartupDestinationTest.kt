package com.example.recipe_app_compose.core.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class StartupDestinationTest {
    @Test
    fun `incomplete onboarding opens onboarding before authentication`() {
        assertEquals(
            StartupDestination.Onboarding,
            resolveStartupDestination(
                completedOnboardingVersion = 0,
                currentOnboardingVersion = 1,
                isSignedIn = true,
            ),
        )
    }

    @Test
    fun `completed onboarding opens main for signed in user`() {
        assertEquals(
            StartupDestination.Main,
            resolveStartupDestination(
                completedOnboardingVersion = 1,
                currentOnboardingVersion = 1,
                isSignedIn = true,
            ),
        )
    }

    @Test
    fun `completed onboarding opens login for signed out user`() {
        assertEquals(
            StartupDestination.Login,
            resolveStartupDestination(
                completedOnboardingVersion = 1,
                currentOnboardingVersion = 1,
                isSignedIn = false,
            ),
        )
    }
}
