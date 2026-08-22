package com.example.recipe_app_compose.core.util

/**
 * Shared result state for work that can load, succeed with data, or fail with
 * a user-presentable message. Platform clients map their own exceptions into
 * this type rather than leaking Retrofit, Ktor, or Android exceptions upward.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(errorMessage: String) : Resource<T>(message = errorMessage)
    class Loading<T> : Resource<T>()
}
