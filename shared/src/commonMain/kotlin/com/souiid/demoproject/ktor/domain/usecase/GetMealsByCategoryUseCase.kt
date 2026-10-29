package com.souiid.demoproject.ktor.domain.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository

class GetMealsByCategoryUseCase(
    private val repository: IMealRepository
) {
    suspend operator fun invoke(category: String): List<Meal> {
        return repository.getMealsByCategory(category)
    }
}