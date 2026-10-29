package com.souiid.demoproject.ktor.domain.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository

class GetMealByIdUseCase(
    private val repository: IMealRepository
) {
    suspend operator fun invoke(mealId: String): Meal? {
        return repository.getMealById(mealId)
    }
}