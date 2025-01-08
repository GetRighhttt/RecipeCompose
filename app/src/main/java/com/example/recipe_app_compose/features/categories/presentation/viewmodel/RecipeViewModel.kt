package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.data.remote.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.features.categories.domain.states.CategoryMealState
import com.example.recipe_app_compose.features.categories.domain.states.IngredientMealState
import com.example.recipe_app_compose.features.categories.domain.states.RandomMealState
import com.example.recipe_app_compose.features.categories.domain.states.RecipeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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
class RecipeViewModel(
    private val repository: RecipeRepositoryImpl = DependencyInjector.repository
) : ViewModel() {

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
    internal val ingredientsList = searchQuery
        // delay text when searching
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        // combine elements and emit new recently emitted values
        .combine(_ingredientsList) { text, ingredients ->
            if (text.isBlank()) {
                ingredients
            } else {
                ingredients?.filter { ingredient ->
                    ingredient.doesMatchSearchQuery(text)
                }.also {
                    fetchIngredients.invoke(text)
                    delay(400L)
                }
            }
        }.onEach { _isSearching.update { false } }
        .stateIn( // convert to State FLow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _ingredientsList.value
        )

    // method to set new search value
    internal val onSearchTextChange: (String) -> Unit = { text ->
        _searchQuery.update { text }
    }

    internal val fetchCategories: () -> Job = {
        viewModelScope.launch(Dispatchers.Main) {
            when (val response = repository.getCategories()) {
                is Resource.Error -> _categoriesState.update {
                    _categoriesState.value.copy(
                        loading = false,
                        error = "Error fetching categories."
                    )
                }

                is Resource.Loading -> _categoriesState.update {
                    _categoriesState.value.copy(loading = true)
                }

                is Resource.Success -> _categoriesState.update {
                    _categoriesState.value.copy(
                        loading = false,
                        list = response.data?.categories ?: emptyList(),
                        error = null
                    )
                }
            }
        }
    }


    internal val fetchCategoryMeals: () -> Job = {
        viewModelScope.launch(Dispatchers.Main) {
            when (val response = repository.getCategoriesMeal()) {
                is Resource.Error -> _categoryMealState.update {
                    _categoryMealState.value.copy(
                        loading = false,
                        error = "Error fetching category meals."
                    )
                }

                is Resource.Loading -> _categoryMealState.update {
                    _categoryMealState.value.copy(loading = true)
                }

                is Resource.Success -> _categoryMealState.update {
                    _categoryMealState.value.copy(
                        loading = false,
                        list = response.data?.meals ?: emptyList(),
                        error = null
                    )
                }
            }
        }
    }

    internal val fetchRandomMeal: () -> Job = {
        viewModelScope.launch(Dispatchers.Main) {
            when (val response = repository.getRandomMeal()) {
                is Resource.Error -> _randomMealState.update {
                    _randomMealState.value.copy(
                        loading = false,
                        error = "Error fetching random meal."
                    )
                }

                is Resource.Loading -> _randomMealState.update {
                    _randomMealState.value.copy(loading = true)
                }

                is Resource.Success -> _randomMealState.update {
                    _randomMealState.value.copy(
                        loading = false,
                        item = response.data?.meals ?: emptyList(),
                        error = null
                    )
                }
            }
        }
    }

    internal val fetchIngredients: (String) -> Job = { query ->
        viewModelScope.launch(Dispatchers.Main) {
            when (val response = repository.getIngredient(query)) {
                is Resource.Error -> _ingredientMealState.update {
                    _ingredientMealState.value.copy(
                        loading = false,
                        error = "Error fetching random ingredients."
                    )
                }

                is Resource.Loading -> _ingredientMealState.update {
                    _ingredientMealState.value.copy(loading = true)
                }

                is Resource.Success -> _ingredientMealState.update {
                    _ingredientMealState.value.copy(
                        loading = false,
                        list = response.data?.meals ?: emptyList(),
                        error = null
                    )
                }
            }
        }
    }

    private fun updateMeals() = viewModelScope.launch {
        fetchCategories.invoke()
        fetchCategoryMeals.invoke()
        fetchRandomMeal.invoke()
        fetchIngredients.invoke(SEARCH_DEFAULT)
    }

    init {
        updateMeals()
    }

    internal companion object {
        // initial search value
        const val SEARCH_DEFAULT = "A"
    }
}


