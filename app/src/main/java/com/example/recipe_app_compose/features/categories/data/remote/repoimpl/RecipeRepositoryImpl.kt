package com.example.recipe_app_compose.features.categories.data.remote.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.data.remote.retrofit.RetrofitInstance.apiService
import com.example.recipe_app_compose.features.categories.domain.model.category.CategoryResponse
import com.example.recipe_app_compose.features.categories.domain.model.categorymeal.CategoryMealResponse
import com.example.recipe_app_compose.features.categories.domain.model.ingredient.IngredientResponse
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMealResponse
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class RecipeRepositoryImpl : RecipeRepository {

    override suspend fun getCategories(): Resource<CategoryResponse> {
        return try {
            val response = apiService.getCategories()
            val result = response.body()
            if ((response.isSuccessful) && (result != null)) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Resource.Error(e.message ?: "Unable to retrieve Categories.")
        }
    }

    override suspend fun getCategoriesMeal(): Resource<CategoryMealResponse> {
        return try {
            val response = apiService.getCategoriesMeal()
            val result = response.body()
            if ((response.isSuccessful) && (result != null)) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Resource.Error(e.message ?: "Unable to retrieve Category Meals.")
        }
    }

    override suspend fun getRandomMeal(): Resource<RandomMealResponse> {
        return try {
            val response = apiService.getRandomMeal()
            val result = response.body()
            if ((response.isSuccessful) && (result != null)) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Resource.Error(e.message ?: "Unable to retrieve Random Meal.")
        }
    }

    override suspend fun getIngredient(ingredient: String): Resource<IngredientResponse> {
        return try {
            val response = apiService.getIngredient(ingredient)
            val result = response.body()
            if ((response.isSuccessful) && (result != null)) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Resource.Error(e.message ?: "Unable to retrieve Ingredient.")
        }
    }
}