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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    private val onboardingPreferences by lazy { OnboardingPreferences(this) }
    private val auth = Firebase.auth
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
            val destination = if (auth.currentUser == null) {
                LoginActivity::class.java
            } else {
                MainActivity::class.java
            }
            startActivity(
                Intent(this@OnboardingActivity, destination).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            finish()
        }
    }
}
