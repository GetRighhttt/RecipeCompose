package com.example.recipe_app_compose.core.migration

import com.example.recipe_app_compose.shared.SHARED_MODULE_ID
import com.example.recipe_app_compose.shared.sharedPlatformName
import org.junit.Assert.assertEquals
import org.junit.Test

/** Guards the Gradle boundary while production features move incrementally. */
class SharedModuleBoundaryTest {
    @Test
    fun androidAppConsumesSharedAndroidTarget() {
        assertEquals("recipe-compose-shared", SHARED_MODULE_ID)
        assertEquals("Android", sharedPlatformName)
    }
}
