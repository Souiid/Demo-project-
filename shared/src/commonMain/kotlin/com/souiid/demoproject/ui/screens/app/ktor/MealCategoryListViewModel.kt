package com.souiid.demoproject.ui.screens.app.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealCategoryListViewModel(
    private val getMealCategoriesUseCase: GetMealCategoriesUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<MealCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _categories.value = getMealCategoriesUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to load categories"
            } finally {
                _isLoading.value = false
            }
        }
    }
}