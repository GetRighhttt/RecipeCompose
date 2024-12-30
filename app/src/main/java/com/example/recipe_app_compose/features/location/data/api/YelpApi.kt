package com.example.recipe_app_compose.features.location.data.api

import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApi {

    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        @Query("limit") limit: UInt,
        @Query("offset") offset: UInt
    ): Response<YelpSearchResult>
}