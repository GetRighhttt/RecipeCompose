package com.example.recipe_app_compose.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryNavigationTest {
    @Test
    fun mapRouteContainsDestinationCoordinates() {
        val route = CategoryScreen.MapScreen.createRoute(
            latitude = 28.2397,
            longitude = -82.3279,
        )

        assertEquals(
            "map_screen?latitude=28.2397&longitude=-82.3279",
            route,
        )
    }
}
