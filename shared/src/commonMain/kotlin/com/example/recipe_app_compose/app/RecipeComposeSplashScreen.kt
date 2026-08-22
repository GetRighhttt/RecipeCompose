package com.example.recipe_app_compose.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.app_name
import com.example.recipe_app_compose.shared.generated.resources.onboarding_discover
import com.example.recipe_app_compose.ui.theme.AppSpacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** A branded transition while the iOS host creates the shared Compose app. */
@Composable
fun RecipeComposeSplashScreen(onFinished: () -> Unit) {
    val entrance = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        entrance.animateTo(1f, animationSpec = tween(durationMillis = 650))
        delay(1_250)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(entrance.value).scale(0.92f + (entrance.value * 0.08f)),
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(AppSpacing.ExtraLarge * 3),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.onboarding_discover),
                        contentDescription = null,
                        modifier = Modifier.size(AppSpacing.ExtraLarge * 2),
                    )
                }
            }
            Spacer(Modifier.height(AppSpacing.Large))
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(AppSpacing.Large))
            CircularProgressIndicator(
                modifier = Modifier.size(AppSpacing.ExtraLarge),
                strokeWidth = AppSpacing.ExtraSmall,
            )
        }
    }
}
