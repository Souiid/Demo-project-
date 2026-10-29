package com.souiid.demoproject.ui.screens.app.ktor.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.usecase.GetMealByIdUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealDetailViewModel(
    private val getMealByIdUseCase: GetMealByIdUseCase
) : ViewModel() {

    private val _meal = MutableStateFlow<Meal?>(null)
    val meal = _meal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadMeal(mealId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _meal.value = null

            try {
                _meal.value = getMealByIdUseCase(mealId)
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
}