package com.example.recipe_app_compose.app

import androidx.compose.ui.window.ComposeUIViewController
import com.example.recipe_app_compose.features.onboarding.presentation.OnboardingScreen
import com.example.recipe_app_compose.ui.theme.AppTheme
import platform.UIKit.UIViewController

/**
 * Native iOS entry point into shared Compose UI.
 *
 * Swift owns the application lifecycle and places this controller in its view
 * hierarchy. The completion callback intentionally remains empty until the
 * shared application shell becomes the next migrated destination.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    AppTheme {
        OnboardingScreen(onFinished = {})
    }
}
