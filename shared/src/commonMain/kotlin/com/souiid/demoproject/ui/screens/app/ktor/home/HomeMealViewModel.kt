package com.souiid.demoproject.ui.screens.app.ktor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import com.souiid.demoproject.ktor.domain.usecase.GetMealsByCategoryUseCase
import com.souiid.demoproject.ktor.domain.usecase.SearchMealsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeMealViewModel(
    private val getMealCategoriesUseCase: GetMealCategoriesUseCase,
    private val getMealsByCategoryUseCase: GetMealsByCategoryUseCase,
    private val searchMealsUseCase: SearchMealsUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<MealCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MealCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

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
        if (_selectedCategory.value?.id == category.id && _searchQuery.value.isBlank()) return

        searchJob?.cancel()
        _searchQuery.value = ""
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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        mealsJob?.cancel()

        if (query.isBlank()) {
            _selectedCategory.value?.let {
                _selectedCategory.value = null
                selectCategory(it)
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(400.milliseconds)
            _isLoading.value = true
            _error.value = null

            try {
                _meals.value = searchMealsUseCase(query.trim())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to search meals"
            } finally {
                _isLoading.value = false
            }
        }
    }
}