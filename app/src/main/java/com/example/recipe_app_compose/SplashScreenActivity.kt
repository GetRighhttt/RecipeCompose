package com.example.recipe_app_compose

import com.example.recipe_app_compose.ui.theme.AppTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.example.recipe_app_compose.core.onboarding.OnboardingPreferences
import com.example.recipe_app_compose.core.onboarding.StartupDestination
import com.example.recipe_app_compose.core.onboarding.resolveStartupDestination
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
                isSignedIn = Firebase.auth.currentUser != null,
            )
            setContent {
                AppTheme {
                    SplashScreen(onFinished = { openDestination(destination) })
                }
            }
        }
    }

    private fun openDestination(destination: StartupDestination) {
        val destinationActivity = when (destination) {
            StartupDestination.Onboarding -> OnboardingActivity::class.java
            StartupDestination.Login -> LoginActivity::class.java
            StartupDestination.Main -> MainActivity::class.java
        }
        startActivity(Intent(this, destinationActivity))
        finish()
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alpha = remember {
        Animatable(0F)
    }

    // Coroutine Launcher that initiates when composables are composed
    LaunchedEffect(key1 = true, block = {
        alpha.animateTo(1F, animationSpec = tween(1000))
        onFinished()
    })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.alpha(alpha = alpha.value),
            painter = painterResource(R.drawable.dining_two),
            contentDescription = stringResource(R.string.image)
        )
    }
}
