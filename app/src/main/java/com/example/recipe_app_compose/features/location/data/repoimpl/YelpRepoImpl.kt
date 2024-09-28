package com.example.recipe_app_compose.features.location.data.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.data.retrofit.YelpRetrofitInstance.yelpApiService
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YelpRepImpl : YelpRepository {
    override suspend fun searchBusinesses(
        authHeader: String,
        searchTerm: String,
        location: String,
        limit: UInt,
        offset: UInt
    ): Resource<YelpSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = yelpApiService.searchBusinesses(
                    authHeader,
                    searchTerm,
                    location,
                    limit,
                    offset
                )
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to retrieve Businesses.")
            }
        }
    }
}