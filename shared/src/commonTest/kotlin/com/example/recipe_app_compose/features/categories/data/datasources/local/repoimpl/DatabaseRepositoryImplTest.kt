package com.example.recipe_app_compose.features.categories.data.datasources.local.repoimpl

import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealDao
import com.example.recipe_app_compose.features.categories.data.datasources.local.db.RandomMealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseRepositoryImplTest {
    @Test
    fun `saved meals hide legacy duplicate remote ids`() = runTest {
        val repository = DatabaseRepositoryImpl(
            FakeRandomMealDao(
                meals = listOf(
                    savedMeal(id = 1, mealId = "53127", name = "First copy"),
                    savedMeal(id = 2, mealId = " 53127 ", name = "Legacy duplicate"),
                    savedMeal(id = 3, mealId = null, name = "Local recipe"),
                    savedMeal(id = 4, mealId = null, name = "Another local recipe"),
                ),
            ),
        )

        val meals = repository.getMeals().first()

        assertEquals(listOf(1, 3, 4), meals.map { it.id })
        assertEquals("First copy", meals.first().strMeal)
    }
}

private class FakeRandomMealDao(
    private val meals: List<RandomMealEntity>,
) : RandomMealDao {
    override suspend fun insertMeal(meal: RandomMealEntity) = Unit

    override suspend fun containsMeal(mealId: String): Boolean =
        meals.any { it.idMeal?.trim() == mealId.trim() }

    override fun getAllMeals(): Flow<List<RandomMealEntity>> = flowOf(meals)

    override suspend fun deleteAllMeals() = Unit

    override suspend fun deleteMealById(mealId: String) = Unit
}

private fun savedMeal(
    id: Int,
    mealId: String?,
    name: String,
) = RandomMealEntity(
    id = id,
    idMeal = mealId,
    strMeal = name,
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
