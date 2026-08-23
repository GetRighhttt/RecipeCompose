package com.example.recipe_app_compose.core.animation

import androidx.compose.runtime.withFrameNanos

/**
 * Advances an animation from rendered frame time instead of platform duration scaling.
 * This keeps short branded transitions consistent on Android and physical iOS devices.
 */
internal suspend fun animateWithFrameTime(
    durationMillis: Long,
    onFrame: (Float) -> Unit,
) {
    if (durationMillis <= 0) {
        onFrame(1f)
        return
    }

    val startedAt = withFrameNanos { it }
    var progress = 0f

    while (progress < 1f) {
        val frameTime = withFrameNanos { it }
        progress = ((frameTime - startedAt) / 1_000_000f / durationMillis)
            .coerceIn(0f, 1f)
        val easedProgress = progress * progress * (3f - (2f * progress))
        onFrame(easedProgress)
    }
}
