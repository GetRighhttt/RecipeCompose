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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class RecipeViewModel(
    private val repository: RecipeRepository = DependencyInjector.repository
) : ViewModel() {

    val categoriesState: StateFlow<RecipeState>
        field: MutableStateFlow<RecipeState> = MutableStateFlow(RecipeState())

    val categoryMealState: StateFlow<CategoryMealState>
        field: MutableStateFlow<CategoryMealState> = MutableStateFlow(CategoryMealState())

    val randomMealState: StateFlow<RandomMealState>
        field: MutableStateFlow<RandomMealState> = MutableStateFlow(RandomMealState())

    val ingredientMealState: StateFlow<IngredientMealState>
        field: MutableStateFlow<IngredientMealState> = MutableStateFlow(IngredientMealState())

    val searchQuery: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val isSearching: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var ingredientSearchJob: Job? = null

    internal val ingredientsList = combine(
        searchQuery,
        ingredientMealState.map { it.list.orEmpty() }
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
        searchQuery.value = text
        ingredientSearchJob?.cancel()

        if (text.isBlank()) {
            isSearching.value = false
            return
        }

        ingredientSearchJob = viewModelScope.launch {
            isSearching.value = true
            delay(500L.milliseconds)
            loadIngredients(text)
            isSearching.value = false
        }
    }

    internal fun fetchCategories() {
        viewModelScope.launch {
            categoriesState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getCategories()) {
                is Resource.Error -> categoriesState.update {
                    it.copy(loading = false, error = "Error fetching categories.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> categoriesState.update {
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
            categoryMealState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getCategoriesMeal()) {
                is Resource.Error -> categoryMealState.update {
                    it.copy(loading = false, error = "Error fetching category meals.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> categoryMealState.update {
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
            randomMealState.update { it.copy(loading = true, error = null) }
            when (val response = repository.getRandomMeal()) {
                is Resource.Error -> randomMealState.update {
                    it.copy(loading = false, error = "Error fetching random meal.")
                }

                is Resource.Loading -> Unit

                is Resource.Success -> randomMealState.update {
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
        ingredientMealState.update { it.copy(loading = true, error = null) }
        when (val response = repository.getIngredient(query)) {
            is Resource.Error -> ingredientMealState.update {
                it.copy(loading = false, error = "Error fetching ingredients.")
            }

            is Resource.Loading -> Unit

            is Resource.Success -> ingredientMealState.update {
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
