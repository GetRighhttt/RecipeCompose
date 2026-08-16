package com.example.recipe_app_compose.features.location.domain.model.yelp

import org.junit.Assert.assertEquals
import org.junit.Test

class YelpShopTest {
    @Test
    fun `rating keeps Yelp half-star precision without a trailing zero`() {
        assertEquals("4.5", business(rating = 4.5).displayRating())
        assertEquals("4", business(rating = 4.0).displayRating())
    }

    @Test
    fun `US phone number is formatted safely`() {
        assertEquals(
            "(850) 555-1234",
            business(phone = "+18505551234").displayPhoneNumber(),
        )
    }

    @Test
    fun `unknown phone format is preserved`() {
        assertEquals(
            "+44 20 7946 0958",
            business(phone = "+44 20 7946 0958").displayPhoneNumber(),
        )
    }

    private fun business(
        rating: Double = 4.5,
        phone: String? = null,
    ) = YelpShop(
        rating = rating,
        phone = phone,
        id = "business-id",
        alias = "business-alias",
        isClosed = false,
        categories = emptyList(),
        reviewCount = 10U,
        name = "Business",
        url = "https://example.com",
        coordinates = YelpCoordinates(latitude = 30.0, longitude = -86.0),
        imageUrl = null,
        location = YelpLocations(
            city = "City",
            country = "US",
            address2 = "",
            address3 = "",
            state = "FL",
            address1 = "123 Main St",
            zipCode = "32501",
        ),
        distance = 100.0,
    )
}
