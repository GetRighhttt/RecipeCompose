package com.example.recipe_app_compose.features.onboarding.presentation

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.recipe_app_compose.core.animation.animateWithFrameTime
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.onboarding_completion_description
import com.example.recipe_app_compose.shared.generated.resources.onboarding_completion_title
import com.example.recipe_app_compose.shared.generated.resources.onboarding_discover
import com.example.recipe_app_compose.ui.theme.AppSpacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * First-run handoff rendered above an already composed home screen.
 *
 * [onFinished] runs only after the exit fade completes. Platform hosts use that
 * callback as the transaction boundary for persisting onboarding completion.
 */
@Composable
fun OnboardingCompletionOverlay(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var messageProgress by remember { mutableFloatStateOf(0f) }
    var overlayAlpha by remember { mutableFloatStateOf(1f) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        animateWithFrameTime(durationMillis = 450) { messageProgress = it }
        delay(900)
        animateWithFrameTime(durationMillis = 900) { overlayAlpha = 1f - it }
        currentOnFinished()
    }

    Popup(
        alignment = Alignment.TopStart,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer { alpha = overlayAlpha },
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
                        alpha = messageProgress
                        val scale = 0.96f + (messageProgress * 0.04f)
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
                        text = stringResource(Res.string.onboarding_completion_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(AppSpacing.Small))
                    Text(
                        text = stringResource(Res.string.onboarding_completion_description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
