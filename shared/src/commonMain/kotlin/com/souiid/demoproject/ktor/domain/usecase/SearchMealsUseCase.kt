package com.souiid.demoproject.ktor.domain.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository

class SearchMealsUseCase(
    private val repository: IMealRepository
) {
    suspend operator fun invoke(query: String): List<Meal> {
        return repository.searchMeals(query)
    }
}