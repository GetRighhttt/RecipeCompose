package com.example.recipe_app_compose.features.location.data.api

import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApi {

    @GET("businesses/search")
    suspend fun searchShops(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("radius") radius: Int?,
        @Query("limit") limit: UInt,
        @Query("offset") offset: UInt,
    ): Response<YelpSearchResult>
}
