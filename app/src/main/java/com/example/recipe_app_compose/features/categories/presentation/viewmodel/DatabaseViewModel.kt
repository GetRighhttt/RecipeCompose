package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseRepository: DatabaseRepository = DependencyInjector.databaseRepo
) : ViewModel() {
    val currentState: StateFlow<DatabaseState>
        field: MutableStateFlow<DatabaseState> = MutableStateFlow(DatabaseState())

    internal fun executeInsertMeal(meal: RandomMeal) {
        viewModelScope.launch { databaseRepository.executeInsertMeal(meal = meal) }
    }

    internal fun executeDeleteMeal(meal: RandomMeal) {
        viewModelScope.launch { databaseRepository.executeDeleteMeal(meal = meal) }
    }

    internal fun executeDeleteAll() {
        viewModelScope.launch { databaseRepository.executeDeleteAll() }
    }

    internal fun executeGetAllMeals() {
        viewModelScope.launch {
            databaseRepository.executeGetMeals().collectLatest { meal ->
                currentState.update { state ->
                    state.copy(
                        loading = false,
                        list = meal,
                        error = null
                    )
                }
            }
        }
    }

    init {
        executeGetAllMeals()
    }
}
