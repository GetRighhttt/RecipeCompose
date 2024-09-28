package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseState
import kotlinx.coroutines.Dispatchers
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

    val executeInsertMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            databaseRepository.executeInsertMeal(meal = meal)
        }
    }

    val executeDeleteMeal: (RandomMeal) -> Job = { meal ->
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            databaseRepository.executeDeleteMeal(meal = meal)
        }
    }

    val executeGetAllMeals: () -> Job = {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            databaseRepository.executeGetAllMeals().collectLatest { meal ->
                _currentState.value = _currentState.value.copy(
                    loading = false,
                    list = meal,
                    error = null
                )
            }
        }
    }

    val executeDeleteAll: () -> Job = {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            databaseRepository.executeDeleteAll()
        }
    }

    init {
        executeGetAllMeals.invoke()
    }
}