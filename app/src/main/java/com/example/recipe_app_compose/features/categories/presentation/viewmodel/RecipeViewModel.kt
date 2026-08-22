package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recipe_app_compose.features.categories.presentation.RecipeStore

/** Android lifecycle adapter around the shared recipe state holder. */
class RecipeViewModel(
    private val store: RecipeStore,
) : ViewModel() {
    val searchQuery = store.searchQuery
    val isSearching = store.isSearching
    val uiState = store.uiState
    val ingUiState = store.ingredientUiState
    val randUiState = store.randomMealUiState
    internal val ingredientsList = store.ingredients

    internal fun onSearchTextChange(text: String) = store.onSearchTextChange(text)
    internal fun fetchCategories() = store.fetchCategories()
    internal fun fetchRandomMeal() = store.fetchRandomMeal()
    internal fun fetchIngredients(query: String = SEARCH_DEFAULT) = store.fetchIngredients(query)

    override fun onCleared() {
        store.close()
    }

    internal companion object {
        const val SEARCH_DEFAULT = RecipeStore.SEARCH_DEFAULT
    }
}
