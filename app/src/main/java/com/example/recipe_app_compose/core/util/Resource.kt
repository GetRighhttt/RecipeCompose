package com.example.recipe_app_compose.core.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    open class Success<T>(data: T) : Resource<T>(data = data)
    open class Error<T>(errorMessage: String) : Resource<T>(message = errorMessage)
    open class Loading<T> : Resource<T>()
}