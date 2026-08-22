package com.example.recipe_app_compose.features.onboarding.presentation

import androidx.compose.runtime.Composable

/**
 * Platform edge for system back behavior.
 *
 * Pager state and the decision to move to the previous page remain shared. Only
 * subscribing to a platform back event requires an Android/iOS implementation.
 */
@Composable
internal expect fun OnboardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
