package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val dbRepository: DatabaseRepository = DependencyInjector.databaseRepo
) : ViewModel() {
    val uiState: StateFlow<DatabaseUiState> field = MutableStateFlow(DatabaseUiState())

    internal fun executeDeleteAll() = viewModelScope.launch { dbRepository.executeDeleteAll() }

    internal fun executeInsertMeal(meal: RandomMeal) = viewModelScope.launch {
        dbRepository.executeInsertMeal(meal = meal)
    }

    internal fun executeDeleteMeal(meal: RandomMeal) = viewModelScope.launch {
        dbRepository.executeDeleteMeal(meal = meal)
    }

    internal fun executeGetAllMeals() = viewModelScope.launch {
        dbRepository.executeGetMeals().collectLatest { meal ->
            uiState.update { state ->
                state.copy(
                    loading = false,
                    list = meal,
                    error = null
                )
            }
        }
    }

    init {
        executeGetAllMeals()
    }
}
