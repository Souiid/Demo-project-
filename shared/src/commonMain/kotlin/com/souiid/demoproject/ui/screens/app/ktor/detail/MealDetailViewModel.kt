package com.souiid.demoproject.ui.screens.app.ktor.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.usecase.GetMealByIdUseCase
import com.souiid.demoproject.room.domain.usecase.AddFavoriteMealUseCase
import com.souiid.demoproject.room.domain.usecase.GetFavoriteMealByIdUseCase
import com.souiid.demoproject.room.domain.usecase.ObserveIsFavoriteUseCase
import com.souiid.demoproject.room.domain.usecase.RemoveFavoriteMealUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealDetailViewModel(
    private val getMealByIdUseCase: GetMealByIdUseCase,
    private val getFavoriteMealByIdUseCase: GetFavoriteMealByIdUseCase,
    private val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase,
    private val addFavoriteMealUseCase: AddFavoriteMealUseCase,
    private val removeFavoriteMealUseCase: RemoveFavoriteMealUseCase
) : ViewModel() {

    private val _meal = MutableStateFlow<Meal?>(null)
    val meal = _meal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private var favoriteJob: Job? = null
    private var isUpdatingFavorite = false



    fun loadMeal(mealId: String) {
        favoriteJob?.cancel()
        _isFavorite.value = false
        favoriteJob = viewModelScope.launch {
            observeIsFavoriteUseCase(mealId).collect { _isFavorite.value = it }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _meal.value = null

            try {
                val localMeal = getFavoriteMealByIdUseCase(mealId)
                _meal.value = localMeal ?: getMealByIdUseCase(mealId)

                if (_meal.value == null) _error.value = "Meal not found"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to load meal"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun toggleFavorite() {
        val currentMeal = _meal.value ?: return
        if (isUpdatingFavorite) return

        viewModelScope.launch {
            isUpdatingFavorite = true
            try {
                if (_isFavorite.value) {
                    removeFavoriteMealUseCase(currentMeal.id)
                } else {
                    addFavoriteMealUseCase(currentMeal)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to update favorite"
            } finally {
                isUpdatingFavorite = false
            }
        }
    }
}
