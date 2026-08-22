package com.example.recipe_app_compose.features.location.data.remote

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

data class YelpApiConfiguration(
    val apiKey: String,
    val baseUrl: String,
)

/** Shared Yelp client. Platform hosts provide only build-time configuration and an engine. */
class YelpRepositoryImpl(
    private val configuration: YelpApiConfiguration,
    private val client: HttpClient = createYelpHttpClient(),
) : YelpRepository {
    override suspend fun searchShops(
        request: YelpSearchRequest,
    ): Resource<YelpSearchResult> {
        if (configuration.apiKey.isBlank()) {
            return Resource.Error("Yelp API key is not configured.")
        }

        val coordinates = request.origin as? YelpSearchOrigin.Coordinates
        val namedLocation = request.origin as? YelpSearchOrigin.NamedLocation

        return try {
            val response = client.get(
                "${configuration.baseUrl.trimEnd('/')}/businesses/search"
            ) {
                header(HttpHeaders.Authorization, "Bearer ${configuration.apiKey}")
                parameter("term", request.term)
                namedLocation?.let { parameter("location", it.value) }
                coordinates?.let {
                    parameter("latitude", it.location.latitude)
                    parameter("longitude", it.location.longitude)
                }
                request.radiusMeters?.let {
                    parameter("radius", it.coerceIn(0, MAX_RADIUS_METERS))
                }
                parameter("limit", request.limit)
                parameter("offset", request.offset)
            }

            if (response.status.isSuccess()) {
                Resource.Success(response.body())
            } else {
                Resource.Error("$DEFAULT_ERROR (${response.status.value})")
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            Resource.Error(throwable.message ?: DEFAULT_ERROR)
        }
    }

    private companion object {
        const val MAX_RADIUS_METERS = 40_000
        const val DEFAULT_ERROR = "Unable to search businesses."
    }
}

private fun createYelpHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
    }
}
