package com.example.recipe_app_compose.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.example.recipe_app_compose.core.animation.animateWithFrameTime
import com.example.recipe_app_compose.core.components.AppLoadingIndicator
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.app_name
import com.example.recipe_app_compose.shared.generated.resources.onboarding_discover
import com.example.recipe_app_compose.ui.theme.AppSpacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Branded startup transition shared by the Android and iOS hosts. */
@Composable
fun RecipeComposeSplashScreen(
    destinationReady: Boolean = true,
    onFinished: () -> Unit,
) {
    var entranceProgress by remember { mutableFloatStateOf(0f) }
    var splashAlpha by remember { mutableFloatStateOf(1f) }
    var minimumDurationElapsed by remember { mutableStateOf(false) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        animateWithFrameTime(durationMillis = 650) { entranceProgress = it }
        delay(1_250)
        minimumDurationElapsed = true
    }

    LaunchedEffect(minimumDurationElapsed, destinationReady) {
        if (minimumDurationElapsed && destinationReady) {
            animateWithFrameTime(durationMillis = 650) { splashAlpha = 1f - it }
            currentOnFinished()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = splashAlpha }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            },
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = entranceProgress
                    val scale = 0.92f + (entranceProgress * 0.08f)
                    scaleX = scale
                    scaleY = scale
                },
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
                AppLoadingIndicator(
                    modifier = Modifier.size(AppSpacing.ExtraLarge),
                    strokeWidth = AppSpacing.ExtraSmall,
                )
            }
        }
    }
}
