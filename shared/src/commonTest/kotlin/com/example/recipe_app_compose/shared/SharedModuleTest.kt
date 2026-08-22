package com.example.recipe_app_compose.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedModuleTest {
    @Test
    fun exposesStableModuleIdentity() {
        assertEquals("recipe-compose-shared", SHARED_MODULE_ID)
    }
}
