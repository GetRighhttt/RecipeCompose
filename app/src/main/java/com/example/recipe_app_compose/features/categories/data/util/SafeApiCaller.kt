package com.example.recipe_app_compose.features.categories.data.util

import com.example.recipe_app_compose.core.util.Resource
import kotlinx.coroutines.CancellationException
import retrofit2.Response

suspend inline fun <T> safeApiCall(
    crossinline call: suspend () -> Response<T>,
    defaultError: String
): Resource<T> =
    runCatching { call() }
        .fold(
            onSuccess = { response ->
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error(
                        response.message().takeIf { it.isNotBlank() } ?: defaultError
                    )
                }
            },
            onFailure = { throwable ->
                if (throwable is CancellationException) throw throwable
                Resource.Error(throwable.message ?: defaultError)
            }
        )