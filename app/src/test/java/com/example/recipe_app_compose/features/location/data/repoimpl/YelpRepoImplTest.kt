package com.example.recipe_app_compose.features.location.data.repoimpl

import com.example.recipe_app_compose.features.location.data.api.YelpApi
import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchOrigin
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchRequest
import com.example.recipe_app_compose.features.location.domain.model.yelp.YelpSearchResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response

class YelpRepoImplTest {
    @Test
    fun `coordinate origin omits named location`() = runTest {
        val api = RecordingYelpApi()
        val repository = YelpRepImpl(api)

        repository.searchShops(
            YelpSearchRequest(
                term = "restaurants",
                origin = YelpSearchOrigin.Coordinates(
                    LocationData(latitude = 28.18, longitude = -82.35)
                ),
                radiusMeters = 16_000,
            )
        )

        assertNull(api.location)
        assertEquals(28.18, api.latitude ?: 0.0, 0.0)
        assertEquals(-82.35, api.longitude ?: 0.0, 0.0)
        assertEquals(16_000, api.radius)
    }

    @Test
    fun `named origin omits coordinates`() = runTest {
        val api = RecordingYelpApi()
        val repository = YelpRepImpl(api)

        repository.searchShops(
            YelpSearchRequest(
                term = "coffee",
                origin = YelpSearchOrigin.NamedLocation("Austin, TX"),
            )
        )

        assertEquals("Austin, TX", api.location)
        assertNull(api.latitude)
        assertNull(api.longitude)
    }

    private class RecordingYelpApi : YelpApi {
        var location: String? = null
        var latitude: Double? = null
        var longitude: Double? = null
        var radius: Int? = null

        override suspend fun searchShops(
            authHeader: String,
            searchTerm: String,
            location: String?,
            latitude: Double?,
            longitude: Double?,
            radius: Int?,
            limit: UInt,
            offset: UInt,
        ): Response<YelpSearchResult> {
            this.location = location
            this.latitude = latitude
            this.longitude = longitude
            this.radius = radius
            return Response.success(YelpSearchResult(total = 0U, shops = emptyList()))
        }
    }
}
