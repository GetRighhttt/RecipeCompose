package com.example.recipe_app_compose.features.location.domain.repo

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest

interface YelpRepository {
    suspend fun searchBusinesses(
        request: YelpSearchRequest,
    ): Resource<YelpSearchResult>
}
