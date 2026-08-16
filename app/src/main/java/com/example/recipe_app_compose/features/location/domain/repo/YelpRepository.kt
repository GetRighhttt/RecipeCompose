package com.example.recipe_app_compose.features.location.domain.repo

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult

interface YelpRepository {
    suspend fun searchShops(
        request: YelpSearchRequest,
    ): Resource<YelpSearchResult>
}
