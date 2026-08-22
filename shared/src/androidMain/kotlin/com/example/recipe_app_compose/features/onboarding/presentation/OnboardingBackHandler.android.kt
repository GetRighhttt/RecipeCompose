package com.example.recipe_app_compose.features.onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/** Routes Android system-back events into the shared pager state machine. */
@Composable
internal actual fun OnboardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
