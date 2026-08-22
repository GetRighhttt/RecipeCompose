package com.example.recipe_app_compose.features.categories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_app_compose.di.DependencyInjector
import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val repository: DatabaseRepository = DependencyInjector.databaseRepository,
) : ViewModel() {
    val uiState: StateFlow<DatabaseUiState> field = MutableStateFlow(DatabaseUiState())
    private var mealsJob: Job? = null

    internal fun deleteAllMeals(onSuccess: () -> Unit = {}) =
        launchDatabaseOperation(onSuccess, repository::deleteAllMeals)

    internal fun saveMeal(meal: RandomMeal, onSuccess: () -> Unit = {}) =
        launchDatabaseOperation(onSuccess) {
            repository.saveMeal(meal)
        }

    internal fun deleteMeal(meal: RandomMeal, onSuccess: () -> Unit = {}) =
        launchDatabaseOperation(onSuccess) {
            repository.deleteMeal(meal)
        }

    internal fun retryLoadingMeals() {
        observeMeals()
    }

    private fun observeMeals() {
        mealsJob?.cancel()
        mealsJob = viewModelScope.launch {
            repository.getMeals()
                .catch { error -> updateError(error) }
                .collectLatest { meals ->
                    uiState.update { currentState ->
                        currentState.copy(
                            loading = false,
                            list = meals,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun launchDatabaseOperation(
        onSuccess: () -> Unit,
        operation: suspend () -> Unit,
    ): Job =
        viewModelScope.launch {
            try {
                operation()
                onSuccess()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                updateError(error)
            }
        }

    private fun updateError(error: Throwable) {
        uiState.update { currentState ->
            currentState.copy(
                loading = false,
                error = error.message ?: DEFAULT_DATABASE_ERROR,
            )
        }
    }

    init {
        observeMeals()
    }

    private companion object {
        const val DEFAULT_DATABASE_ERROR = "Unable to update saved dishes."
    }
}
