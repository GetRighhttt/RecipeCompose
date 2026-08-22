package com.example.recipe_app_compose.features.onboarding.presentation

import androidx.compose.runtime.Composable

/**
 * Onboarding is the iOS root, so there is no parent destination for a system
 * back event. HorizontalPager still provides direct page navigation by swipe.
 */
@Composable
internal actual fun OnboardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
