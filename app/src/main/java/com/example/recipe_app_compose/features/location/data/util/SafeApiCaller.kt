package com.example.recipe_app_compose.features.location.data.util

import com.example.recipe_app_compose.core.util.Resource
import kotlinx.coroutines.CancellationException
import retrofit2.Response

/** Retrofit boundary retained only while Yelp remains Android-specific. */
suspend inline fun <T> safeApiCall(
    crossinline call: suspend () -> Response<T>,
    defaultError: String,
): Resource<T> = runCatching { call() }.fold(
    onSuccess = { response ->
        response.body().takeIf { response.isSuccessful }?.let { body -> Resource.Success(body) }
            ?: Resource.Error(response.message().takeIf(String::isNotBlank) ?: defaultError)
    },
    onFailure = { throwable ->
        if (throwable is CancellationException) throw throwable
        Resource.Error(throwable.message ?: defaultError)
    },
)
