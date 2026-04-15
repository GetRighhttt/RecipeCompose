package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseRepository: DatabaseRepository = DependencyInjector.databaseRepo
) : ViewModel() {
    private val _currentState = MutableStateFlow(DatabaseState())
    val currentState = _currentState.asStateFlow()

    internal val executeInsertMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch { databaseRepository.executeInsertMeal(meal = meal) }
    }

    internal val executeDeleteMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch { databaseRepository.executeDeleteMeal(meal = meal) }
    }

    internal val executeDeleteAll: () -> Job = {
        viewModelScope.launch { databaseRepository.executeDeleteAll() }
    }

    internal val executeGetAllMeals: () -> Job = {
        viewModelScope.launch {
            databaseRepository.executeGetAllMeals().collectLatest { meal ->
                _currentState.update {
                    _currentState.value.copy(
                        loading = false,
                        list = meal,
                        error = null
                    )
                }
            }
        }
    }

    init { executeGetAllMeals.invoke() }
}