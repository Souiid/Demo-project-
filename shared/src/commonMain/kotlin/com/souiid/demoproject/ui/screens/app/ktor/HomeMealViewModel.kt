package com.souiid.demoproject.ui.screens.app.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import com.souiid.demoproject.ktor.domain.usecase.GetMealsByCategoryUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeMealViewModel(
    private val getMealCategoriesUseCase: GetMealCategoriesUseCase,
    private val getMealsByCategoryUseCase: GetMealsByCategoryUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<MealCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MealCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var mealsJob: Job? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = getMealCategoriesUseCase()
                _categories.value = result
                result.firstOrNull()?.let { selectCategory(it) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: MealCategory) {
        if (_selectedCategory.value?.id == category.id) return
        _selectedCategory.value = category
        mealsJob?.cancel()
        _meals.value = emptyList()

        mealsJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _meals.value = getMealsByCategoryUseCase(category.name)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}