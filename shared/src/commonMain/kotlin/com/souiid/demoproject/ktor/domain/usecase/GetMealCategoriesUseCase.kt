package com.souiid.demoproject.ktor.domain.usecase

import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.repository.IMealRepository

class GetMealCategoriesUseCase(
    private val repository: IMealRepository
) {
    suspend operator fun invoke(): List<MealCategory> {
        return repository.getCategories()
    }
}