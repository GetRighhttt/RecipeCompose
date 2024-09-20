package com.example.recipe_app_compose.presentation.viewmodel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.recipe_app_compose.data.api.RetrofitInstance.apiService
import com.example.recipe_app_compose.data.repoimpl.RecipeRepositoryImpl
import com.example.recipe_app_compose.domain.model.Category
import com.example.recipe_app_compose.domain.model.SeafoodCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val _categoriesState = mutableStateOf(RecipeState())
    val categoriesState: State<RecipeState> = _categoriesState

    private val _seafoodState = mutableStateOf(SeafoodState())
    val seafoodState: State<SeafoodState> = _seafoodState

    private val repository = RecipeRepositoryImpl()

    init {
        fetchCategories()
        fetchSeafoodCategories()
    }

    /*
    Must launch with main as this will have to be performed on the main thread during initialization
    */
    private fun fetchCategories() = viewModelScope.launch(Dispatchers.Main) {
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

    private fun fetchSeafoodCategories() = viewModelScope.launch(Dispatchers.Main) {
        _seafoodState.value = _seafoodState.value.copy(loading = true)
        try {
            val response = repository.getSeafoodCategories()
            _seafoodState.value = _seafoodState.value.copy(
                loading = false,
                list = response.data!!.meals,
                error = null
            )
        } catch (e: Exception) {
            _seafoodState.value = _seafoodState.value.copy(
                loading = false,
                error = "Error fetching data: ${e.message}"
            )
        }
    }

    data class RecipeState (
        val loading: Boolean = true,
        val list: List<Category> = emptyList(),
        val error: String? = null
    )

    data class SeafoodState (
        val loading: Boolean = true,
        val list: List<SeafoodCategory> = emptyList(),
        val error: String? = null
    )
}