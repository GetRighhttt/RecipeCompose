package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    // Collecting flow data in LiveData variable
    private val _currentState = MutableLiveData<List<RandomMeal>>()
    val currentState = _currentState.asFlow()

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // operator methods
    private operator fun MutableLiveData<Boolean>.invoke(state: Boolean?) =
        _isLoading.postValue(state)

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

    private val executeGetAllMeals: () -> Job = {
        viewModelScope.launch {
            _isLoading(true)
            delay(1000)
            databaseRepository.executeGetAllMeals().collectLatest { meal ->
                _currentState.value = meal
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