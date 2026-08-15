package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository
import com.example.recipe_app_compose.features.categories.domain.states.CategoryMealState
import com.example.recipe_app_compose.features.categories.domain.states.IngredientMealState
import com.example.recipe_app_compose.features.categories.domain.states.RandomMealState
import com.example.recipe_app_compose.features.categories.domain.states.RecipeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class RecipeViewModel(
    private val repository: RecipeRepository = DependencyInjector.repository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow(RecipeState())
    val categoriesState = _categoriesState.asStateFlow()

    private val _categoryMealState = MutableStateFlow(CategoryMealState())
    val categoryMealState = _categoryMealState.asStateFlow()

    private val _randomMealState = MutableStateFlow(RandomMealState())
    val randomMealState = _randomMealState.asStateFlow()

    private val _ingredientMealState = MutableStateFlow(IngredientMealState())
    val ingredientMealState = _ingredientMealState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var ingredientSearchJob: Job? = null

    internal val ingredientsList = combine(
        searchQuery,
        _ingredientMealState.map { it.list.orEmpty() }
    ) { text, ingredients ->
        if (text.isBlank()) {
            ingredients
        } else {
            ingredients.filter { ingredient -> ingredient.doesMatchSearchQuery(text) }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    internal fun onSearchTextChange(text: String) {
        _searchQuery.value = text
        ingredientSearchJob?.cancel()

        if (text.isBlank()) {
            _isSearching.value = false
            return
        }

        ingredientSearchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(500L.milliseconds)
            loadIngredients(text)
            _isSearching.value = false
        }
    }

    internal fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getCategories()) {
                is Resource.Error -> _categoriesState.update {
                    it.copy(loading = false, error = "Error fetching categories.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> _categoriesState.update {
                    it.copy(
                        loading = false,
                        list = response.data?.categories.orEmpty(),
                        error = null
                    )
                }
            }
        }
    }

    internal fun fetchCategoryMeals() {
        viewModelScope.launch {
            _categoryMealState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getCategoriesMeal()) {
                is Resource.Error -> _categoryMealState.update {
                    it.copy(loading = false, error = "Error fetching category meals.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> _categoryMealState.update {
                    it.copy(
                        loading = false,
                        list = response.data?.meals.orEmpty(),
                        error = null
                    )
                }
            }
        }
    }

    internal fun fetchRandomMeal() {
        viewModelScope.launch {
            _randomMealState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getRandomMeal()) {
                is Resource.Error -> _randomMealState.update {
                    it.copy(loading = false, error = "Error fetching random meal.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> _randomMealState.update {
                    it.copy(
                        loading = false,
                        item = response.data?.meals.orEmpty(),
                        error = null
                    )
                }
            }
        }
    }

    internal fun fetchIngredients(query: String = SEARCH_DEFAULT) {
        viewModelScope.launch {
            loadIngredients(query)
        }
    }

    private suspend fun loadIngredients(query: String) {
        _ingredientMealState.update { it.copy(loading = true, error = null) }
        when (val response = repository.getIngredient(query)) {
            is Resource.Error -> _ingredientMealState.update {
                it.copy(loading = false, error = "Error fetching ingredients.")
            }

            is Resource.Loading -> Unit

            is Resource.Success -> _ingredientMealState.update {
                it.copy(
                    loading = false,
                    list = response.data?.meals.orEmpty(),
                    error = null
                )
            }
        }
    }

    init {
        fetchCategories()
        fetchCategoryMeals()
        fetchRandomMeal()
        fetchIngredients()
    }

    internal companion object {
        const val SEARCH_DEFAULT = "A"
    }
}
