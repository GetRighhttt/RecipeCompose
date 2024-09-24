package com.example.recipe_app_compose.features.location.domain.repo

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult

interface YelpRepository {
    suspend fun searchBusinesses(
        authHeader: String,
        searchTerm: String,
        location: String,
        limit: UInt,
        offset: UInt
    ): Resource<YelpSearchResult>
}