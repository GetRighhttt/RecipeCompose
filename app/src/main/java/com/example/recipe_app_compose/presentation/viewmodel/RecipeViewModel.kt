package com.example.recipe_app_compose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.data.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.domain.states.CategoryMealState
import com.example.recipe_app_compose.domain.states.IngredientMealState
import com.example.recipe_app_compose.domain.states.RandomMealState
import com.example.recipe_app_compose.domain.states.RecipeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
Explaining various states and reasoning as this can be somewhat confusing at first..
 */
class RecipeViewModel : ViewModel() {

    // necessary repo composition
    private val repository = RecipeRepositoryImpl()

    // states for each api call from `RecipeStates.kt`
    private val _categoriesState = MutableStateFlow(RecipeState())
    val categoriesState = _categoriesState.asStateFlow()

    private val _categoryMealState = MutableStateFlow(CategoryMealState())
    val categoryMealState = _categoryMealState.asStateFlow()

    private val _randomMealState = MutableStateFlow(RandomMealState())
    val randomMealState = _randomMealState.asStateFlow()

    private val _ingredientMealState = MutableStateFlow(IngredientMealState())
    val ingredientMealState = _ingredientMealState.asStateFlow()

    // states for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // ingredient list to be populated by way of Flow operators
    private val _ingredientsList = MutableStateFlow(_ingredientMealState.value.list)
    @OptIn(FlowPreview::class)
    val ingredientsList = searchQuery
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_ingredientsList) { text, ingredients ->
            if (text.isBlank()) {
                ingredients
            } else {
                delay(500L)
                ingredients?.filter { ingredient ->
                    ingredient.doesMatchSearchQuery(text)
                }.also { fetchIngredients(text) }
            }
            // convert to State FLow
        }.onEach { _isSearching.update { false } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _ingredientsList.value
        )

    // method to set new search value
    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
    }

    init {
        fetchCategories()
        fetchCategoryMeals()
        fetchRandomMeal()
        fetchIngredients(SEARCH_DEFAULT)
    }

    /*
    viewModelScope.launch{} executes on the main thread by default; added in the Dispatcher to
    explicitly illustrate this.
    */

    internal fun fetchCategories() = viewModelScope.launch(Dispatchers.Main) {
            when (val response = repository.getCategories()) {
                is Resource.Error -> _categoriesState.value = _categoriesState.value.copy(
                    loading = false,
                    error = "Error fetching categories.}"
                )

                is Resource.Loading -> _categoriesState.value =
                    _categoriesState.value.copy(loading = true)

                is Resource.Success -> _categoriesState.value = _categoriesState.value.copy(
                    loading = false,
                    list = response.data!!.categories,
                    error = null
                )
            }
        }


    internal fun fetchCategoryMeals() = viewModelScope.launch(Dispatchers.Main) {
        when (val response = repository.getCategoriesMeal()) {
            is Resource.Error -> _categoryMealState.value = _categoryMealState.value.copy(
                loading = false,
                error = "Error fetching category meals.}"
            )

            is Resource.Loading -> _categoryMealState.value =
                _categoryMealState.value.copy(loading = true)

            is Resource.Success -> _categoryMealState.value = _categoryMealState.value.copy(
                loading = false,
                list = response.data!!.meals,
                error = null
            )
        }
    }

    internal fun fetchRandomMeal() = viewModelScope.launch(Dispatchers.Main) {
        when (val response = repository.getRandomMeal()) {
            is Resource.Error -> _randomMealState.value = _randomMealState.value.copy(
                loading = false,
                error = "Error fetching random meal.}"
            )

            is Resource.Loading -> _randomMealState.value =
                _randomMealState.value.copy(loading = true)

            is Resource.Success -> _randomMealState.value = _randomMealState.value.copy(
                loading = false,
                item = response.data!!.meals,
                error = null
            )
        }
    }

    private fun fetchIngredients(query: String) = viewModelScope.launch(Dispatchers.Main) {
        when (val response = repository.getIngredient(query)) {
            is Resource.Error -> _ingredientMealState.value = _ingredientMealState.value.copy(
                loading = false,
                error = "Error fetching random ingredients.}"
            )

            is Resource.Loading -> _ingredientMealState.value =
                _ingredientMealState.value.copy(loading = true)

            is Resource.Success -> _ingredientMealState.value = _ingredientMealState.value.copy(
                loading = false,
                list = response.data!!.meals,
                error = null
            )
        }
    }

    companion object {
        // initial search value
        const val SEARCH_DEFAULT = ""
    }
}


