package com.example.recipe_app_compose.features.categories.data.remote

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

/** Ktor implementation shared by Android and iOS. Each host supplies its engine. */
class RecipeRepositoryImpl(
    private val client: HttpClient = createRecipeHttpClient(),
) : RecipeRepository {
    override suspend fun getCategories(): Resource<CategoryResponse> =
        request("categories.php", "Unable to retrieve categories.")

    override suspend fun getRandomMeal(): Resource<RandomMealResponse> =
        request("random.php", "Unable to retrieve random meal.")

    override suspend fun getIngredient(ingredient: String): Resource<IngredientResponse> =
        request("search.php", "Unable to retrieve ingredient.") { parameter("s", ingredient) }

    private suspend inline fun <reified T> request(
        path: String,
        defaultError: String,
        crossinline configure: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {},
    ): Resource<T> = try {
        Resource.Success(client.get("$BASE_URL$path") { configure() }.body())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Resource.Error(throwable.message ?: defaultError)
    }
}

private fun createRecipeHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
    }
}
