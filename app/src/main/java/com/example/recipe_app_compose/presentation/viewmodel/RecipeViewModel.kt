package com.example.recipe_app_compose.presentation.viewmodel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.data.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.domain.states.RandomMealState
import com.example.recipe_app_compose.domain.states.RecipeState
import com.example.recipe_app_compose.domain.states.CategoryMealState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    //
    private val _categoriesState = mutableStateOf(RecipeState())
    val categoriesState: State<RecipeState> = _categoriesState

    private val _categoryMealState = mutableStateOf(CategoryMealState())
    val categoryMealState: State<CategoryMealState> = _categoryMealState

    private val _randomMealState = mutableStateOf(RandomMealState())
    val randomMealState: State<RandomMealState> = _randomMealState

    private val repository = RecipeRepositoryImpl()

    init {
        fetchCategories()
        fetchCategoryMeals()
        fetchRandomMeal()
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
}