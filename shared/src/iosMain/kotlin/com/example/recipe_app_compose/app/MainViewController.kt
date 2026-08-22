package com.example.recipe_app_compose.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.example.recipe_app_compose.core.persistence.iosPersistenceModule
import com.example.recipe_app_compose.ui.theme.AppTheme
import platform.UIKit.UIViewController

/**
 * Native iOS entry point into shared Compose UI.
 *
 * Swift owns the application lifecycle and places this controller in its view
 * hierarchy. The shared shell owns onboarding and the migrated recipe flow.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    // SwiftUI supplies a full-screen host; Compose owns the one safe-area inset
    // shared by onboarding, primary destinations, and details.
    var showSplash by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        if (showSplash) {
            AppTheme {
                RecipeComposeSplashScreen(onFinished = { showSplash = false })
            }
        } else {
            RecipeComposeApp(iosPersistenceModule)
        }
    }
}
