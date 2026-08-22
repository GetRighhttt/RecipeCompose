package com.example.recipe_app_compose.features.categories.presentation

import com.example.recipe_app_compose.core.util.Resource
import com.example.recipe_app_compose.features.categories.domain.repository.RecipeRepository
import com.example.recipe_app_compose.features.categories.domain.states.IngredientUiState
import com.example.recipe_app_compose.features.categories.domain.states.RandomMealUiState
import com.example.recipe_app_compose.features.categories.domain.states.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared recipe state and actions; each platform owns this store's lifecycle. */
class RecipeStore(
    private val repository: RecipeRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutableSearchQuery = MutableStateFlow("")
    private val mutableIsSearching = MutableStateFlow(false)
    private val mutableUiState = MutableStateFlow(UiState())
    private val mutableIngredientUiState = MutableStateFlow(IngredientUiState())
    private val mutableRandomMealUiState = MutableStateFlow(RandomMealUiState())

    val searchQuery: StateFlow<String> = mutableSearchQuery
    val isSearching: StateFlow<Boolean> = mutableIsSearching
    val uiState: StateFlow<UiState> = mutableUiState
    val ingredientUiState: StateFlow<IngredientUiState> = mutableIngredientUiState
    val randomMealUiState: StateFlow<RandomMealUiState> = mutableRandomMealUiState
    private var ingredientSearchJob: Job? = null

    val ingredients = combine(searchQuery, ingredientUiState.map { it.list }) { text, meals ->
        if (text.isBlank()) meals else meals.filter { it.doesMatchSearchQuery(text) }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchTextChange(text: String) {
        mutableSearchQuery.value = text
        ingredientSearchJob?.cancel()
        if (text.isBlank()) {
            mutableIsSearching.value = false
            return
        }
        ingredientSearchJob = scope.launch {
            mutableIsSearching.value = true
            delay(SEARCH_DEBOUNCE_MILLIS)
            loadIngredients(text)
            mutableIsSearching.value = false
        }
    }

    fun fetchCategories() = scope.launch {
        mutableUiState.update { it.copy(loading = true, error = null) }
        when (val response = repository.getCategories()) {
            is Resource.Success -> mutableUiState.update { it.copy(loading = false, list = response.data?.categories.orEmpty(), error = null) }
            is Resource.Error -> mutableUiState.update { it.copy(loading = false, error = "Error fetching categories.") }
            is Resource.Loading -> Unit
        }
    }

    fun fetchRandomMeal() = scope.launch {
        mutableRandomMealUiState.update { it.copy(loading = true, error = null) }
        when (val response = repository.getRandomMeal()) {
            is Resource.Success -> mutableRandomMealUiState.update { it.copy(loading = false, item = response.data?.meals.orEmpty(), error = null) }
            is Resource.Error -> mutableRandomMealUiState.update { it.copy(loading = false, error = "Error fetching random meal.") }
            is Resource.Loading -> Unit
        }
    }

    fun fetchIngredients(query: String = SEARCH_DEFAULT) = scope.launch { loadIngredients(query) }

    fun close() = scope.cancel()

    private suspend fun loadIngredients(query: String) {
        mutableIngredientUiState.update { it.copy(loading = true, error = null) }
        when (val response = repository.getIngredient(query)) {
            is Resource.Success -> mutableIngredientUiState.update { it.copy(loading = false, list = response.data?.meals.orEmpty(), error = null) }
            is Resource.Error -> mutableIngredientUiState.update { it.copy(loading = false, error = "Error fetching ingredients.") }
            is Resource.Loading -> Unit
        }
    }

    init {
        fetchCategories()
        fetchRandomMeal()
        fetchIngredients()
    }

    companion object {
        const val SEARCH_DEFAULT = "A"
        private const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
