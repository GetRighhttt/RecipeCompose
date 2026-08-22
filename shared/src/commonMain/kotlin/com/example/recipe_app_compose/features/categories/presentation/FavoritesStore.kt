package com.example.recipe_app_compose.features.categories.presentation

import com.example.recipe_app_compose.features.categories.domain.model.randommeal.RandomMeal
import com.example.recipe_app_compose.features.categories.domain.repository.DatabaseRepository
import com.example.recipe_app_compose.features.categories.domain.states.DatabaseUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared state holder for saved recipes. */
class FavoritesStore(
    private val repository: DatabaseRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutableUiState = MutableStateFlow(DatabaseUiState())
    val uiState: StateFlow<DatabaseUiState> = mutableUiState
    private var mealsJob: Job? = null

    fun saveMeal(meal: RandomMeal) = launchOperation { repository.saveMeal(meal) }

    fun deleteMeal(meal: RandomMeal) = launchOperation { repository.deleteMeal(meal) }

    fun deleteAllMeals() = launchOperation(repository::deleteAllMeals)

    fun retry() = observeMeals()

    fun close() = scope.cancel()

    private fun observeMeals() {
        mealsJob?.cancel()
        mealsJob = scope.launch {
            repository.getMeals()
                .catch { error -> updateError(error) }
                .collectLatest { meals ->
                    mutableUiState.value = DatabaseUiState(loading = false, list = meals)
                }
        }
    }

    private fun launchOperation(operation: suspend () -> Unit): Job = scope.launch {
        try {
            operation()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            updateError(error)
        }
    }

    private fun updateError(error: Throwable) {
        mutableUiState.update {
            it.copy(loading = false, error = error.message ?: DEFAULT_DATABASE_ERROR)
        }
    }

    init {
        observeMeals()
    }

    private companion object {
        const val DEFAULT_DATABASE_ERROR = "Unable to update saved dishes."
    }
}
