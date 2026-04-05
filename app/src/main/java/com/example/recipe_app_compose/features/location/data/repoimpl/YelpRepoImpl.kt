package com.example.recipe_app_compose.features.location.data.repoimpl

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.data.util.safeApiCall
import com.example.recipe_app_compose.features.location.data.retrofit.YelpRetrofitInstance.yelpApiService
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository

class YelpRepImpl : YelpRepository {
    override suspend fun searchBusinesses(
        authHeader: String,
        searchTerm: String,
        location: String,
        limit: UInt,
        offset: UInt
    ): Resource<YelpSearchResult> =
        safeApiCall(
            call = {
                yelpApiService.searchBusinesses(
                    authHeader = authHeader,
                    searchTerm = searchTerm,
                    location = location,
                    limit = limit,
                    offset = offset,
                )
            },
            defaultError = "Unable to search businesses."
        )
}