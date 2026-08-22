package com.example.recipe_app_compose.features.categories.presentation.view

import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.model.details.containsSavedMeal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MealDetailsStateTest {
    @Test
    fun `matching remote meal id is saved`() {
        assertTrue(listOf(meal("52772")).containsSavedMeal("52772"))
    }

    @Test
    fun `different remote meal id is not saved`() {
        assertFalse(listOf(meal("52772")).containsSavedMeal("52819"))
    }

    @Test
    fun `blank meal ids never mark details as saved`() {
        assertFalse(listOf(meal("")).containsSavedMeal(""))
        assertFalse(listOf(meal(null)).containsSavedMeal(null))
    }

    private fun meal(idMeal: String?) = RandomMeal(
        id = 1,
        idMeal = idMeal,
        strMeal = "Meal",
        strCategory = null,
        strArea = null,
        strInstructions = null,
        strMealThumb = null,
        strYoutube = null,
        strIngredient1 = null,
        strIngredient2 = null,
        strIngredient3 = null,
        strIngredient4 = null,
        strIngredient5 = null,
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strSource = null,
    )
}
