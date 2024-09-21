package com.example.recipe_app_compose.data.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.data.api.RetrofitInstance.apiService
import com.example.recipe_app_compose.domain.model.CategoryResponse
import com.example.recipe_app_compose.domain.model.RandomMealResponse
import com.example.recipe_app_compose.domain.model.CategoryMealResponse
import com.example.recipe_app_compose.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepositoryImpl : RecipeRepository {

    override suspend fun getCategories(): Resource<CategoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Categories.")
            }
        }
    }

    override suspend fun getCategoriesMeal(): Resource<CategoryMealResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategoriesMeal()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Category Meals.")
            }
        }
    }

    override suspend fun getRandomMeal(): Resource<RandomMealResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRandomMeal()
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Random Meal.")
            }
        }
    }
}