package com.example.recipe_app_compose.features.location.data.retrofit

import com.example.recipe_app_compose.core.util.Constants.YELP_BASE_URL
import com.example.recipe_app_compose.features.location.data.api.YelpApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object YelpRetrofitInstance {

    private fun provideHttpInterceptor(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
        }.build()
        return client
    }

    private val gson: GsonConverterFactory = GsonConverterFactory.create()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(YELP_BASE_URL)
        .addConverterFactory(gson)
        .client(provideHttpInterceptor())
        .build()

    val yelpApiService: YelpApi by lazy {
        retrofit.create(YelpApi::class.java)
    }
}