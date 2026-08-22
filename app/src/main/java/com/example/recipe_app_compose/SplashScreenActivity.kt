package com.example.recipe_app_compose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.recipe_app_compose.app.RecipeComposeSplashScreen
import com.example.recipe_app_compose.core.onboarding.OnboardingPreferences
import com.example.recipe_app_compose.core.onboarding.StartupDestination
import com.example.recipe_app_compose.core.onboarding.resolveStartupDestination
import com.example.recipe_app_compose.ui.theme.AppTheme
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val destination = resolveStartupDestination(
                completedOnboardingVersion = OnboardingPreferences(this@SplashScreenActivity)
                    .completedVersion(),
            )
            setContent {
                AppTheme {
                    RecipeComposeSplashScreen(onFinished = { openDestination(destination) })
                }
            }
        }
    }

    private fun openDestination(destination: StartupDestination) {
        val destinationActivity = when (destination) {
            StartupDestination.Onboarding -> OnboardingActivity::class.java
            StartupDestination.Main -> MainActivity::class.java
        }
        startActivity(Intent(this, destinationActivity))
        finish()
    }
}
