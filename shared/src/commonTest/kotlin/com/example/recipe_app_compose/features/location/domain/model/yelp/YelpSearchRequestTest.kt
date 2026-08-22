package com.example.recipe_app_compose.features.location.domain.model.yelp

import com.example.recipe_app_compose.features.location.domain.model.location.LocationData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class YelpSearchRequestTest {
    @Test
    fun `coordinate search keeps location and default paging`() {
        val location = LocationData(latitude = 28.18, longitude = -82.35)
        val request = YelpSearchRequest(
            term = "restaurants",
            origin = YelpSearchOrigin.Coordinates(location),
        )

        assertEquals(location, (request.origin as YelpSearchOrigin.Coordinates).location)
        assertNull(request.radiusMeters)
        assertEquals(50U, request.limit)
        assertEquals(0U, request.offset)
    }

    @Test
    fun `named location search retains user supplied value`() {
        val request = YelpSearchRequest(
            term = "brunch",
            origin = YelpSearchOrigin.NamedLocation("Austin, TX"),
        )

        assertIs<YelpSearchOrigin.NamedLocation>(request.origin)
        assertEquals("Austin, TX", request.origin.value)
    }
}
