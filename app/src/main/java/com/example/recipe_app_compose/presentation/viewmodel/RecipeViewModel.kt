package com.example.recipe_app_compose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.data.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.domain.states.CategoryMealState
import com.example.recipe_app_compose.domain.states.IngredientMealState
import com.example.recipe_app_compose.domain.states.RandomMealState
import com.example.recipe_app_compose.domain.states.RecipeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    //
    private val _categoriesState = MutableStateFlow(RecipeState())
    val categoriesState = _categoriesState.asStateFlow()

    private val _categoryMealState = MutableStateFlow(CategoryMealState())
    val categoryMealState = _categoryMealState.asStateFlow()

    private val _randomMealState = MutableStateFlow(RandomMealState())
    val randomMealState = _randomMealState.asStateFlow()

    private val _ingredientMealState = MutableStateFlow(IngredientMealState())
    val ingredientMealState = _ingredientMealState.asStateFlow()

    private val repository = RecipeRepositoryImpl()

    /*
    Implementing search bar logic
     */

    //first state the text typed by the user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    //second state whether the search is happening or not
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    //third state the list to be filtered
    private val _ingredientsList = MutableStateFlow(ingredientMealState.value.list)
    val ingredientsList = searchQuery // set list to searchQuery
        // combine list with search query
        .combine(_ingredientsList) { text, ingredients ->
            if (text.isBlank()) {
                ingredients
            } else {
                ingredients?.filter { ingredient ->
                    ingredient.doesMatchSearchQuery(text)
                }
                fetchIngredients(text)
            }
            // convert to State FLow
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),//it will allow the StateFlow survive 5 seconds before it been canceled
            initialValue = _ingredientsList.value
        )

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    init {
        fetchCategories()
        fetchCategoryMeals()
        fetchRandomMeal()
        fetchIngredients(searchQuery.toString())
    }

    /*
    Must launch all init methods with Dispatchers.Main as these will be called from the main
    thread as soon as the lifecycle starts.
    */

    internal fun fetchCategories() = viewModelScope.launch(Dispatchers.Main) {
        _categoriesState.value = _categoriesState.value.copy(loading = true)
        try {
            val response = repository.getCategories()
            _categoriesState.value = _categoriesState.value.copy(
                loading = false,
                list = response.data!!.categories,
                error = null
            )
        } catch (e: Exception) {
            _categoriesState.value = _categoriesState.value.copy(
                loading = false,
                error = "Error fetching data: ${e.message}"
            )
        }
    }

    internal fun fetchCategoryMeals() = viewModelScope.launch(Dispatchers.Main) {
        _categoryMealState.value = _categoryMealState.value.copy(loading = true)
        try {
            val response = repository.getCategoriesMeal()
            _categoryMealState.value = _categoryMealState.value.copy(
                loading = false,
                list = response.data!!.meals,
                error = null
            )
        } catch (e: Exception) {
            _categoryMealState.value = _categoryMealState.value.copy(
                loading = false,
                error = "Error fetching data: ${e.message}"
            )
        }
    }

    internal fun fetchRandomMeal() = viewModelScope.launch(Dispatchers.Main) {
        _randomMealState.value = _randomMealState.value.copy(loading = true)
        try {
            val response = repository.getRandomMeal()
            _randomMealState.value = _randomMealState.value.copy(
                loading = false,
                item = response.data!!.meals,
                error = null
            )
        } catch (e: Exception) {
            _randomMealState.value = _randomMealState.value.copy(
                loading = false,
                error = "Error fetching data: ${e.message}"
            )
        }
    }

    internal fun fetchIngredients(query: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _ingredientMealState.value = _ingredientMealState.value.copy(loading = true)
            try {
                val response = repository.getIngredient(query)
                _ingredientMealState.value = _ingredientMealState.value.copy(
                    loading = false,
                    list = response.data!!.meals,
                    error = null
                )
            } catch (e: Exception) {
                _ingredientMealState.value = _ingredientMealState.value.copy(
                    loading = false,
                    error = "Error fetching data: ${e.message}"
                )
            }
        }
    }
}


