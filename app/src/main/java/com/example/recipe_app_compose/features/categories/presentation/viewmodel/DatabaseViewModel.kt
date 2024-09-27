package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DatabaseViewModel(
    // must default for dependency injection
    private val databaseRepository: DatabaseRepository = DependencyInjector.databaseRepo
) : ViewModel() {

    // Collecting flow data in LiveData variable
    private val _currentState = MutableStateFlow(DatabaseState())
    val currentState = _currentState.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)

    // operator extension method for loading state to remove dot notation
    private operator fun MutableStateFlow<Boolean>.invoke(state: Boolean?) =
        _isLoading.value == state

    val executeInsertMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch {
            _isLoading(true)
            delay(1000)
            databaseRepository.executeInsertMeal(meal = meal)
            _isLoading(false)
        }
    }

    val executeDeleteMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch {
            _isLoading(true)
            delay(1000)
            databaseRepository.executeDeleteMeal(meal = meal)
            _isLoading(false)
        }
    }

    val executeGetAllMeals: () -> Job = {
        viewModelScope.launch {
            _isLoading(true)
            delay(1000)
            databaseRepository.executeGetAllMeals().collectLatest { meal ->
                _currentState.value = _currentState.value.copy(
                    loading = false,
                    list = meal,
                    error = null
                )
            }
            _isLoading(false)
        }
    }

    val executeDeleteAll: () -> Job = {
        viewModelScope.launch {
            _isLoading(true)
            delay(1000)
            databaseRepository.executeDeleteAll()
            _isLoading(false)
        }
    }

    init {
        executeGetAllMeals.invoke()
    }
}