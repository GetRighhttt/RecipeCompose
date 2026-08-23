package com.example.recipe_app_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.recipe_app_compose.features.onboarding.presentation.OnboardingScreen
import com.example.recipe_app_compose.ui.theme.AppTheme

class OnboardingActivity : ComponentActivity() {
    private var isStartingHandoff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                OnboardingScreen(onFinished = ::startOnboardingHandoff)
            }
        }
    }

    private fun startOnboardingHandoff() {
        if (isStartingHandoff) return
        isStartingHandoff = true

        startActivity(
            Intent(this, MainActivity::class.java).putExtra(
                MainActivity.EXTRA_SHOW_ONBOARDING_COMPLETION,
                true,
            )
        )
        finish()
    }
}
