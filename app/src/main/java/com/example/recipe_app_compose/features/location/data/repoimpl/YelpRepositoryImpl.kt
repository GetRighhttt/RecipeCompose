package com.example.recipe_app_compose.features.location.data.repoimpl

import com.example.recipe_app_compose.core.util.Constants
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.data.util.safeApiCall
import com.example.recipe_app_compose.features.location.data.api.YelpApi
import com.example.recipe_app_compose.features.location.data.retrofit.YelpRetrofitInstance.yelpApiService
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import com.example.recipe_app_compose.features.location.domain.repo.YelpRepository

class YelpRepositoryImpl(
    private val api: YelpApi = yelpApiService,
) : YelpRepository {
    override suspend fun searchShops(
        request: YelpSearchRequest,
    ): Resource<YelpSearchResult> {
        val coordinates = request.origin as? YelpSearchOrigin.Coordinates
        val namedLocation = request.origin as? YelpSearchOrigin.NamedLocation

        return safeApiCall(
            call = {
                api.searchShops(
                    authHeader = "Bearer ${Constants.YELP_API_KEY}",
                    searchTerm = request.term,
                    location = namedLocation?.value,
                    latitude = coordinates?.location?.latitude,
                    longitude = coordinates?.location?.longitude,
                    radius = request.radiusMeters?.coerceIn(0, MAX_RADIUS_METERS),
                    limit = request.limit,
                    offset = request.offset,
                )
            },
            defaultError = "Unable to search businesses."
        )
    }

    private companion object {
        const val MAX_RADIUS_METERS = 40_000
    }
}
