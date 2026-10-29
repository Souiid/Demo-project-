package com.souiid.demoproject.room.domain.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository

class AddFavoriteMealUseCase(
    private val repository: IFavoriteMealRepository
) {
    suspend operator fun invoke(meal: Meal) = repository.addFavorite(meal)
}
