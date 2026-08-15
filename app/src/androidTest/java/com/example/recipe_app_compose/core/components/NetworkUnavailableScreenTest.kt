package com.example.recipe_app_compose.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NetworkUnavailableScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun offlineActionsInvokeTheirCallbacks() {
        var retryInvoked = false
        var networkSettingsInvoked = false

        composeTestRule.setContent {
            MaterialTheme {
                NetworkUnavailableScreen(
                    onRetry = { retryInvoked = true },
                    onOpenNetworkSettings = { networkSettingsInvoked = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Try again").performClick()
        composeTestRule.onNodeWithText("Network settings").performClick()

        assertTrue(retryInvoked)
        assertTrue(networkSettingsInvoked)
    }
}
