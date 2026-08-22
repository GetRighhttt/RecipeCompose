package com.example.recipe_app_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.recipe_app_compose.core.onboarding.OnboardingPreferences
import com.example.recipe_app_compose.features.onboarding.presentation.OnboardingScreen
import com.example.recipe_app_compose.ui.theme.AppTheme
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    private val onboardingPreferences by lazy { OnboardingPreferences(this) }
    private var isCompleting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                OnboardingScreen(onFinished = ::completeOnboarding)
            }
        }
    }

    private fun completeOnboarding() {
        if (isCompleting) return
        isCompleting = true

        lifecycleScope.launch {
            onboardingPreferences.markCompleted()
            startActivity(
                Intent(this@OnboardingActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            finish()
        }
    }
}
